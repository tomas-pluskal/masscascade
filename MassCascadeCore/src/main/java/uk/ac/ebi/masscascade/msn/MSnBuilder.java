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
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;
import uk.ac.ebi.masscascade.utilities.range.ToleranceRange;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;
import uk.ac.ebi.masscascade.utilities.xyz.XYZPoint;
import uk.ac.ebi.masscascade.utilities.xyz.XYZTrace;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
    private ScanContainer scanContainer;
    private FeatureSetContainer featureSetContainer;

    private int globalMsnId = 1;
    private final TreeMap<Trace, Boolean> traceToExtended = new TreeMap<>();

    /**
     * Constructor for a MSn builder task.
     *
     * @param params the parameter map
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the task fails
     */
    public MSnBuilder(ParameterMap params) throws MassCascadeException {

        super(MSnBuilder.class);
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

            traceToExtended.clear();

            for (int scanId : entry.getValue()) {
                Scan scan = scanContainer.getScan(scanId);
                if (scan == null) continue;

                double rt = scan.getRetentionTime();
                XYList scanDps = scan.getData();
                for (int i = 0; i < scanDps.size(); i++) {

                    XYPoint currentDp = scanDps.get(i);
                    double nextDpMz = (currentDp == scanDps.getLast()) ? Double.MAX_VALUE : scanDps.get(i + 1).x;

                    XYZTrace signalTrace = new XYZTrace(currentDp, rt);
                    XYZTrace closestTrace = (XYZTrace) DataUtils.getClosestKey(signalTrace, traceToExtended);

                    if (closestTrace == null) addTrace(signalTrace);
                    else if (traceToExtended.containsKey(signalTrace)) {
                        if (!traceToExtended.get(signalTrace)) appendTrace(signalTrace, closestTrace);
                    } else if (isCloserThanNext(closestTrace.getAvg(), currentDp.x, nextDpMz)) {
                        if (isWithinParameter(closestTrace.getAvg(), currentDp.x, traceToExtended.get(closestTrace)))
                            appendTrace(signalTrace, closestTrace);
                        else addTrace(signalTrace);
                    } else addTrace(signalTrace);
                }

                Iterator<Map.Entry<Trace, Boolean>> iter = traceToExtended.entrySet().iterator();
                while (iter.hasNext()) iter.next().setValue(false);
            }

            annotateFeature(feature, entry.getKey(), traceToExtended);
        }
    }

    private boolean isCloserThanNext(double avg, double curMz, double nextMz) {
        return FastMath.abs(avg - curMz) <= FastMath.abs(avg - nextMz);
    }

    private boolean isWithinParameter(double avg, double mz, boolean extended) {
        return (new ToleranceRange(avg, ppm).contains(mz) && !extended);
    }

    private void addTrace(XYZTrace trace) {

        XYZPoint dp = trace.getData().get(0);
        trace.push(new XYZPoint(dp.x - 1, dp.y, Constants.MIN_ABUNDANCE));
        traceToExtended.put(trace, true);
    }

    private void appendTrace(XYZTrace signalTrace, XYZTrace closestTrace) {

        closestTrace.add(signalTrace.get(0));
        traceToExtended.put(closestTrace, true);
    }

    private void annotateFeature(Feature feature, Constants.MSN msn, TreeMap<Trace, Boolean> traceToExtended) {

        Set<Feature> spectrumFeatures = new HashSet<>();
        XYList spectrumData = new XYList();

        int totalMsnScans = feature.getMsnScans().get(msn).size();
        for (Trace trace : traceToExtended.keySet()) {
            if (trace.size() - 1 != totalMsnScans) continue;

            XYZTrace xyzTrace = (XYZTrace) trace;

            Range mzRange = new ExtendableRange(xyzTrace.get(0).y);
            Feature msnFeature = new FeatureImpl(globalMsnId++, xyzTrace.get(0), mzRange);
            for (int i = 1; i < trace.size(); i++) msnFeature.addFeaturePoint(xyzTrace.get(i));
            msnFeature.closeFeature(((XYZTrace) trace).get(trace.size() - 1).x + 1);

            if (msnFeature.getIntensity() < minIntensity) continue;

            spectrumFeatures.add(msnFeature);
            spectrumData.add(msnFeature.getMzIntDp());
        }

        if (spectrumFeatures.size() == 0) return;

        Range rtRange = new ExtendableRange(spectrumFeatures.iterator().next().getRetentionTime());
        double rt = 0;
        for (Feature msnFeature : spectrumFeatures) rt += msnFeature.getRetentionTime();
        rt /= spectrumFeatures.size();

        FeatureSet featureSet = new FeatureSetImpl(1, spectrumData, rtRange, rt, spectrumFeatures);
        featureSet.setParent(feature.getId(), feature.getMz(), 0);
        feature.addMsnSpectrum(msn, featureSet);
    }
}
