/*
 * Copyright (c) 2013, Stephan Beisken. All rights reserved.
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
 */

package uk.ac.ebi.masscascade.background;

import com.google.common.collect.TreeMultimap;
import uk.ac.ebi.masscascade.core.container.file.raw.FileRawContainer;
import uk.ac.ebi.masscascade.core.raw.RawLevel;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.container.RawContainer;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.interfaces.Scan;
import uk.ac.ebi.masscascade.interfaces.Trace;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.DataUtils;
import uk.ac.ebi.masscascade.utilities.ScanUtils;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;
import uk.ac.ebi.masscascade.utilities.range.SimpleRange;
import uk.ac.ebi.masscascade.utilities.range.ToleranceRange;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;
import uk.ac.ebi.masscascade.utilities.xyz.XYZPoint;
import uk.ac.ebi.masscascade.utilities.xyz.XYZTrace;

import java.util.Iterator;
import java.util.TreeSet;

/**
 * Class implementing background reduction using a blank control template.<br />
 * <i>﻿Zhu et al., A retention-time-shift-tolerant background subtraction and noise reduction algorithm (BgS-NoRA)
 * for extraction of drug metabolites in liquid chromatography / mass spectrometry data from biological matrices.
 * (2009) Rapid Communications in Mass Spectrometry, 1563-1572</i>
 * <ul>
 * <li>Parameter <code> TIME WINDOW </code>- The time window in seconds.</li>
 * <li>Parameter <code> MZ WINDOW PPM </code>- The mass window in ppm.</li>
 * <li>Parameter <code> SCALE FACTOR </code>- The factor by which background signals are multiplied before
 * subtraction.</li>
 * <li>Parameter <code> REFERENCE RAW CONTAINER </code>- The input raw background container.</li>
 * <li>Parameter <code> RAW CONTAINER </code>- The input raw container.</li>
 * </ul>
 */
public class BackgroundSubtraction extends CallableTask {

    // task variables
    private double timeWindow;
    private double ppm;
    private double intensityScale;

    private RawContainer rawContainer;
    private RawContainer bgRawContainer;

    private TreeMultimap<Range, Trace> reference;

    private final TreeSet<Trace> traces = new TreeSet<Trace>();
    private final TreeSet<Trace> tracesExtended = new TreeSet<Trace>();

    /**
     * Constructs an empty background subtraction task. This constructor should only be used to retrieve the <code>
     * Parameter.REFERENCE_RAW_MAP </code> via the <code> getReference() </code> method.
     *
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the task fails
     */
    public BackgroundSubtraction() throws MassCascadeException {

        super(BackgroundSubtraction.class);
    }

    /**
     * Returns the reference map: m/z range to m/z trace along the time axis.
     *
     * @param bgRawContainer the background raw container
     * @return the reference map
     */
    public TreeMultimap<Range, Trace> getReference(RawContainer bgRawContainer) {

        this.bgRawContainer = bgRawContainer;
        buildReferenceMap();

        return reference;
    }

    /**
     * Constructs a background subtraction task.
     *
     * @param params the parameter map holding all required task parameters
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the task fails
     */
    public BackgroundSubtraction(ParameterMap params) throws MassCascadeException {

        super(BackgroundSubtraction.class);
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

        timeWindow = params.get(Parameter.TIME_WINDOW, Double.class);
        ppm = params.get(Parameter.MZ_WINDOW_PPM, Double.class);
        intensityScale = params.get(Parameter.SCALE_FACTOR, Double.class);
        rawContainer = params.get(Parameter.RAW_CONTAINER, RawContainer.class);

        if (params.containsKey(Parameter.REFERENCE_RAW_CONTAINER))
            bgRawContainer = params.get(Parameter.REFERENCE_RAW_CONTAINER, RawContainer.class);
        else if (params.containsKey(Parameter.REFERENCE_RAW_MAP))
            reference = params.get(Parameter.REFERENCE_RAW_MAP, TreeMultimap.class);
    }

    /**
     * Executes the task. The <code> Callable </code> returns a {@link uk.ac.ebi.masscascade.interfaces.container
     * .RawContainer} with the processed data.
     *
     * @return the raw container with the processed data
     */
    @Override
    public RawContainer call() {

        if (reference == null) buildReferenceMap();

        // prepares the new scan container
        String id = rawContainer.getId() + IDENTIFIER;
        RawContainer outRawContainer = rawContainer.getBuilder().newInstance(RawContainer.class, id, rawContainer);

        for (RawLevel level : rawContainer.getRawLevels()) {

            if (level.getMsn() == Constants.MSN.MS1) subtractBackground(outRawContainer);
            else for (Scan scan : rawContainer.iterator(level.getMsn())) outRawContainer.addScan(scan);
        }

        outRawContainer.finaliseFile(rawContainer.getRawInfo().getDate());
        return outRawContainer;
    }

    /**
     * Subtracts the background from the raw container using the <code> Parameter.REFERENCE_RAW_MAP </code>.
     *
     * @param outRawContainer the output raw container
     */
    private void subtractBackground(final RawContainer outRawContainer) {

        for (Scan scan : rawContainer) {
            double rt = scan.getRetentionTime();

            Range timeRange = new ExtendableRange(rt - timeWindow / 2d, rt + timeWindow / 2d);

            TreeSet<Trace> referenceSlice = new TreeSet<Trace>();
            for (Range range : reference.keySet()) {
                if (isOverlap(timeRange, range)) {
                    for (Trace trace : reference.get(range)) {
                        double avg = trace.getAvg();
                        Trace avgTrace = new XYZTrace(avg, ((XYZTrace) trace).getData(), avg);
                        if (referenceSlice.contains(avgTrace)) {
                            avg += 0.00000001;
                            avgTrace = new XYZTrace(avg, ((XYZTrace) trace).getData(), avg);
                        }
                        referenceSlice.add(avgTrace);
                    }
                }
            }

            XYList processedData = new XYList();

            for (XYPoint dp : scan.getData()) {

                if (referenceSlice.isEmpty()) {
                    processedData.add(dp);
                    continue;
                }

                Trace trace = new XYZTrace(dp, rt);
                Trace floorValue = referenceSlice.floor(trace);
                Trace ceilingValue = referenceSlice.higher(trace);

                if (floorValue == null && ceilingValue == null) {
                    processedData.add(dp);
                    continue;
                }

                double deltaFloor = (floorValue != null) ? (trace.getAnchor() - floorValue.getAvg()) : Double.MAX_VALUE;
                double deltaCeiling =
                        (ceilingValue != null) ? (ceilingValue.getAvg() - trace.getAnchor()) : Double.MAX_VALUE;

                XYZTrace refTrace = (XYZTrace) ((deltaFloor <= deltaCeiling) ? floorValue : ceilingValue);

                if (!(new ToleranceRange(dp.x, ppm).contains(refTrace.getAvg()))) {
                    processedData.add(dp);
                    continue;
                }

                double maxIntensity = 0;
                for (XYZPoint refDp : refTrace) {
                    if (timeRange.contains(refDp.x) && refDp.z > maxIntensity) maxIntensity = refDp.z;
                }

                double scaledIntensity = dp.y - (maxIntensity * intensityScale);
                if (scaledIntensity > 0) processedData.add(new XYPoint(dp.x, scaledIntensity));
            }

            Scan processedScan = ScanUtils.getModifiedScan(scan, processedData);
            outRawContainer.addScan(processedScan);
        }
    }

    /**
     * Tests if two ranges overlap.
     *
     * @param range1 the first range
     * @param range2 the second range
     * @return if the two ranges overlap
     */
    private boolean isOverlap(Range range1, Range range2) {
        return (range1.getLowerBounds() <= range2.getUpperBounds() && range1.getUpperBounds() >= range2
                .getLowerBounds());
    }

    /**
     * Builds the reference map to be used for background subtraction.
     */
    private void buildReferenceMap() {

        reference = TreeMultimap.create();

        for (Scan scan : bgRawContainer) {

            if (traces.isEmpty()) {
                for (XYPoint dataPoint : scan.getData())
                    traces.add(new XYZTrace(dataPoint, scan.getRetentionTime()));
                continue;
            }

            searchExistingTraces(scan);
            updateTraceMaps();
        }

        for (Trace trace : traces) reference.put(getTimeRange((XYZTrace) trace), trace);
    }

    /**
     * Takes the scan data and searches the existing traces for bins for every data point in the scan.
     *
     * @param scan the scan to be binned
     */
    private void searchExistingTraces(Scan scan) {

        XYList dataPoints = scan.getData();

        for (int signalPos = 0; signalPos < dataPoints.size(); signalPos++) {

            XYPoint signal = dataPoints.get(signalPos);
            double nextSignal =
                    (signalPos == dataPoints.size() - 1) ? Double.MAX_VALUE : dataPoints.get(signalPos + 1).x;

            XYZTrace signalTrace = new XYZTrace(signal, scan.getRetentionTime());
            XYZTrace closestTrace = (XYZTrace) DataUtils.getClosestValue(signalTrace, traces);

            // (1) signal m/z not in the map >> map empty || null
            if (closestTrace == null) addTrace(signalTrace);
                // (2) signal m/z is in the map >> exact match
            else if (traces.contains(signalTrace)) {
                if (!tracesExtended.contains(signalTrace)) appendTrace(signalTrace, closestTrace);
                // (3) signal m/z is in the map >> closest key
            } else if (Math.abs(closestTrace.getAvg() - signal.x) <= Math.abs(closestTrace.getAvg() - nextSignal)) {
                // check if the signal m/z is within the tolerance range and was not already extended
                if (new ToleranceRange(closestTrace.getAvg(), ppm).contains(signal.x) && !tracesExtended.contains(
                        closestTrace)) appendTrace(signalTrace, closestTrace);
                else addTrace(signalTrace);
                // (4) signal m/z is outside the tolerance range of the closest key in the map
            } else addTrace(signalTrace);
        }
    }

    /**
     * After every iteration, i.e., scan, the trace map are updated to eliminate non-extended profiles.
     */
    private void updateTraceMaps() {

        Iterator<Trace> iterator = traces.iterator();
        Trace trace;
        while (iterator.hasNext()) {
            trace = iterator.next();
            if (!tracesExtended.contains(trace)) {
                reference.put(getTimeRange((XYZTrace) trace), trace);
                iterator.remove();
            }
        }

        traces.addAll(tracesExtended);
        tracesExtended.clear();
    }

    /**
     * Adds a trace to the trace map.
     *
     * @param trace the trace
     */
    private void addTrace(XYZTrace trace) {
        tracesExtended.add(trace);
    }

    /**
     * Appends a trace to an existing trace.
     *
     * @param signalTrace  the new trace
     * @param closestTrace the trace to be appended to
     */
    private void appendTrace(XYZTrace signalTrace, XYZTrace closestTrace) {

        closestTrace.add(signalTrace.get(0));
        tracesExtended.add(closestTrace);
    }

    /**
     * Returns a time range for a trace based on the trace's retention time.
     *
     * @param trace the trace
     * @return the time range
     */
    private Range getTimeRange(XYZTrace trace) {
        return new SimpleRange(trace.get(0).x, trace.get(trace.size() - 1).x);
    }
}
