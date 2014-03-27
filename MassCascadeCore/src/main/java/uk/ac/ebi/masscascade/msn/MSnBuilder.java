/*
 * Copyright (C) 2013 EMBL - European Bioinformatics Institute
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

package uk.ac.ebi.masscascade.msn;

import org.apache.commons.math3.util.FastMath;
import uk.ac.ebi.masscascade.core.feature.FeatureImpl;
import uk.ac.ebi.masscascade.core.featureset.FeatureSetImpl;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.*;
import uk.ac.ebi.masscascade.interfaces.Feature;
import uk.ac.ebi.masscascade.interfaces.container.FeatureSetContainer;
import uk.ac.ebi.masscascade.interfaces.container.ScanContainer;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.DataUtils;
import uk.ac.ebi.masscascade.utilities.comparator.PointIntensityComparator;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;
import uk.ac.ebi.masscascade.utilities.range.ToleranceRange;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;
import uk.ac.ebi.masscascade.utilities.xyz.XYZPoint;
import uk.ac.ebi.masscascade.utilities.xyz.XYZTrace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Class implementing an MSn builder method. The method compiles a representative MSn spectra for each feature that has
 * MSn references. The MSn references point to the RAW container. For each MSn level, one MSn featureset is generated,
 * only taking into account signals that are present in all MSn scans of a particular level and that are above the
 * intensity threshold.
 * <ul>
 * <li>Parameter <code> MZ_WINDOW_PPM </code>- The mass tolerance in ppm.</li>
 * <li>Parameter <code> MIN_FEATURE_INTENSITY </code>- The minimum feature intensity.</li>
 * <li>Parameter <code> SCAN_FILE </code>- The input scan container.</li>
 * <li>Parameter <code> FEATURE_SET_CONTAINER </code>- The input feature set container.</li>
 * </ul>
 */
public class MSnBuilder extends CallableTask {

    private double ppm;
    private double minIntensity;
    private int minFeatureWidth;
    private ScanContainer scanContainer;
    private FeatureSetContainer featureSetContainer;

    private int globalMsnId;
    private double lastRt;
    private double currRt;
    private TreeSet<Trace> traces;
    private List<Trace> tracesClosed;
    private TreeSet<Trace> tracesExtended;

    /**
     * Constructor for a MSn builder task.
     *
     * @param params the parameter map
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the task fails
     */
    public MSnBuilder(ParameterMap params) throws MassCascadeException {

        super(MSnBuilder.class);

        globalMsnId = 1;

        setParameters(params);
    }

    /**
     * Sets the parameters for the MSn builder task.
     *
     * @param params the new parameter values
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the task fails
     */
    public void setParameters(ParameterMap params) throws MassCascadeException {

        ppm = params.get(Parameter.MZ_WINDOW_PPM, Double.class);
        minIntensity = params.get(Parameter.MIN_FEATURE_INTENSITY, Double.class);
        scanContainer = params.get(Parameter.SCAN_CONTAINER, ScanContainer.class);
        minFeatureWidth = params.get(Parameter.MIN_FEATURE_WIDTH, Integer.class);
        featureSetContainer = params.get(Parameter.FEATURE_SET_CONTAINER, FeatureSetContainer.class);
    }

    /**
     * Executes the MSn builder task.
     *
     * @return the featureset container
     */
    @Override
    public FeatureSetContainer call() {

        String id = featureSetContainer.getId() + IDENTIFIER;
        FeatureSetContainer outFeatureSetContainer = featureSetContainer.getBuilder().newInstance(FeatureSetContainer.class, id,
                featureSetContainer.getIonMode(), featureSetContainer.getWorkingDirectory());

        for (FeatureSet featureSet : featureSetContainer) {
            for (Feature feature : featureSet) {
                if (feature.hasMsnScans()) compileMSnSpectra(feature);
            }
            outFeatureSetContainer.addFeatureSet(featureSet);
        }

        outFeatureSetContainer.finaliseFile();
        return outFeatureSetContainer;
    }

    private void compileMSnSpectra(Feature feature) {

        for (Map.Entry<Constants.MSN, Set<Integer>> entry : feature.getMsnScans().entrySet()) {

            if (entry.getKey().getLvl() < 2 || entry.getKey().getLvl() > 5) continue;

            traces = new TreeSet<>();
            tracesClosed = new ArrayList<>();
            tracesExtended = new TreeSet<>();
            currRt = 0;
            lastRt = 0;

            List<Integer> scanIds = new ArrayList<>(entry.getValue());
            Collections.sort(scanIds);
            for (int scanId : scanIds) {

                Scan scan = scanContainer.getScan(scanId);
                if (scan == null) continue;

                currRt = scan.getRetentionTime();
                if (lastRt == 0) lastRt = FastMath.max(0, currRt - 1);

                if (traces.isEmpty()) {
                    for (XYPoint dataPoint : scan.getData()) {
                        XYZTrace trace = new XYZTrace(dataPoint, currRt);
                        trace.push(new XYZPoint(lastRt, trace.get(0).y, Constants.MIN_ABUNDANCE));
                        traces.add(trace);
                    }
                    continue;
                }

                searchExistingTraces(scan);
                updateTraceMaps();

                lastRt = currRt;
            }

            currRt += 1;
            for (Trace trace : traces) {
                addToContainer(trace);
            }

            annotateFeature(feature, entry.getKey());
        }
    }

    private void searchExistingTraces(Scan scan) {

        XYList dataPoints = scan.getData();
        Collections.sort(dataPoints, new Comparator<XYPoint>() {
            @Override
            public int compare(XYPoint o1, XYPoint o2) {
                double x1 = o1.x;
                double x2 = o2.x;

                if (x1 == x2) {
                    return 0;
                } else if (x1 < x2) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        for (int signalPos = 0; signalPos < dataPoints.size(); signalPos++) {

            XYPoint signal = dataPoints.get(signalPos);

            double nextSignal =
                    (signalPos == dataPoints.size() - 1) ? Double.MAX_VALUE : dataPoints.get(signalPos + 1).x;

            XYZTrace signalTrace = new XYZTrace(signal, currRt);
            XYZTrace closestTrace = (XYZTrace) DataUtils.getClosestValue(signalTrace, traces);

            // (1) signal m/z not in the map >> map empty || null
            if (closestTrace == null) addTrace(signalTrace);
                // (2) signal m/z is in the map >> exact match
            else if (traces.contains(signalTrace)) {
                if (!tracesExtended.contains(signalTrace)) appendTrace(signalTrace, closestTrace);
                // (3) signal m/z is in the map >> closest key
            } else if (FastMath.abs(closestTrace.getAvg() - signal.x) <= FastMath.abs(closestTrace.getAvg() - nextSignal)) {
                // check if the signal m/z is within the tolerance range and was not already extended
                if (new ToleranceRange(closestTrace.getAvg(), ppm).contains(signal.x) && !tracesExtended.contains(
                        closestTrace)) appendTrace(signalTrace, closestTrace);
                else addTrace(signalTrace);
                // (4) signal m/z is outside the tolerance range of the closest key in the map
            } else addTrace(signalTrace);
        }
    }

    private void updateTraceMaps() {

        Iterator<Trace> iterator = traces.iterator();
        Trace trace;
        while (iterator.hasNext()) {
            trace = iterator.next();
            if (!tracesExtended.contains(trace)) {
                addToContainer(trace);
                iterator.remove();
            }
        }

        traces.addAll(tracesExtended);
        tracesExtended.clear();
    }

    private void addTrace(XYZTrace trace) {

        trace.push(new XYZPoint(lastRt, trace.get(0).y, Constants.MIN_ABUNDANCE));
        tracesExtended.add(trace);
    }

    private void appendTrace(XYZTrace signalTrace, XYZTrace closestTrace) {

        closestTrace.add(signalTrace.get(0), signalTrace.getMsnMap());
        tracesExtended.add(closestTrace);
    }

    private void addToContainer(Trace trace) {

        if (trace.size() <= minFeatureWidth) {
            return;
        }

        tracesClosed.add(trace);
    }

    private void annotateFeature(Feature feature, Constants.MSN msn) {

        Set<Feature> spectrumFeatures = new HashSet<>();
        XYList spectrumData = new XYList();

        for (Trace trace : tracesClosed) {

            XYZTrace xyzTrace = (XYZTrace) trace;

            Range mzRange = new ExtendableRange(xyzTrace.get(0).y);
            Feature msnFeature = new FeatureImpl(globalMsnId++, xyzTrace.get(0), mzRange);
            for (int i = 1; i < xyzTrace.size(); i++) {
                msnFeature.addFeaturePoint(xyzTrace.get(i));
            }
            msnFeature.closeFeature(xyzTrace.get(xyzTrace.size() - 1).x + 1);

            if (msnFeature.getIntensity() < minIntensity) {
                continue;
            }

            spectrumFeatures.add(msnFeature);
            spectrumData.add(msnFeature.getMzIntDp());

        }

        if (spectrumFeatures.size() == 0) {
            return;
        }

        Range rtRange = new ExtendableRange(spectrumFeatures.iterator().next().getRetentionTime());
        double rt = 0;
        for (Feature msnFeature : spectrumFeatures) rt += msnFeature.getRetentionTime();
        rt /= spectrumFeatures.size();

        FeatureSet featureSet = new FeatureSetImpl(1, spectrumData, rtRange, rt, spectrumFeatures);
        featureSet.setParent(feature.getId(), feature.getMz(), 0);
        feature.addMsnSpectrum(msn, featureSet);
    }
}
