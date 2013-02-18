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

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import uk.ac.ebi.masscascade.core.container.file.raw.FileRawContainer;
import uk.ac.ebi.masscascade.core.raw.RawLevel;
import uk.ac.ebi.masscascade.core.raw.ScanImpl;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.container.RawContainer;
import uk.ac.ebi.masscascade.interfaces.Scan;
import uk.ac.ebi.masscascade.interfaces.Trace;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.DataUtils;
import uk.ac.ebi.masscascade.utilities.xyz.XYTrace;
import uk.ac.ebi.masscascade.utilities.range.ToleranceRange;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class for reducing random noise.
 * <p/>
 * For every m/z in every scan, the algorithm checks m/z values in subsequent scans for similar values within the
 * tolerance window. Only values that are part of a m/z trace exceeding the minimum trace width are kept.
 * <ul>
 * <li>Parameter <code> SCAN WINDOW </code>- The scan window in scans.</li>
 * <li>Parameter <code> MZ WINDOW PPM </code>- The mass window in ppm.</li>
 * <li>Parameter <code> RAW CONTAINER </code>- The input raw container.</li>
 * </ul>
 */
public class NoiseReduction extends CallableTask {

    // task variables
    private int minTraceWidth;
    private double ppm;

    private RawContainer rawContainer;

    // m/z trace -> scan index
    private final TreeMap<Trace, Integer> traces = new TreeMap<Trace, Integer>();
    private final TreeMap<Trace, Integer> tracesExtended = new TreeMap<Trace, Integer>();

    /**
     * Constructs a noise reduction task.
     *
     * @param params the parameter map
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public NoiseReduction(ParameterMap params) throws MassCascadeException {

        super(NoiseReduction.class);

        setParameters(params);
    }

    /**
     * Sets the parameters attribute for the noise reduction task.
     *
     * @param params the parameter values
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public void setParameters(ParameterMap params) throws MassCascadeException {

        minTraceWidth = params.get(Parameter.SCAN_WINDOW, Integer.class);
        ppm = params.get(Parameter.MZ_WINDOW_PPM, Double.class);
        rawContainer = params.get(Parameter.RAW_CONTAINER, RawContainer.class);
    }

    /**
     * Reduces random noise in the mass spec sample.
     *
     * @return the processed sample file
     */
    public RawContainer call() {

        String id = rawContainer.getId() + IDENTIFIER;
        RawContainer outRawContainer =
                rawContainer.getBuilder().newInstance(RawContainer.class, id, rawContainer.getWorkingDirectory());

        Multimap<Integer, XYTrace> completedTraces = TreeMultimap.create();

        for (RawLevel level : rawContainer.getRawLevels()) {

            if (level.getMsn() == Constants.MSN.MS1) {
                buildTraceMap(completedTraces);
                buildContainer(completedTraces, outRawContainer);
            } else {
                for (Scan scan : rawContainer.iterator(level.getMsn())) outRawContainer.addScan(scan);
            }
        }

        outRawContainer.finaliseFile(rawContainer.getRawInfo().getDate());
        return outRawContainer;
    }

    private void buildTraceMap(Multimap<Integer, XYTrace> completedTraces) {

        for (Scan scan : rawContainer) {

            if (traces.isEmpty()) {
                for (XYPoint dataPoint : scan.getData())
                    traces.put(new XYTrace(dataPoint), scan.getIndex());
                continue;
            }

            searchExistingTraces(scan);
            updateTraceMaps(completedTraces);
        }

        for (Map.Entry<Trace, Integer> entry : traces.entrySet())
            if (entry.getKey().size() >= minTraceWidth) completedTraces.put(entry.getValue(), (XYTrace) entry.getKey());
    }

    private void searchExistingTraces(Scan scan) {

        XYList dataPoints = scan.getData();

        for (int signalPos = 0; signalPos < dataPoints.size(); signalPos++) {

            XYPoint signal = dataPoints.get(signalPos);
            double nextSignal =
                    (signalPos == dataPoints.size() - 1) ? Double.MAX_VALUE : dataPoints.get(signalPos + 1).x;

            XYTrace signalTrace = new XYTrace(signal);
            XYTrace closestTrace = (XYTrace) DataUtils.getClosestKey(signalTrace, traces);

            // (1) signal m/z not in the map >> map empty || null
            if (closestTrace == null) addTrace(signalTrace, scan.getIndex());
                // (2) signal m/z is in the map >> exact match
            else if (traces.containsKey(signalTrace)) {
                if (!tracesExtended.containsKey(signalTrace)) appendTrace(signalTrace, closestTrace);
                // (3) signal m/z is in the map >> closest key
            } else if (Math.abs(closestTrace.getAvg() - signal.x) <= Math.abs(closestTrace.getAvg() - nextSignal)) {
                // check if the signal m/z is within the tolerance range and was not already extended
                if (new ToleranceRange(closestTrace.getAvg(), ppm).contains(signal.x) && !tracesExtended.containsKey(
                        closestTrace)) appendTrace(signalTrace, closestTrace);
                else addTrace(signalTrace, scan.getIndex());
                // (4) signal m/z is outside the tolerance range of the closest key in the map
            } else addTrace(signalTrace, scan.getIndex());
        }
    }

    private void updateTraceMaps(Multimap<Integer, XYTrace> completedTraces) {

        Iterator<Map.Entry<Trace, Integer>> iterator = traces.entrySet().iterator();
        Map.Entry<Trace, Integer> entry;
        while (iterator.hasNext()) {
            entry = iterator.next();
            if (!tracesExtended.containsKey(entry.getKey())) {
                if (entry.getKey().size() >= minTraceWidth)
                    completedTraces.put(entry.getValue(), (XYTrace) entry.getKey());
                iterator.remove();
            }
        }

        traces.putAll(tracesExtended);
        tracesExtended.clear();
    }

    private void buildContainer(Multimap<Integer, XYTrace> completedTraces, RawContainer outRawContainer) {

        Map<XYTrace, Integer> activeTraces = new HashMap<XYTrace, Integer>();

        int i;
        XYTrace trace;
        XYList processedData;
        Iterator<Map.Entry<XYTrace, Integer>> iterator;
        for (Scan scan : rawContainer) {

            processedData = new XYList();

            iterator = activeTraces.entrySet().iterator();
            while (iterator.hasNext()) {
                trace = iterator.next().getKey();
                i = activeTraces.get(trace);
                processedData.add(trace.get(i));
                i++;
                if (i < trace.size()) activeTraces.put(trace, i);
                else iterator.remove();
            }

            if (completedTraces.containsKey(scan.getIndex())) {
                for (XYTrace completedTrace : completedTraces.get(scan.getIndex())) {
                    processedData.add(completedTrace.get(0));
                    if (completedTrace.size() > 1) activeTraces.put(completedTrace, 1);
                }
            }

            if (processedData.size() == 0) continue;
            Collections.sort(processedData);

            Scan processedScan = new ScanImpl(scan.getIndex(), scan.getMsn(), scan.getIonMode(), processedData,
                    scan.getRetentionTime(), scan.getParentScan(), scan.getParentCharge(), scan.getParentMz());
            outRawContainer.addScan(processedScan);
            processedScan = null;
        }
        activeTraces = null;
    }

    private void addTrace(XYTrace trace, int scanId) {
        tracesExtended.put(trace, scanId);
    }

    private void appendTrace(XYTrace signalTrace, XYTrace closestTrace) {

        closestTrace.add(signalTrace.get(0));
        tracesExtended.put(closestTrace, traces.get(closestTrace));
    }
}