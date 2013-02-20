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
 * Class for profile selection using the CODA algorithm.
 * <p/>
 * Calculates the mass chromatographic quality (MCQ) score for all profiles.
 * Profiles below the threshold will be removed.
 * <ul>
 * <li>Parameter <code> CODA </code>- The CODA threshold.</li>
 * <li>Parameter <code> DATA WINDOW </code>- The size of the rectangular smoothing window.</li>
 * <li>Parameter <code> PROFILE CONTAINER </code>- The input profile container.</li>
 * </ul>
 */
public class CodaFilter extends CallableTask {

    private double mcqThreshold;
    private int windowSize;
    private ProfileContainer profileContainer;

    public CodaFilter(ParameterMap params) throws MassCascadeException {

        super(CodaFilter.class);
        setParameters(params);
    }

    /**
     * Sets the parameters for the MCQ task.
     *
     * @param params the parameter values
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public void setParameters(ParameterMap params) throws MassCascadeException {

        mcqThreshold = params.get(Parameter.CODA, Double.class);
        windowSize = params.get(Parameter.DATA_WINDOW, Integer.class);
        profileContainer = params.get(Parameter.PROFILE_CONTAINER, ProfileContainer.class);
    }

    /**
     * Applies the MCQ method to the profiles.
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
            double mcq = getMCQ(profile.getData());
            if (mcq >= mcqThreshold) {
                profile.setProperty(new Score("mcq", mcq));
                outProfileContainer.addProfile(profile);
            }
        }

        outProfileContainer.finaliseFile();
        return outProfileContainer;
    }

    /**
     * Calculates the MCQ value from the given data list.
     *
     * @param data a data list
     * @return the MCQ score
     */
    private double getMCQ(XYZList data) {

        double mean = 0;
        double n = 0;
        double m2 = 0;

        double scaleFactor = 0;

        if (data.size() - windowSize - 3 <= 0) return 0;
        double[] intensities = new double[data.size() - windowSize - 2];
        for (int i = 1; i <= data.size() - windowSize - 2; i++) {
            double intensity = 0;
            for (int j = 0; j < windowSize; j++) intensity += data.get(i + j).z;
            intensity /= windowSize;

            scaleFactor += data.get(i).z * data.get(i).z;
            // Welford's method
            n += 1;
            double delta = intensity - mean;
            mean += delta / n;
            m2 += delta * (intensity - mean);

            intensities[i - 1] = intensity;
        }

        for (int i = data.size() - windowSize; i < data.size() - 1; i++) scaleFactor += data.get(i).z * data.get(i).z;
        scaleFactor = Math.sqrt(scaleFactor);

        double stdDev = Math.sqrt(m2 / (n - 1));
        double mcq = 0;

        for (int i = 0; i < intensities.length; i++)
            mcq += (data.get(i + 1).z / scaleFactor) * ((intensities[i] - mean) / stdDev);

        if (mcq < 0) mcq = 0;
        return mcq / Math.sqrt(data.size() - windowSize);
    }
}
