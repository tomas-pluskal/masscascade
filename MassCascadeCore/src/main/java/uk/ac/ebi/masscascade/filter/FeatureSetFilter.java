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
import uk.ac.ebi.masscascade.core.featureset.FeatureSetImpl;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Feature;
import uk.ac.ebi.masscascade.interfaces.FeatureSet;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.interfaces.container.FeatureSetContainer;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;

import java.util.HashSet;
import java.util.Set;

/**
 * Class implementing a feature set filter.
 * <ul>
 * <li>Parameter <code> MZ_RANGE </code>- The mz range used for filtering in amu.</li>
 * <li>Parameter <code> TIME_RANGE </code>- The time range used for filtering in seconds.</li>
 * <li>Parameter <code> MIN_INTENSITY </code>- The minimum intensity for a feature.</li>
 * <li>Parameter <code> FEATURE_SET_FILE </code>- The input feature set container.</li>
 * </ul>
 */
public class FeatureSetFilter extends CallableTask {

    private Range timeRange;
    private Range mzRange;
    private double minIntensity;
    private boolean keepIsotopes;
    private FeatureSetContainer featureSetContainer;

    /**
     * Constructs a feature set filter task.
     *
     * @param params the parameter map holding all required task parameters
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the task fails
     */
    public FeatureSetFilter(ParameterMap params) throws MassCascadeException {

        super(FeatureSetFilter.class);
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
        minIntensity = params.get(Parameter.MIN_FEATURE_INTENSITY, Double.class);
        keepIsotopes = params.get(Parameter.KEEP_ISOTOPES, Boolean.class);
        featureSetContainer = params.get(Parameter.FEATURE_SET_CONTAINER, FeatureSetContainer.class);
    }

    /**
     * Executes the task. The <code> Callable </code> returns a {@link uk.ac.ebi.masscascade.interfaces.container
     * .FeatureSetContainer} with the processed data.
     *
     * @return the featureset container with the processed data
     */
    @Override
    public FeatureSetContainer call() {

        String id = featureSetContainer.getId() + IDENTIFIER;
        FeatureSetContainer outFeatureSetContainer = featureSetContainer.getBuilder().newInstance(FeatureSetContainer.class, id,
                featureSetContainer.getIonMode(), featureSetContainer.getWorkingDirectory());

        for (FeatureSet featureSet : featureSetContainer) {
            if (timeRange.contains(featureSet.getRetentionTime())) {
                Set<Feature> outFeatures = new HashSet<>();
                Range rtRange = null;
                XYList mzIntList = new XYList();
                for (Feature feature : featureSet) {
                    if (mzRange.contains(feature.getMz())) {
                        if (feature.getDifIntensity() >= minIntensity || (keepIsotopes && feature.hasProperty(
                                PropertyType.Isotope))) {
                            if (rtRange == null) rtRange = new ExtendableRange(feature.getRetentionTime());
                            else rtRange.extendRange(feature.getRetentionTime());
                            outFeatures.add(feature);
                            mzIntList.add(feature.getMzIntDp());
                        }
                    }
                }

                if (outFeatures.size() == 0) continue;

                FeatureSet outFeatureSet =
                        new FeatureSetImpl(featureSet.getIndex(), mzIntList, rtRange, featureSet.getRetentionTime(),
                                outFeatures);
                outFeatureSetContainer.addFeatureSet(outFeatureSet);
            }
        }

        outFeatureSetContainer.finaliseFile();
        return outFeatureSetContainer;
    }
}
