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

import uk.ac.ebi.masscascade.core.featureset.FeatureSetImpl;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.FeatureSet;
import uk.ac.ebi.masscascade.interfaces.container.FeatureSetContainer;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;

import java.util.ArrayList;

/**
 * Class implementing an adduct finder method. The initial adduct map is empty and needs to be populated first.
 * <ul>
 * <li>Parameter <code> MZ_WINDOW_PPM </code>- The mass tolerance in ppm.</li>
 * <li>Parameter <code> ADDUCT_LIST </code>- The adducts to be searched for.</li>
 * <li>Parameter <code> FEATURE_SET_CONTAINER </code>- The input feature set container.</li>
 * </ul>
 */
public class AdductFinder extends CallableTask {

    private AdductDetector adductDetector;
    private FeatureSetContainer featureSetContainer;

    /**
     * Constructor for a adduct finder task.
     *
     * @param params the parameter map
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public AdductFinder(ParameterMap params) throws MassCascadeException {

        super(AdductFinder.class);
        setParameters(params);
    }

    /**
     * Sets the parameters for the adduct finder task.
     *
     * @param params the new parameter values
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public void setParameters(ParameterMap params) throws MassCascadeException {

        featureSetContainer = params.get(Parameter.FEATURE_SET_CONTAINER, FeatureSetContainer.class);
        adductDetector = new AdductDetector(params.get(Parameter.MZ_WINDOW_PPM, Double.class),
                featureSetContainer.getIonMode());
        adductDetector.setAdductList(params.get(Parameter.ADDUCT_LIST, (new ArrayList<>()).getClass()));
    }

    /**
     * Executes the adduct detection task.
     *
     * @return the isotope-detected profiles
     */
    @Override
    public FeatureSetContainer call() {

        String id = featureSetContainer.getId() + IDENTIFIER;
        FeatureSetContainer outFeatureSetContainer = featureSetContainer.getBuilder().newInstance(FeatureSetContainer.class, id,
                featureSetContainer.getIonMode(), featureSetContainer.getWorkingDirectory());

        for (FeatureSet featureSet : featureSetContainer) {
            adductDetector.findAdducts(((FeatureSetImpl) featureSet).getFeaturesList());
            outFeatureSetContainer.addFeatureSet(featureSet);
        }

        outFeatureSetContainer.finaliseFile();
        return outFeatureSetContainer;
    }
}
