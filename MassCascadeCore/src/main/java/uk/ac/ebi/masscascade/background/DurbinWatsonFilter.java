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

package uk.ac.ebi.masscascade.background;

import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Feature;
import uk.ac.ebi.masscascade.interfaces.container.FeatureContainer;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.properties.Score;
import uk.ac.ebi.masscascade.utilities.xyz.XYZList;

/**
 * Class for feature selection using the Durbin-Watson criterion.
 * <p/>
 * Calculates the Durbin-Watson (DW) score for all features. Features below the threshold will be removed.
 * The score is calculated for the first derivative of the features: x_{ij}=a_{(i+1)j)}-a_{ij}
 * <ul>
 * <li>Parameter <code> DURBIN </code>- The Durbin-Watson threshold.</li>
 * <li>Parameter <code> FEATURE_CONTAINER </code>- The input feature container.</li>
 * </ul>
 */
public class DurbinWatsonFilter extends CallableTask {

    private double dwThreshold;
    private FeatureContainer featureContainer;

    /**
     * Constructs a Durbin-Watson filter task.
     *
     * @param params the parameter map holding all required task parameters
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the task fails
     */
    public DurbinWatsonFilter(ParameterMap params) throws MassCascadeException {

        super(DurbinWatsonFilter.class);
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

        dwThreshold = params.get(Parameter.DURBIN, Double.class);
        featureContainer = params.get(Parameter.FEATURE_CONTAINER, FeatureContainer.class);
    }

    /**
     * Executes the task. The <code> Callable </code> returns a {@link uk.ac.ebi.masscascade.interfaces.container
     * .FeatureContainer} with the processed data.
     *
     * @return the feature container with the processed data
     */
    @Override
    public FeatureContainer call() {

        String id = featureContainer.getId() + IDENTIFIER;
        FeatureContainer outFeatureContainer = featureContainer.getBuilder().newInstance(FeatureContainer.class, id,
                featureContainer.getIonMode(), featureContainer.getWorkingDirectory());

        for (Feature feature : featureContainer) {
            double dw = getDurbinWatson(feature.getData());
            if (dw <= dwThreshold) {
                feature.setProperty(new Score("dw", dw));
                outFeatureContainer.addFeature(feature);
            }
        }

        outFeatureContainer.finaliseFile();
        return outFeatureContainer;
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
