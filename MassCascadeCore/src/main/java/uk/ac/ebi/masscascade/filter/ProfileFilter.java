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

package uk.ac.ebi.masscascade.filter;

import uk.ac.ebi.masscascade.core.container.file.profile.FileProfileContainer;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.container.ProfileContainer;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;

/**
 * Class implementing a profile filter.
 * <ul>
 * <li>Parameter <code> MZ_RANGE </code>- The mz range used for filtering in amu.</li>
 * <li>Parameter <code> TIME RANGE </code>- The time range used for filtering in seconds.</li>
 * <li>Parameter <code> PROFILE RANGE </code>- The profile minimum and maximum width in scans.</li>
 * <li>Parameter <code> PROFILE FILE </code>- The input profile container.</li>
 * </ul>
 */
public class ProfileFilter extends CallableTask {

    // task variables
    private Range timeRange;
    private Range mzRange;
    private Range profileWidthRange;
    private double minIntensity;
    private ProfileContainer profileContainer;

    /**
     * Constructor for a filter implementation task.
     *
     * @param params the parameter map
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public ProfileFilter(ParameterMap params) throws MassCascadeException {

        super(ProfileFilter.class);
        setParameters(params);
    }

    /**
     * Sets the parameters for the ProfileFilter task.
     *
     * @param params the new parameter values
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public void setParameters(ParameterMap params) throws MassCascadeException {

        mzRange = params.get(Parameter.MZ_RANGE, ExtendableRange.class);
        timeRange = params.get(Parameter.TIME_RANGE, ExtendableRange.class);
        profileWidthRange = params.get(Parameter.PROFILE_RANGE, ExtendableRange.class);
        minIntensity = params.get(Parameter.MIN_PROFILE_INTENSITY, Double.class);
        profileContainer = params.get(Parameter.PROFILE_CONTAINER, ProfileContainer.class);
    }

    /**
     * Executes the task.
     *
     * @return the filtered profile collection
     */
    @Override
    public ProfileContainer call() {

        String id = profileContainer.getId() + IDENTIFIER;
        ProfileContainer outProfileContainer = profileContainer.getBuilder().newInstance(ProfileContainer.class, id,
                profileContainer.getWorkingDirectory());

        for (Profile profile : profileContainer) {

            if (timeRange.contains(profile.getRetentionTime()) &&
                    mzRange.contains(profile.getMz()) &&
                    profileWidthRange.contains(profile.getData().size() - 2) &&
                    profile.getIntensity() >= minIntensity) outProfileContainer.addProfile(profile);
        }
        outProfileContainer.finaliseFile();

        return outProfileContainer;
    }
}
