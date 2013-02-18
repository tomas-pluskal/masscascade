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

package uk.ac.ebi.masscascade.identification;

import uk.ac.ebi.masscascade.core.container.file.spectrum.FileSpectrumContainer;
import uk.ac.ebi.masscascade.core.spectrum.PseudoSpectrum;
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
     * Constructor for a isotope finder taks.
     *
     * @param params the parameter map
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public IsotopeFinder(ParameterMap params) throws MassCascadeException {

        super(IsotopeFinder.class);
        setParameters(params);
    }

    /**
     * Sets the parameters for the isotope finder task.
     *
     * @param params the new parameter values
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public void setParameters(ParameterMap params) throws MassCascadeException {

        massTolerance = params.get(Parameter.MZ_WINDOW_PPM, Double.class);
        spectrumContainer = params.get(Parameter.SPECTRUM_CONTAINER, FileSpectrumContainer.class);
    }

    /**
     * Executes the isotope detection task.
     *
     * @return the isotope-detected peaks
     */
    public SpectrumContainer call() {

        String id = spectrumContainer.getId() + IDENTIFIER;
        SpectrumContainer
                outSpectrumContainer = new FileSpectrumContainer(id, spectrumContainer.getWorkingDirectory());

        IsotopeDetector isotopeDetector = new IsotopeDetector(CHARGE, massTolerance);

        for (Spectrum spectrum : spectrumContainer) {

            isotopeDetector.findIsotopes(spectrum);
            outSpectrumContainer.addSpectrum(spectrum);
        }

        outSpectrumContainer.finaliseFile();

        return outSpectrumContainer;
    }
}
