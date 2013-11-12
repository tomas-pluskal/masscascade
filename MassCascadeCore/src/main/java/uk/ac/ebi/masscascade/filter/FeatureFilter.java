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

import uk.ac.ebi.masscascade.core.PropertyType;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Feature;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.interfaces.container.FeatureContainer;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;

/**
 * Class implementing a feature filter.
 * <ul>
 * <li>Parameter <code> MZ_RANGE </code>- The mz range used for filtering in amu.</li>
 * <li>Parameter <code> TIME_RANGE </code>- The time range used for filtering in seconds.</li>
 * <li>Parameter <code> FEATURE_RANGE </code>- The feature minimum and maximum width in scans.</li>
 * <li>Parameter <code> FEATURE_CONTAINER </code>- The input feature container.</li>
 * </ul>
 */
public class FeatureFilter extends CallableTask {

    // task variables
    private Range timeRange;
    private Range mzRange;
    private Range featureWidthRange;
    private double minIntensity;
    private FeatureContainer featureContainer;

    /**
     * Constructs a feature filter task.
     *
     * @param params the parameter map holding all required task parameters
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the task fails
     */
    public FeatureFilter(ParameterMap params) throws MassCascadeException {

        super(FeatureFilter.class);
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
        featureWidthRange = params.get(Parameter.FEATURE_RANGE, ExtendableRange.class);
        minIntensity = params.get(Parameter.MIN_FEATURE_INTENSITY, Double.class);
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

        String id = featureContainer.getId() + IDENTIFIER;
        FeatureContainer outFeatureContainer = featureContainer.getBuilder().newInstance(FeatureContainer.class, id,
                featureContainer.getIonMode(), featureContainer.getWorkingDirectory());

        for (Feature feature : featureContainer) {

            if (timeRange.contains(feature.getRetentionTime()) && mzRange.contains(feature.getMz()) &&
                    featureWidthRange.contains(feature.getData().size() - 2)) {
                if (feature.getDifIntensity() >= minIntensity) {
                    outFeatureContainer.addFeature(feature);
                }
            }
        }
        outFeatureContainer.finaliseFile();

        return outFeatureContainer;
    }
}
