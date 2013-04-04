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

package uk.ac.ebi.masscascade.tracebuilder;

import uk.ac.ebi.masscascade.core.profile.ProfileImpl;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.container.ProfileContainer;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.xyz.XYZList;
import uk.ac.ebi.masscascade.utilities.xyz.XYZPoint;

/**
 * Class implementing a mass trace splitter method.
 * <p/>
 * The traces are searched for zero intensity values which serve as boundary signal to define separate profiles.
 * <ul>
 * <li>Parameter <code> PROFILE FILE </code>- The input profile container.</li>
 * </ul>
 */
public class ProfileSplitter extends CallableTask {

    private ProfileContainer profileContainer;
    private ProfileContainer outProfileContainer;
    private int id;

    /**
     * Constructs a trace splitter task.
     *
     * @param params the parameter map holding all required task parameters
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the task fails
     */
    public ProfileSplitter(ParameterMap params) throws MassCascadeException {

        super(ProfileSplitter.class);
        setParameters(params);
        id = 0;
    }

    /**
     * Sets the task class variables using the parameter map.
     *
     * @param params the parameter map containing the <code> Parameter </code> to <code> Object </code> relations.
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the parameter map does not contain all variables required by this class
     */
    public void setParameters(ParameterMap params) throws MassCascadeException {
        profileContainer = params.get(Parameter.PROFILE_CONTAINER, ProfileContainer.class);
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
        outProfileContainer = profileContainer.getBuilder().newInstance(ProfileContainer.class, id,
                profileContainer.getWorkingDirectory());

        for (Profile profile : profileContainer) extractTraces(profile);

        outProfileContainer.finaliseFile();
        return outProfileContainer;
    }

    /**
     * Extracts the profiles from the mass trace using zero intensity values as boundary condition.
     *
     * @param inProfile the mass trace containing the profiles
     */
    private void extractTraces(Profile inProfile) {

        XYZPoint dp;
        boolean isNew = true;
        Profile partProfile = null;
        XYZList data = inProfile.getData();

        for (int i = 0; i < data.size(); i++) {
            dp = data.get(i);
            if (isNew) {
                if (dp.z != 0 && partProfile != null) {
                    partProfile = new ProfileImpl(id, data.get(i - 1), inProfile.getMzRange());
                    partProfile.addProfilePoint(dp);
                } else partProfile = new ProfileImpl(id, dp, inProfile.getMzRange());
                id++;
                isNew = false;
            } else partProfile.addProfilePoint(dp);

            if (dp.z == 0 && partProfile.getData().size() > 1) {
                partProfile.closeProfile();
                outProfileContainer.addProfile(partProfile);
                isNew = true;
            }
        }
    }
}
