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

package uk.ac.ebi.masscascade.featurebuilder;

import org.apache.commons.math3.util.FastMath;
import uk.ac.ebi.masscascade.core.feature.FeatureImpl;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.*;
import uk.ac.ebi.masscascade.interfaces.Feature;
import uk.ac.ebi.masscascade.interfaces.container.FeatureContainer;
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Class implementing a feature building method where zero intensity values within the mass trace are treated as
 * feature boundaries.
 * <p/>
 * The peaks have a defined minimum and maximum feature width in the time domain. The subsequent signals are connected
 * only if the signal masses are within the set mass window. Finally, the highest signal intensity of the putative
 * feature must be greater than the set minimum signal intensity.
 * <ul>
 * <li>Parameter <code> MZ_WINDOW_PPM </code>- The mass window in ppm.</li>
 * <li>Parameter <code> MIN_FEATURE_INTENSITY </code>- The minimum feature intensity.</li>
 * <li>Parameter <code> SCAN_FILE </code>- The input scan container.</li>
 * <li>Parameter <code> MIN_FEATURE_WIDTH </code>- The minimum feature width in scans.</li>
 * </ul>
 */

public class SequentialFeatureBuilder extends CallableTask {

    private int minFeatureWidth;
    private double ppm;
    private double minIntensity;

    private ScanContainer scanContainer;
    private FeatureContainer featureContainer;

    private FeatureMsnHelper msnHelper;
    private int globalFeatureId;
    private double lastRt;
    private double currRt;

    private final TreeSet<Trace> traces = new TreeSet<>();
    private final TreeSet<Trace> tracesExtended = new TreeSet<>();

    /**
     * Constructs a feature builder task.
     *
     * @param params the parameter map holding all required task parameters
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the task fails
     */
    public SequentialFeatureBuilder(ParameterMap params) throws MassCascadeException {

        super(SequentialFeatureBuilder.class);

        globalFeatureId = 1;
        currRt = 0;
        lastRt = 0;

        setParameters(params);
    }

    /**
     * Sets the task class variables using the parameter map.
     *
     * @param params the parameter map containing the <code> Parameter </code> to <code> Object </code> relations.
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the parameter map does not contain all variables required by this class
     */
    @Override
    public void setParameters(ParameterMap params) throws MassCascadeException {

        ppm = params.get(Parameter.MZ_WINDOW_PPM, Double.class);
        minIntensity = params.get(Parameter.MIN_FEATURE_INTENSITY, Double.class);
        scanContainer = params.get(Parameter.SCAN_CONTAINER, ScanContainer.class);
        minFeatureWidth = params.get(Parameter.MIN_FEATURE_WIDTH, Integer.class);
    }

    /**
     * Executes the task. The <code> Callable </code> returns a {@link uk.ac.ebi.masscascade.interfaces.container
     * .ScanContainer} with the processed data.
     *
     * @return the scan container with the processed data
     */
    @Override
    public FeatureContainer call() {

        String id = scanContainer.getId() + IDENTIFIER;
        Constants.ION_MODE ionMode = scanContainer.getScanLevels().get(0).getIonMode();
        featureContainer = scanContainer.getBuilder().newInstance(
                FeatureContainer.class, id, ionMode, scanContainer.getWorkingDirectory());

        buildFeatures();

        featureContainer.finaliseFile();
        return featureContainer;
    }

    private void buildFeatures() {

        msnHelper = scanContainer.getMsnHelper();

        for (Scan scan : scanContainer) {

            currRt = scan.getRetentionTime();
            if (lastRt == 0) lastRt = FastMath.max(0, currRt - 1);

            if (traces.isEmpty()) {
                for (XYPoint dataPoint : scan.getData()) {
                    XYZTrace trace =
                            new XYZTrace(dataPoint, currRt, msnHelper.getChildIds(scan.getIndex(), dataPoint.x));
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
        for (Trace trace : traces) addToContainer((XYZTrace) trace);
    }

    private void searchExistingTraces(Scan scan) {

        XYList dataPoints = scan.getData();

        for (int signalPos = 0; signalPos < dataPoints.size(); signalPos++) {

            XYPoint signal = dataPoints.get(signalPos);
            double nextSignal =
                    (signalPos == dataPoints.size() - 1) ? Double.MAX_VALUE : dataPoints.get(signalPos + 1).x;

            XYZTrace signalTrace = new XYZTrace(signal, currRt, msnHelper.getChildIds(scan.getIndex(), signal.x));
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
                addToContainer((XYZTrace) trace);
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

    private void addToContainer(XYZTrace trace) {

        if (trace.size() < minFeatureWidth) return;

        Range mzRange = new ExtendableRange(trace.get(0).y);
        Feature feature = new FeatureImpl(globalFeatureId, trace.get(0), mzRange);
        for (int i = 1; i < trace.size(); i++) feature.addFeaturePoint(trace.get(i));
        feature.closeFeature(currRt);

        if (feature.getIntensity() >= minIntensity) {

            Map<Constants.MSN, Set<Integer>> msnMap = new HashMap<>();
            for (int key : trace.getMsnMap().keySet())
                // Guava cannot be handled by Kryo
                msnMap.put(Constants.MSN.get(key + 2), new HashSet<>(trace.getMsnMap().get(key)));
            feature.setMsnScans(msnMap);
            featureContainer.addFeature(feature);
            globalFeatureId++;
        }
    }
}
