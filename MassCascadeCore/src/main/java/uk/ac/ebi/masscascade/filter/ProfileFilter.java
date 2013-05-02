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

package uk.ac.ebi.masscascade.filter;

import uk.ac.ebi.masscascade.core.PropertyManager;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.interfaces.container.ProfileContainer;
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
    private boolean keepIsotopes;
    private ProfileContainer profileContainer;

    /**
     * Constructs a profile filter task.
     *
     * @param params the parameter map holding all required task parameters
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the task fails
     */
    public ProfileFilter(ParameterMap params) throws MassCascadeException {

        super(ProfileFilter.class);
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

        mzRange = params.get(Parameter.MZ_RANGE, ExtendableRange.class);
        timeRange = params.get(Parameter.TIME_RANGE, ExtendableRange.class);
        profileWidthRange = params.get(Parameter.PROFILE_RANGE, ExtendableRange.class);
        minIntensity = params.get(Parameter.MIN_PROFILE_INTENSITY, Double.class);
        profileContainer = params.get(Parameter.PROFILE_CONTAINER, ProfileContainer.class);
        keepIsotopes = params.get(Parameter.KEEP_ISOTOPES, Boolean.class);
    }

    /**
     * Executes the task. The <code> Callable </code> returns a {@link uk.ac.ebi.masscascade.interfaces.container
     * .RawContainer} with the processed data.
     *
     * @return the profile container with the processed data
     */
    @Override
    public ProfileContainer call() {

        String id = profileContainer.getId() + IDENTIFIER;
        ProfileContainer outProfileContainer = profileContainer.getBuilder().newInstance(ProfileContainer.class, id,
                profileContainer.getWorkingDirectory());

        for (Profile profile : profileContainer) {

            if (timeRange.contains(profile.getRetentionTime()) && mzRange.contains(profile.getMz()) &&
                    profileWidthRange.contains(profile.getData().size() - 2)) {
                if (profile.getIntensity() >= minIntensity || (keepIsotopes && profile.hasProperty(
                        PropertyManager.TYPE.Isotope))) outProfileContainer.addProfile(profile);
            }
        }
        outProfileContainer.finaliseFile();

        return outProfileContainer;
    }
}
