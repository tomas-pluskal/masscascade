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

package uk.ac.ebi.masscascade.tracebuilder;

import uk.ac.ebi.masscascade.core.profile.ProfileImpl;
import uk.ac.ebi.masscascade.core.container.file.raw.FileRawContainer;
import uk.ac.ebi.masscascade.core.raw.RawLevel;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.container.ProfileContainer;
import uk.ac.ebi.masscascade.interfaces.container.RawContainer;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.interfaces.Scan;
import uk.ac.ebi.masscascade.interfaces.Trace;
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

import java.util.Iterator;
import java.util.TreeSet;

/**
 * Class implementing a profile building method where zero intensity values within the mass trace are treated as
 * profile boundaries.
 * <p/>
 * The peaks have a defined minimum and maximum profile width in the time domain. The subsequent signals are connected
 * only if the signal masses are within the set mass window. Finally, the highest signal intensity of the putative
 * profile must be greater than the set minimum signal intensity.
 * <ul>
 * <li>Parameter <code> MZ WINDOW PPM </code>- The mass window in ppm.</li>
 * <li>Parameter <code> MIN PROFILE INTENSITY </code>- The minimum profile intensity.</li>
 * <li>Parameter <code> RAW FILE </code>- The input raw container.</li>
 * <li>Parameter <code> MIN PROFILE WIDTH </code>- The minimum profile width in scans.</li>
 * </ul>
 */

public class ProfileBuilder extends CallableTask {

    private int minProfileWidth;
    private double ppm;
    private double minIntensity;

    private RawContainer rawContainer;
    private ProfileContainer profileContainer;

    private int globalProfileId;
    private double lastRt;
    private double currRt;

    private final TreeSet<Trace> traces = new TreeSet<Trace>();
    private final TreeSet<Trace> tracesExtended = new TreeSet<Trace>();

    /**
     * Constructor for a profile builder task.
     *
     * @param params the parameter map
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public ProfileBuilder(ParameterMap params) throws MassCascadeException {

        super(ProfileBuilder.class);

        globalProfileId = 1;
        currRt = 0;
        lastRt = 0;

        setParameters(params);
    }

    /**
     * Sets the parameters for the profile builder task.
     *
     * @param params the new parameter values
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    private void setParameters(ParameterMap params) throws MassCascadeException {

        ppm = params.get(Parameter.MZ_WINDOW_PPM, Double.class);
        minIntensity = params.get(Parameter.MIN_PROFILE_INTENSITY, Double.class);
        rawContainer = params.get(Parameter.RAW_CONTAINER, FileRawContainer.class);
        minProfileWidth = params.get(Parameter.MIN_PROFILE_WIDTH, Integer.class);
    }

    /**
     * Executes the mass builder task.
     *
     * @return the profile container
     */
    public ProfileContainer call() {

        String id = rawContainer.getId() + IDENTIFIER;
        profileContainer = rawContainer.getBuilder().newInstance(ProfileContainer.class, id, rawContainer.getWorkingDirectory());

        for (RawLevel level : rawContainer.getRawLevels()) {

            if (level.getMsn() == Constants.MSN.MS1) buildProfiles();
            else ; // do nothing for the time being
        }

        profileContainer.finaliseFile();
        return profileContainer;
    }

    private void buildProfiles() {

        for (Scan scan : rawContainer) {

            currRt = scan.getRetentionTime();
            if (lastRt == 0) lastRt = Math.max(0, currRt - 1);

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
        for (Trace trace : traces) addToContainer((XYZTrace) trace);
    }

    private void searchExistingTraces(Scan scan) {

        XYList dataPoints = scan.getData();

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
            } else if (Math.abs(closestTrace.getAvg() - signal.x) <= Math.abs(closestTrace.getAvg() - nextSignal)) {
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

        closestTrace.add(signalTrace.get(0));
        tracesExtended.add(closestTrace);
    }

    private void addToContainer(XYZTrace trace) {

        if (trace.size() < minProfileWidth) return;

        Range mzRange = new ExtendableRange(trace.get(0).y);
        Profile profile = new ProfileImpl(globalProfileId, trace.get(0), mzRange);
        for (int i = 1; i < trace.size(); i++) profile.addProfilePoint(trace.get(i));
        profile.closeProfile(currRt);

        if (profile.getIntensity() >= minIntensity) {
            profileContainer.addProfile(profile);
            globalProfileId++;
        }
    }
}
