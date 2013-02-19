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

import uk.ac.ebi.masscascade.core.container.file.profile.FileProfileContainer;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.container.ProfileContainer;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.properties.Score;
import uk.ac.ebi.masscascade.utilities.xyz.XYZList;

/**
 * Class for profile selection using the Durbin-Watson criterion.
 * <p/>
 * Calculates the Durbin-Watson (DW) score for all profiles. Profiles below the threshold will be removed.
 * The score is calculated for the first derivative of the profiles: x_{ij}=a_{(i+1)j)}-a_{ij}
 * <ul>
 * <li>Parameter <code> DURBIN </code>- The Durbin-Watson threshold.</li>
 * <li>Parameter <code> PROFILE CONTAINER </code>- The input profile container.</li>
 * </ul>
 */
public class DurbinWatsonFilter extends CallableTask {

    private double dwThreshold;
    private ProfileContainer profileContainer;

    public DurbinWatsonFilter(ParameterMap params) throws MassCascadeException {

        super(DurbinWatsonFilter.class);
        setParameters(params);
    }

    /**
     * Sets the parameters for the Durbin-Watson task.
     *
     * @param params the parameter values
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public void setParameters(ParameterMap params) throws MassCascadeException {

        dwThreshold = params.get(Parameter.DURBIN, Double.class);
        profileContainer = params.get(Parameter.PROFILE_CONTAINER, ProfileContainer.class);
    }

    /**
     * Applies the Durbin-Watson criterion to the profiles.
     *
     * @return the profile container
     * @throws Exception unexptected behaviour
     */
    @Override
    public ProfileContainer call() {

        String id = profileContainer.getId() + IDENTIFIER;
        ProfileContainer outProfileContainer = profileContainer.getBuilder().newInstance(ProfileContainer.class, id,
                profileContainer.getWorkingDirectory());

        for (Profile profile : profileContainer) {
            double dw = getDurbinWatson(profile.getData());
            if (dw <= dwThreshold) {
                profile.setProperty(new Score("dw", dw));
                outProfileContainer.addProfile(profile);
            }
        }

        outProfileContainer.finaliseFile();
        return outProfileContainer;
    }

    /**
     * Calculates the Durbin-Watson score from the first derivative of the chromatogram data list.
     *
     * @param data the chromatogram data list
     * @return the Durbin-Watson score
     */
    private double getDurbinWatson(XYZList data) {

        double xSq = 0;
        double[] intensities = new double[data.size() - 2];
        for (int i = 1; i < intensities.length; i++) {
            intensities[i] = data.get(i + 1).z - data.get(i).z;
            xSq += intensities[i] * intensities[i];
        }

        double xDe = 0;
        for (int i = 1; i < intensities.length; i++)
            xDe += (intensities[i] - intensities[i - 1]) * (intensities[i] - intensities[i - 1]);

        return xDe / xSq;
    }
}
