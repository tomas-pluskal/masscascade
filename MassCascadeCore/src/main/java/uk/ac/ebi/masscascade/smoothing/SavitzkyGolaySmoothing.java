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

package uk.ac.ebi.masscascade.smoothing;

import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Feature;
import uk.ac.ebi.masscascade.interfaces.container.FeatureContainer;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

/**
 * Class implementing a Savitzky Golay smoothing method.
 * <ul>
 * <li>Parameter <code> POLYNOMIAL_ORDER </code>- The order of the polynomial function.</li>
 * <li>Parameter <code> SCAN_WINDOW </code>- The number of data points.</li>
 * <li>Parameter <code> FEATURE_FILE </code>- The input feature container.</li>
 * </ul>
 */
public class SavitzkyGolaySmoothing extends CallableTask {

    private FeatureContainer featureContainer;
    private int order;
    private int mzWindow;

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
        mzWindow = params.get(Parameter.SCAN_WINDOW, Integer.class);
        featureContainer = params.get(Parameter.FEATURE_CONTAINER, FeatureContainer.class);
    }

    /**
     * Executes the task. The <code> Callable </code> returns a {@link uk.ac.ebi.masscascade.interfaces.container
     * .ScanContainer} with the processed data.
     *
     * @return the feature container with the processed data
     */
    @Override
    public FeatureContainer call() {

        int nLDp = (int) Math.floor(mzWindow / 2d);
        int nRDp = (int) Math.ceil(mzWindow / 2d);

        double[] coeffs = SavitzkyGolayFilter.computeSGCoefficients(nLDp, nRDp, order);
        SavitzkyGolayFilter sgFilter = new SavitzkyGolayFilter(nLDp, nRDp);

        String id = featureContainer.getId() + IDENTIFIER;
        FeatureContainer outFeatureContainer = featureContainer.getBuilder().newInstance(FeatureContainer.class, id,
                featureContainer.getIonMode(), featureContainer.getWorkingDirectory());

        double[] y;
        double[] smoothedY;

        Feature smoothedFeature;
        for (Feature feature : featureContainer) {

            y = feature.getTrace(mzWindow).getData().getYs();
            smoothedY = sgFilter.smooth(y, coeffs);
            double syMax = 0;
            for (double sy : smoothedY) {
                if (sy > syMax) {
                    syMax = sy;
                }
            }
            double coeff = feature.getIntensity() / syMax;

            smoothedFeature = feature.copy();

            for (int i = mzWindow; i < smoothedY.length - mzWindow - 1; i++)
                smoothedFeature.addFeaturePoint(
                        new XYPoint(feature.getMzData().get(i - mzWindow + 1).x, smoothedY[i] * coeff),
                        feature.getTrace().getData().get(i - mzWindow + 1).x);

            smoothedFeature.closeFeature(feature.getTrace().getData().getLast().x);
            outFeatureContainer.addFeature(smoothedFeature);

            y = null;
            smoothedY = null;
            smoothedFeature = null;
        }

        outFeatureContainer.finaliseFile();
        return outFeatureContainer;
    }
}
