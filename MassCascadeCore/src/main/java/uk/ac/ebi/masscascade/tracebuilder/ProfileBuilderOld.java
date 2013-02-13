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

import uk.ac.ebi.masscascade.core.profile.ProfileContainer;
import uk.ac.ebi.masscascade.core.profile.ProfileImpl;
import uk.ac.ebi.masscascade.core.raw.RawContainer;
import uk.ac.ebi.masscascade.core.raw.RawLevel;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.ACallableTask;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.interfaces.Scan;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;
import uk.ac.ebi.masscascade.utilities.xyz.YMinPoint;
import uk.ac.ebi.masscascade.utilities.math.MathUtils;
import uk.ac.ebi.masscascade.utilities.range.MovingRange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class ProfileBuilderOld extends ACallableTask {

    private int minProfileWidth;
    private double ppm;
    private double minIntensity;

    private RawContainer rawContainer;
    private ProfileContainer profileContainer;

    private int globalProfileId;
    private double lastRetentionTime;

    private int profileRangesIndex;
    private List<Range> profileRanges;
    private List<Range> profileRangesActive;

    private final Map<Range, Profile> profileMap;

    /**
     * Constructor for a profile builder task.
     *
     * @param params the parameter map
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public ProfileBuilderOld(ParameterMap params) throws MassCascadeException {

        super(ProfileBuilderOld.class);

        globalProfileId = 1;
        lastRetentionTime = -1;
        profileRanges = new ArrayList<Range>();
        profileRangesActive = new ArrayList<Range>();
        profileMap = new HashMap<Range, Profile>();

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
        rawContainer = params.get(Parameter.RAW_CONTAINER, RawContainer.class);
        minProfileWidth = params.get(Parameter.MIN_PROFILE_WIDTH, Integer.class);
    }

    /**
     * Executes the mass builder task.
     *
     * @return the profile container
     */
    public ProfileContainer call() {

        String id = rawContainer.getId() + IDENTIFIER;
        profileContainer = new ProfileContainer(id, rawContainer.getWorkingDirectory());

        for (RawLevel level : rawContainer.getRawLevels()) {

            if (level.getMsn() == Constants.MSN.MS1) buildProfiles();
            else ; // do nothing for the time being
        }

        profileContainer.finaliseFile();
        return profileContainer;
    }

    /**
     * Builds the profiles.
     */
    public void buildProfiles() {

        XYList scanData;
        double retentionTime;

        for (Scan scan : rawContainer) {

            if (scan.getData().isEmpty()) {
                closeFinishedProfiles(scan.getRetentionTime());
                resetProfileParameters();
                lastRetentionTime = scan.getRetentionTime();
                continue;
            }

            if (profileMap.isEmpty()) {
                initProfileMap(scan);
                resetProfileParameters();
                continue;
            }

            retentionTime = scan.getRetentionTime();

            scanData = scan.getData();

            for (int scanDataIndex = 0; scanDataIndex < scanData.size() - 1; scanDataIndex++) {
                categoriseScanDataPoint(scanData, scanDataIndex, retentionTime);
            }
            categoriseLastScanDataPoint(scanData, scanData.size() - 1, retentionTime);

            closeFinishedProfiles(retentionTime);
            resetProfileParameters();

            lastRetentionTime = retentionTime;
        }

        closeFinishedProfiles(lastRetentionTime + 1);
    }

    private void categoriseScanDataPoint(XYList scanData, int scanDataIndex, double retentionTime) {

        categorise(scanData, scanDataIndex, scanDataIndex + 1, retentionTime);
    }

    private void categoriseLastScanDataPoint(XYList scanData, int scanDataIndex, double retentionTime) {

        categorise(scanData, scanDataIndex, scanDataIndex, retentionTime);
    }

    /**
     * Appends the data point to an existing trace or adds the data point as new trace origin.
     *
     * @param scanData      the scan data
     * @param scanDataIndex the current scan data index
     * @param retentionTime the retention time
     */
    private void categorise(XYList scanData, int scanDataIndex, int nextScanDataIndex, double retentionTime) {

        XYPoint scanDataPoint = scanData.get(scanDataIndex);
        double mz = scanDataPoint.x;

        if (profileRangesIndex >= profileRanges.size()) {
            addProfile(scanDataPoint, retentionTime);
            return;
        }

        Range profileRange = profileRanges.get(profileRangesIndex);
        Range nextProfileRange = (profileRangesIndex < profileRanges.size() - 1) ? profileRanges.get(
                profileRangesIndex + 1) : profileRange;

        if (mz < profileRange.getLowerBounds()) addProfile(scanDataPoint, retentionTime);
        else if (mz >= profileRange.getUpperBounds()) {
            profileRangesIndex++;
            categorise(scanData, scanDataIndex, nextScanDataIndex, retentionTime);
        } else {
            double nextMz = scanData.get(nextScanDataIndex).x;
            if (mz == profileRange.getClosest(mz, nextMz)) {
                if (MathUtils.isAbsClosest(mz, profileRange.getMean(), nextProfileRange.getMean()))
                    if (!profileRangesActive.contains(profileRange))
                        appendToProfile(scanDataPoint, retentionTime, profileRange);
                    else addProfile(scanDataPoint, retentionTime);
                else {
                    if (MathUtils.isAbsClosest(nextProfileRange.getMean(), mz, nextMz)) {
                        profileRangesIndex++;
                        categorise(scanData, scanDataIndex, nextScanDataIndex, retentionTime);
                    } else {
                        if (!profileRangesActive.contains(profileRange)) addProfile(scanDataPoint, retentionTime);
                        else appendToProfile(scanDataPoint, retentionTime, profileRange);
                    }
                }
            } else {
                if (MathUtils.isAbsClosest(nextMz, profileRange.getMean(), nextProfileRange.getMean()))
                    addProfile(scanDataPoint, retentionTime);
                else appendToProfile(scanDataPoint, retentionTime, profileRange);
            }
        }
    }

    /**
     * Instantiates the profile map if it is empty.
     *
     * @param scan the current scan
     */
    private void initProfileMap(Scan scan) {

        double retentionTime = scan.getRetentionTime();

        if (lastRetentionTime == -1) lastRetentionTime = Math.max(0, retentionTime - 1);
        for (XYPoint dataPoint : scan.getData()) addProfile(dataPoint, retentionTime);

        profileRangesIndex = 0;
        Collections.sort(profileRanges);
    }

    /**
     * Adds a new profile to the profile map.
     *
     * @param scanDataPoint the scan mz-signal data point
     * @param retentionTime the retention time
     */
    private void addProfile(XYPoint scanDataPoint, double retentionTime) {

        Range mzRange = new MovingRange(globalProfileId, scanDataPoint.x, ppm);

        Profile profile = new ProfileImpl(globalProfileId, new YMinPoint(scanDataPoint.x), lastRetentionTime, mzRange);
        profile.addProfilePoint(scanDataPoint, retentionTime);

        profileMap.put(mzRange, profile);
        profileRangesActive.add(mzRange);

        globalProfileId++;
    }

    /**
     * Appends a scan mz-intensity data point to an existing profile.
     *
     * @param scanDataPoint the scan data point
     * @param retentionTime the retention time
     * @param profileRange  the profile range
     */
    private void appendToProfile(XYPoint scanDataPoint, double retentionTime, Range profileRange) {

        profileRangesIndex++;

        profileRange.extendRange(scanDataPoint.x);
        Profile profile = profileMap.get(profileRange);
        profile.addProfilePoint(scanDataPoint, retentionTime);

        profileMap.put(profileRange, profile);
        profileRangesActive.add(profileRange);
    }

    /**
     * Resets all global parameters before commencing another scan iteration.
     */
    private void resetProfileParameters() {

        profileRanges = null;
        profileRanges = profileRangesActive;

        profileRangesActive = new ArrayList<Range>();

        Collections.sort(profileRanges);
        profileRangesIndex = 0;
    }

    /**
     * Closes all inactive, non-extended, profiles and adds them to the output data container.
     *
     * @param retentionTime the closing retention time
     */
    private void closeFinishedProfiles(double retentionTime) {

        profileRanges.removeAll(profileRangesActive);

        Profile finishedProfile;
        for (Range profileRange : profileRanges) {

            finishedProfile = profileMap.get(profileRange);
            if (finishedProfile.getMzData().size() - 1 >= minProfileWidth) {
                finishedProfile.closeProfile(retentionTime);
                if (finishedProfile.getMzIntDp().y >= minIntensity) profileContainer.addProfile(finishedProfile);
            }

            profileMap.remove(profileRange);
        }
    }
}
