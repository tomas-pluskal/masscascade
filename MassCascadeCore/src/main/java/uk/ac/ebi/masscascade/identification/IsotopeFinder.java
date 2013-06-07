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
import uk.ac.ebi.masscascade.interfaces.Spectrum;
import uk.ac.ebi.masscascade.interfaces.container.SpectrumContainer;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;

/**
 * Class implementing an isotope finder method.
 * <ul>
 * <li>Parameter <code> MZ WINDOW PPM </code>- The m/z tolerance in ppm.</li>
 * <li>Parameter <code> SPECTRUM FILE </code>- The input spectrum container.</li>
 * </ul>
 */
public class IsotopeFinder extends CallableTask {

    private static final int CHARGE = 3;

    private double massTolerance;
    private SpectrumContainer spectrumContainer;

    /**
     * Constructor for the isotope finder implementation.
     *
     * @param massTolerance the mass tolerance
     */
    public IsotopeFinder(double massTolerance, SpectrumContainer spectrumContainer) {

        super(IsotopeFinder.class);

        this.massTolerance = massTolerance;
        this.spectrumContainer = spectrumContainer;
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
        spectrumContainer = params.get(Parameter.SPECTRUM_CONTAINER, SpectrumContainer.class);
    }

    /**
     * Executes the task. The <code> Callable </code> returns a {@link uk.ac.ebi.masscascade.interfaces.container
     * .RawContainer} with the processed data.
     *
     * @return the spectrum container with the processed data
     */
    @Override
    public SpectrumContainer call() {

        String id = spectrumContainer.getId() + IDENTIFIER;
        SpectrumContainer outSpectrumContainer = spectrumContainer.getBuilder().newInstance(SpectrumContainer.class, id,
                spectrumContainer.getWorkingDirectory());

        IsotopeDetector isotopeDetector = new IsotopeDetector(CHARGE, massTolerance);

        for (Spectrum spectrum : spectrumContainer) {
            isotopeDetector.findIsotopes(spectrum);
            outSpectrumContainer.addSpectrum(spectrum);

            spectrum = null;
        }

        outSpectrumContainer.finaliseFile();

        return outSpectrumContainer;
    }
}
