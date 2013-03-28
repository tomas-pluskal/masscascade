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

package uk.ac.ebi.masscascade.smoothing;

import uk.ac.ebi.masscascade.core.profile.ProfileImpl;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.container.ProfileContainer;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

/**
 * Class implementing a Savitzky Golay smoothing method.
 * <ul>
 * <li>Parameter <code> POLYNOMIAL ORDER </code>- The order of the polynomial function.</li>
 * <li>Parameter <code> DATA WINDOW </code>- The number of data points in the m/z domain.</li>
 * <li>Parameter <code> MS LEVEL </code>- The MSn level.</li>
 * <li>Parameter <code> PROFILE FILE </code>- The input profile container.</li>
 * </ul>
 */
public class SavitzkyGolaySmoothing extends CallableTask {

    private ProfileContainer profileContainer;
    private int order;
    private int mzWindow;
    private Constants.MSN msn;

    /**
     * Constructs a Savitzky Golay smoothing task.
     *
     * @param params the parameter map holding all required task parameters
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the task fails
     */
    public SavitzkyGolaySmoothing(ParameterMap params) throws MassCascadeException {

        super(SavitzkyGolaySmoothing.class);
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

        order = params.get(Parameter.POLYNOMIAL_ORDER, Integer.class);
        mzWindow = params.get(Parameter.DATA_WINDOW, Integer.class);
        msn = params.get(Parameter.MS_LEVEL, Constants.MSN.class);
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

        int nLDp = (int) Math.floor(mzWindow / 2d);
        int nRDp = (int) Math.ceil(mzWindow / 2d);

        double[] coeffs = SavitzkyGolayFilter.computeSGCoefficients(nLDp, nRDp, order);
        SavitzkyGolayFilter sgFilter = new SavitzkyGolayFilter(nLDp, nRDp);

        String id = profileContainer.getId() + IDENTIFIER;
        ProfileContainer outProfileContainer = profileContainer.getBuilder().newInstance(ProfileContainer.class, id,
                profileContainer.getWorkingDirectory());

        double[] y;
        double[] smoothedY;

        Profile smoothedProfile;
        for (Profile profile : profileContainer) {

            y = profile.getTrace(mzWindow).getData().getYs();
            smoothedY = sgFilter.smooth(y, coeffs);

            smoothedProfile =
                    new ProfileImpl(profile.getId(), new XYPoint(profile.getMzData().getFirst().x, Constants.MIN_ABUNDANCE),
                            profile.getTrace().getData().getFirst().x, profile.getMzRange());

            for (int i = mzWindow; i < smoothedY.length - mzWindow - 1; i++)
                smoothedProfile.addProfilePoint(new XYPoint(profile.getMzData().get(i - mzWindow + 1).x, smoothedY[i]),
                        profile.getTrace().getData().get(i - mzWindow + 1).x);

            smoothedProfile.closeProfile(new XYPoint(profile.getMzDataLast().x, Constants.MIN_ABUNDANCE),
                    profile.getTrace().getData().getLast().x);
            outProfileContainer.addProfile(smoothedProfile);

            y = null;
            smoothedY = null;
            smoothedProfile = null;
        }

        outProfileContainer.finaliseFile();
        return outProfileContainer;
    }
}
