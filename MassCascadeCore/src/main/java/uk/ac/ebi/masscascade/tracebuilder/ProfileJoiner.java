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

import com.google.common.collect.TreeMultimap;
import uk.ac.ebi.masscascade.core.container.file.profile.FileProfileContainer;
import uk.ac.ebi.masscascade.core.profile.ProfileImpl;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.container.ProfileContainer;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.DataUtils;
import uk.ac.ebi.masscascade.utilities.xyz.XYZPoint;
import uk.ac.ebi.masscascade.utilities.range.ToleranceRange;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Class implementing a mass trace compilation method that builds a mass trace from a set of profiles.
 * <p/>
 * All profiles that fall within the defined mass range are grouped together and form the mass trace.
 * <ul>
 * <li>Parameter <code> MZ WINDOW PPM </code>- The mass window in ppm.</li>
 * <li>Parameter <code> PROFILE FILE </code>- The input profile container.</li>
 * </ul>
 */
public class ProfileJoiner extends CallableTask {

    private ProfileContainer profileContainer;
    private double ppm;
    private int id;

    /**
     * Constructs the mass trace compilation task.
     *
     * @param params the parameter map
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public ProfileJoiner(ParameterMap params) throws MassCascadeException {

        super(ProfileJoiner.class);

        setParameters(params);
    }

    /**
     * Sets the parameters for the trace compilation task.
     *
     * @param params the new parameter values
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public void setParameters(ParameterMap params) throws MassCascadeException {

        ppm = params.get(Parameter.MZ_WINDOW_PPM, Double.class);
        profileContainer = params.get(Parameter.PROFILE_CONTAINER, ProfileContainer.class);
    }

    /**
     * Executes the trace compilation task.
     *
     * @return the mass spec trace container
     */
    @Override
    public ProfileContainer call() {

        // map a list of profile ids to the m/z of the profile.
        TreeMultimap<Double, Integer> mzIdMap = TreeMultimap.create();

        for (Profile profile : profileContainer) resortProfile(profile, mzIdMap);

        return buildJoinedProfiles(mzIdMap);
    }

    /**
     * Adds or inserts a profile to a mass trace.
     *
     * @param trace   the profile to be added
     * @param mzIdMap the map of m/z-id relations
     */
    private void resortProfile(Profile trace, TreeMultimap<Double, Integer> mzIdMap) {

        double mz = trace.getMz();
        Double mzScanKey = DataUtils.getClosestValue(mz, new TreeSet<Double>(mzIdMap.keySet()));

        if (mzScanKey == null) mzIdMap.put(mz, trace.getId());
        else if (new ToleranceRange(mzScanKey, ppm).contains(mz)) mzIdMap.get(mzScanKey).add(trace.getId());
        else mzIdMap.put(mz, trace.getId());
    }

    /**
     * Compiles the extracted mass traces based on the sorted profiles.
     *
     * @param mzIdMap the map of mass-scan id relations
     * @return the extracted mass traces
     */
    private ProfileContainer buildJoinedProfiles(TreeMultimap<Double, Integer> mzIdMap) {

        String id = profileContainer.getId() + IDENTIFIER;
        ProfileContainer outProfileContainer = profileContainer.getBuilder().newInstance(ProfileContainer.class, id,
                profileContainer.getWorkingDirectory());

        for (double mz : mzIdMap.keySet()) {
            Range mzRange = new ToleranceRange(mz, ppm);
            outProfileContainer.addProfile(buildProfile(mzIdMap.get(mz), mzRange));
        }

        outProfileContainer.finaliseFile();
        return outProfileContainer;
    }

    /**
     * Creates a new mass trace based on the list of profile ids.
     *
     * @param ids     the list of profile ids making up the mass trace
     * @param mzRange the interval of mz covered by the trace
     * @return the mass trace
     */
    private Profile buildProfile(SortedSet<Integer> ids, Range mzRange) {

        Profile profile;
        Map<Double, XYZPoint> dataMap = new TreeMap<Double, XYZPoint>();

        for (int traceId : ids) {
            profile = profileContainer.getProfile(traceId);

            for (XYZPoint dp : profile.getData()) {

                if (dataMap.containsKey(dp.x)) {
                    double avgInt = Math.max(dp.z, dataMap.get(dp.x).z);
                    double avgMz = (dp.z == 0) ? dataMap.get(dp.x).y : (dataMap.get(dp.x).y + dp.y) / 2d;
                    dataMap.put(dp.x, new XYZPoint(dp.x, avgMz, avgInt));
                } else dataMap.put(dp.x, dp);
            }
        }

        Profile joinedProfile = null;
        for (double rt : dataMap.keySet()) {
            if (joinedProfile == null) joinedProfile = new ProfileImpl(id, dataMap.get(rt), mzRange);
            else joinedProfile.addProfilePoint(dataMap.get(rt));
        }
        assert joinedProfile != null;
        joinedProfile.closeProfile();

        id++;

        return joinedProfile;
    }
}
