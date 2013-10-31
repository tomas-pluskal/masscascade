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

package uk.ac.ebi.masscascade.identification;

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
import uk.ac.ebi.masscascade.properties.Adduct;
import uk.ac.ebi.masscascade.properties.Isotope;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class to filter for features that have an isotope annotation.
 * <ul>
 * <li>Parameter <code> TIME_WINDOW </code>- The scan window for the alignment in scans.</li>
 * <li>Parameter <code> REFERENCE_FILE </code>- The input scan reference container.</li>
 * <li>Parameter <code> FEATURE_SET_FILE </code>- The input feature set container.</li>
 * </ul>
 */
public class IsotopeKeeper extends CallableTask {

    private FeatureSetContainer featureSetContainer;

    /**
     * Constructs an isotope keeper task.
     *
     * @param params the parameter map holding all required task parameters
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the task fails
     */
    public IsotopeKeeper(ParameterMap params) throws MassCascadeException {

        super(IsotopeKeeper.class);
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
        featureSetContainer = params.get(Parameter.FEATURE_SET_CONTAINER, FeatureSetContainer.class);
    }

    /**
     * Executes the task. The <code> Callable </code> returns a {@link uk.ac.ebi.masscascade.interfaces.container
     * .ScanContainer} with the processed data.
     *
     * @return the featureset container with the processed data
     */
    @Override
    public FeatureSetContainer call() {

        String id = featureSetContainer.getId() + IDENTIFIER;
        FeatureSetContainer outFeatureSetContainer = featureSetContainer.getBuilder().newInstance(FeatureSetContainer.class, id,
                featureSetContainer.getIonMode(), featureSetContainer.getWorkingDirectory());

        XYList xyList;
        Range rtRange;
        Feature exFeature;
        Set<Feature> features;

        for (FeatureSet featureSet : featureSetContainer) {

            Set<Integer> idSet = new HashSet<>();
            List<Integer> adductParentIds = new ArrayList<>();

            for (Feature feature : ((FeatureSetImpl) featureSet).getFeaturesList()) {

                if (feature.hasProperty(PropertyType.Isotope)) {
                    idSet.add(feature.getId());
                    for (Isotope propI : feature.getProperty(PropertyType.Isotope, Isotope.class)) {
                        if (propI.getValue(Integer.class) == 0 && feature.hasProperty(
                                PropertyType.Adduct))
                            adductParentIds.add(feature.getId());
                    }
                }
            }

            for (Feature feature : ((FeatureSetImpl) featureSet).getFeaturesList()) {

                if (feature.hasProperty(PropertyType.Adduct)) {
                    for (Adduct propA : feature.getProperty(PropertyType.Adduct, Adduct.class))
                        if (adductParentIds.contains((propA).getParentId())) idSet.add(feature.getId());
                }
            }

            rtRange = new ExtendableRange();
            xyList = new XYList();
            features = new HashSet<>();

            for (int featureId : idSet) {

                exFeature = featureSet.getFeature(featureId);

                if (exFeature == null) continue;

                features.add(exFeature);
                xyList.add(exFeature.getMzIntDp());
                rtRange.extendRange(exFeature.getRetentionTime());
            }

            if (xyList.isEmpty()) continue;

            outFeatureSetContainer.addFeatureSet(
                    new FeatureSetImpl(featureSet.getIndex(), xyList, rtRange, featureSet.getRetentionTime(), features));
        }

        outFeatureSetContainer.finaliseFile();
        return outFeatureSetContainer;
    }
}
