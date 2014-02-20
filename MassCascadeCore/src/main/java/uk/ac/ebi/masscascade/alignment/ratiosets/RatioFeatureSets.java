/*
 * Copyright (C) 2014 EMBL - European Bioinformatics Institute
 *
 * This file is part of MassCascade.
 *
 * MassCascade is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MassCascade is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MassCascade. If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 *   Stephan Beisken - initial API and implementation
 */
package uk.ac.ebi.masscascade.alignment.ratiosets;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.util.FastMath;
import uk.ac.ebi.masscascade.core.feature.FeatureImpl;
import uk.ac.ebi.masscascade.core.featureset.FeatureSetImpl;
import uk.ac.ebi.masscascade.interfaces.*;
import uk.ac.ebi.masscascade.interfaces.container.Container;
import uk.ac.ebi.masscascade.interfaces.container.FeatureSetContainer;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.DataUtils;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;
import uk.ac.ebi.masscascade.utilities.range.SimpleRange;
import uk.ac.ebi.masscascade.utilities.range.ToleranceRange;
import uk.ac.ebi.masscascade.utilities.xyz.*;

import java.util.*;

/**
 * Class ...
 * <p/>
 * <ul>
 * <li>Parameter <code>  </code>- .</li>
 * </ul>
 */
public class RatioFeatureSets extends CallableTask {

    private List<FeatureSetContainer> featureSetContainers;

    private double r;
    private double bvNoise;
    private List<XYPoint> basePeakChromatogram;

    private static final double PPM = 10.0;

    /**
     * Constructs an ratio feature set task.
     *
     * @param params the parameter map holding all required task parameters
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException if the task fails
     */
    public RatioFeatureSets(ParameterMap params) {

        super(RatioFeatureSets.class);
        setParameters(params);
    }

    /**
     * Sets the task class variables using the parameter map.
     *
     * @param params the parameter map containing the <code> Parameter </code> to <code> Object </code> relations.
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException if the parameter map does not contain all variables
     *                                                              required by this class
     */
    @Override
    @SuppressWarnings("unchecked")
    public void setParameters(ParameterMap params) {

        this.featureSetContainers = params.get(Parameter.FEATURE_SET_LIST, ArrayList.class);
    }

    /**
     * Executes the task. The <code> Callable </code> returns a {@link uk.ac.ebi.masscascade.interfaces.container
     * .FeatureSetContainer} with the processed data.
     *
     * @return the feature container with the processed data
     */
    @Override
    public Container call() {

        // generate master base peak chromatogram
        basePeakChromatogram = new ArrayList<>();
        for (FeatureSetContainer container : featureSetContainers) {
            basePeakChromatogram.addAll(container.getBasePeaks());
        }
        // sort by time
        Collections.sort(basePeakChromatogram);

        // set initial boundaries
        int bl = 0;
        int br = basePeakChromatogram.size() - 1;

        // final result list of boundaries
        List<Double> boundsFinal = new ArrayList<>();

        // determine resolution {0,0.5} in 0.05 increments
        for (r = 0.1; r <= 0.5; r += 0.05) {

            // determine noise bin value
            bvNoise = 0;
            double[] bvNoiseMax = new double[1];
            traverseNoise(bl, br, iMax(bl, br), bvNoiseMax);
            bvNoise = bvNoiseMax[0] / featureSetContainers.size();

            // result list of boundaries
            List<Double> bounds = new ArrayList<>();

            // start adaptive binning
            traverse(bl, br, iMax(0, br), bounds);

            // select final bounds list based on total number of generated size
            // best trade-off between the resolution and noise bin value
            if (bounds.size() > boundsFinal.size()) {
                boundsFinal = bounds;
            }
        }

        // add first and last boundary and sort list
        boundsFinal.add(basePeakChromatogram.get(0).x);
        boundsFinal.add(basePeakChromatogram.get(basePeakChromatogram.size() - 1).x);
        Collections.sort(boundsFinal);

        // build new feature set container
        FeatureSetContainer fsc = featureSetContainers.get(0);
        String id = fsc.getId() + IDENTIFIER;
        FeatureSetContainer fscOut = fsc.getBuilder().newInstance(FeatureSetContainer.class, id,
                fsc.getIonMode(), fsc.getWorkingDirectory());

        // apply ratio consensus compilation
        ratioFeatureSets(fscOut, boundsFinal);

        fscOut.finaliseFile();
        return fscOut;
    }

    class FeatureMapper {

        private final TreeSet<Trace>[] binToFeatures;

        private FeatureMapper(int capacity) {

            binToFeatures = new TreeSet[capacity];
        }

        private void add(int bin, Feature feature, double maxIntensity) {

            double intensityRatio = feature.getIntensity() / maxIntensity;
            Trace trace = new ExXYZTrace(feature.getMz(), maxIntensity, intensityRatio, feature.getRetentionTime());
            if (binToFeatures[bin] == null) {
                TreeSet<Trace> traceTreeSet = new TreeSet<>();
                traceTreeSet.add(trace);
                binToFeatures[bin] = traceTreeSet;
            } else {

                TreeSet<Trace> traceTreeSet = binToFeatures[bin];
                ExXYZTrace closest = (ExXYZTrace) DataUtils.getClosestValue(trace, traceTreeSet);

                if (closest != null && new ToleranceRange(closest.getAvg(), PPM).contains(trace.getAnchor())) {
                    closest.add(new XYZPoint(feature.getRetentionTime(), feature.getMz(), maxIntensity), intensityRatio);
                } else {
                    binToFeatures[bin].add(trace);
                }
            }
        }

        private TreeSet<Trace> get(int bin) {

            if (binToFeatures[bin] == null) {
                return new TreeSet<>();
            } else {
                return binToFeatures[bin];
            }
        }
    }

    private void ratioFeatureSets(FeatureSetContainer fscOut, List<Double> boundsFinal) {

        FeatureMapper mapper = new FeatureMapper(boundsFinal.size() - 1);

        int[] fids = new int[boundsFinal.size() - 1];
        for (FeatureSetContainer fsc : featureSetContainers) {
            for (FeatureSet fs : fsc) {

                int x;
                for (x = 1; x < boundsFinal.size(); x++) {
                    if (fs.getRetentionTime() <= boundsFinal.get(x)) {
                        break;
                    }
                }
                for (Feature f : fs) {
                    int fid = fids[x - 1];
                    f.setId(fid++);
                    mapper.add(x - 1, f, fs.getBasePeak().get(0).y);
                    fids[x - 1] = fid;
                }
            }
        }

        int gid = 0;
        for (int bin = 0; bin < boundsFinal.size() - 1; bin++) {

            TreeSet<Trace> traceTreeSet = mapper.get(bin);
            if (traceTreeSet.size() == 0) {
                continue;
            }

            int max = 0;
            for (Trace trace : traceTreeSet) {
                int size = trace.size();
                if (max < size) {
                    max = size;
                }
            }

            Range range = null;
            double gRt = 0;
            XYList xyList = new XYList();
            Set<Feature> features = new HashSet<>();
            for (Trace trace : traceTreeSet) {
                ExXYZTrace exTrace = (ExXYZTrace) trace;
                if (exTrace.size() == max) {
                    double avgMaxInt = exTrace.getAvgInt();
                    double avgIntRatio = exTrace.getAvgIntRatio();
                    double sigma = exTrace.getStdDevIntRatio();

                    double mz = exTrace.getAvg();
                    double rt = exTrace.getAvgRt();
                    double intensity = avgMaxInt * (avgIntRatio / sigma);

                    System.out.println("Sigma: " + sigma);
                    System.out.println(intensity);

                    if (range == null) {
                        range = new ExtendableRange(rt);
                    } else {
                        range.extendRange(rt);
                    }
                    gRt += rt;
                    xyList.add(new XYPoint(mz, intensity));

                    Feature feature = new FeatureImpl(gid++, new XYZPoint(rt, mz, intensity), new SimpleRange(mz, mz));
                    features.add(feature);
                }
            }

            fscOut.addFeatureSet(new FeatureSetImpl(bin, xyList, range, gRt / xyList.size(), features));
        }
    }

    private void traverseNoise(int bl, int br, double iMax, double[] bvNoiseMax) {

        double il = basePeakChromatogram.get(bl).y;
        double ir = basePeakChromatogram.get(br).y;

        int bvMaxI = 0;
        double[] bvMax = new double[3];

        double bvBase = FastMath.pow((iMax - il) * (iMax - ir), r);

        for (int b = bl + 1; b < br; b++) {

            double i = basePeakChromatogram.get(b).y;

            double bvl = FastMath.pow((iMax - il) * (iMax - i), r);
            double bvr = FastMath.pow((iMax - i) * (iMax - ir), r);

            if (bvl + bvr > bvMax[0]) {
                bvMax[0] = bvl + bvr;
                bvMax[1] = bvl;
                bvMax[2] = bvr;
                bvMaxI = b;
            }
        }

        if (bvMax[0] > bvBase && bvMax[1] > bvNoise && bvMax[2] > bvNoise) {

            double bvMaxS = FastMath.max(bvMax[1], bvMax[2]);
            if (bvNoiseMax[0] < bvMaxS) {
                bvNoiseMax[0] = bvMaxS;
            }

            traverseNoise(bl, bvMaxI, iMax(bl, bvMaxI), bvNoiseMax);
            traverseNoise(bvMaxI, br, iMax(bvMaxI, br), bvNoiseMax);
        }
    }

    private void traverse(int bl, int br, double iMax, List<Double> bounds) {

        double il = basePeakChromatogram.get(bl).y;
        double ir = basePeakChromatogram.get(br).y;

        int bvMaxI = 0;
        double[] bvMax = new double[3];

        double bvBase = FastMath.pow((iMax - il) * (iMax - ir), r);

        for (int b = bl + 1; b < br; b++) {

            double i = basePeakChromatogram.get(b).y;

            double bvl = FastMath.pow((iMax - il) * (iMax - i), r);
            double bvr = FastMath.pow((iMax - i) * (iMax - ir), r);

            if (bvl + bvr > bvMax[0]) {
                bvMax[0] = bvl + bvr;
                bvMax[1] = bvl;
                bvMax[2] = bvr;
                bvMaxI = b;
            }
        }

        if (bvMax[0] > bvBase && bvMax[1] > bvNoise && bvMax[2] > bvNoise) {

            bounds.add(basePeakChromatogram.get(bvMaxI).x);

            traverse(bl, bvMaxI, iMax(bl, bvMaxI), bounds);
            traverse(bvMaxI, br, iMax(bvMaxI, br), bounds);
        }
    }

    private double iMax(int bl, int br) {

        double iMax = 0;
        for (int b = bl; b <= br; b++) {
            if (iMax < basePeakChromatogram.get(b).y) {
                iMax = basePeakChromatogram.get(b).y;
            }
        }
        return iMax;
    }
}