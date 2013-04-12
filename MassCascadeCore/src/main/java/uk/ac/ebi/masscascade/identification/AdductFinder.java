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

import uk.ac.ebi.masscascade.core.spectrum.PseudoSpectrum;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Spectrum;
import uk.ac.ebi.masscascade.interfaces.container.SpectrumContainer;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;

import java.util.ArrayList;

/**
 * Class implementing an adduct finder method. The initial adduct map is empty and needs to be populated first.
 * <ul>
 * <li>Parameter <code> MZ WINDOW PPM </code>- The mass tolerance in ppm.</li>
 * <li>Parameter <code> ION MODE </code>- The ion mode.</li>
 * <li>Parameter <code> NEUTRAL LOSS </code>- If the input is a list of neutral losses.</li>
 * <li>Parameter <code> ADDUCT LIST </code>- The adducts to be searched for.</li>
 * <li>Parameter <code> SPECTRUM FILE </code>- The input spectrum container.</li>
 * </ul>
 */
public class AdductFinder extends CallableTask {

    private AdductDetector adductDetector;
    private SpectrumContainer spectrumContainer;

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

        spectrumContainer = params.get(Parameter.SPECTRUM_CONTAINER, SpectrumContainer.class);
        adductDetector = new AdductDetector(params.get(Parameter.MZ_WINDOW_PPM, Double.class),
                params.get(Parameter.ION_MODE, Constants.ION_MODE.class),
                params.get(Parameter.NEUTRAL_LOSS, Boolean.class));
        adductDetector.setAdductList(params.get(Parameter.ADDUCT_LIST, (new ArrayList<AdductSingle>()).getClass()));
    }

    /**
     * Executes the adduct detection task.
     *
     * @return the isotope-detected profiles
     */
    @Override
    public SpectrumContainer call() {

        String id = spectrumContainer.getId() + IDENTIFIER;
        SpectrumContainer outSpectrumContainer = spectrumContainer.getBuilder().newInstance(SpectrumContainer.class, id,
                spectrumContainer.getWorkingDirectory());

        for (Spectrum spectrum : spectrumContainer) {
            adductDetector.findAdducts(((PseudoSpectrum) spectrum).getProfileList());
            outSpectrumContainer.addSpectrum(spectrum);
        }

        outSpectrumContainer.finaliseFile();
        return outSpectrumContainer;
    }
}
