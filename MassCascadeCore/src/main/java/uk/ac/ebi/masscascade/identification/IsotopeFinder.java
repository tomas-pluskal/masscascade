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

import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.FeatureSet;
import uk.ac.ebi.masscascade.interfaces.container.FeatureSetContainer;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;

/**
 * Class implementing an isotope finder method.
 * <ul>
 * <li>Parameter <code> MZ_WINDOW_PPM </code>- The m/z tolerance in ppm.</li>
 * <li>Parameter <code> FEATURE_SET_FILE </code>- The input feature set container.</li>
 * </ul>
 */
public class IsotopeFinder extends CallableTask {

    private static final int CHARGE = 3;

    private double massTolerance;
    private FeatureSetContainer featureSetContainer;

    /**
     * Constructor for the isotope finder implementation.
     *
     * @param massTolerance the mass tolerance
     */
    public IsotopeFinder(double massTolerance, FeatureSetContainer featureSetContainer) {

        super(IsotopeFinder.class);

        this.massTolerance = massTolerance;
        this.featureSetContainer = featureSetContainer;
    }

    /**
     * Constructs an isotope finder task.
     *
     * @param params the parameter map holding all required task parameters
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the task fails
     */
    public IsotopeFinder(ParameterMap params) throws MassCascadeException {

        super(IsotopeFinder.class);
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

        massTolerance = params.get(Parameter.MZ_WINDOW_PPM, Double.class);
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

//        IsotopeDetector isotopeDetector = new IsotopeDetector(CHARGE, massTolerance);
        IsotopeDetectorRec isotopeDetector = new IsotopeDetectorRec(CHARGE, massTolerance);

        for (FeatureSet featureSet : featureSetContainer) {
            isotopeDetector.findIsotopes(featureSet);
            outFeatureSetContainer.addFeatureSet(featureSet);
        }

        outFeatureSetContainer.finaliseFile();

        return outFeatureSetContainer;
    }
}
