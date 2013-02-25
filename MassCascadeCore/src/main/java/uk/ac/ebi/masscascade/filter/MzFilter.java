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

import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.interfaces.container.ProfileContainer;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.DataUtils;
import uk.ac.ebi.masscascade.utilities.range.ToleranceRange;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * Class implementing a mass filter. Pre-defined ion masses are detected and removed from the profile collection.
 * <ul>
 * <li>Parameter <code> MZ WINDOW PPM </code>- The mz window in ppm.</li>
 * <li>Parameter <code> MASSES FOR REMOVAL </code>- The mz values that should be removed.</li>
 * <li>Parameter <code> PROFILE FILE </code>- The input profile container.</li>
 * </ul>
 */
public class MzFilter extends CallableTask {

    // task variables
    private double ppm;
    private TreeSet<Double> mzForRemoval;
    private Map<Double, Range> mzRanges;
    private ProfileContainer sampleContainer;

    /**
     * Constructs a mass filter task.
     *
     * @param params the parameter map holding all required task parameters
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the task fails
     */
    public MzFilter(ParameterMap params) throws MassCascadeException {

        super(MzFilter.class);
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
        mzForRemoval = params.get(Parameter.MZ_FOR_REMOVAL, (new TreeSet<Double>()).getClass());
        sampleContainer = params.get(Parameter.PROFILE_CONTAINER, ProfileContainer.class);

        mzRanges = new HashMap<Double, Range>();
        for (double mz : mzForRemoval)
            mzRanges.put(mz, new ToleranceRange(mz, ppm));
    }

    /**
     * Executes the task. The <code> Callable </code> returns a {@link uk.ac.ebi.masscascade.interfaces.container
     * .RawContainer} with the processed data.
     *
     * @return the profile container with the processed data
     */
    @Override
    public ProfileContainer call() {

        String id = sampleContainer.getId() + IDENTIFIER;
        ProfileContainer profileContainer = sampleContainer.getBuilder().newInstance(ProfileContainer.class, id,
                sampleContainer.getWorkingDirectory());

        for (Profile profile : sampleContainer) {

            Double mz = DataUtils.getClosestValue(profile.getMz(), mzForRemoval);
            if (mz == null) profileContainer.addProfile(profile);
            else if (!(mzForRemoval.contains(mz) && mzRanges.get(mz).contains(profile.getMz())))
                profileContainer.addProfile(profile);
        }
        profileContainer.finaliseFile();

        return profileContainer;
    }
}
