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

package uk.ac.ebi.masscascade.library;

import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableSearch;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.Spectrum;
import uk.ac.ebi.masscascade.interfaces.container.SpectrumContainer;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.properties.Identity;
import uk.ac.ebi.masscascade.reference.ReferenceContainer;
import uk.ac.ebi.masscascade.reference.ReferenceSpectrum;
import uk.ac.ebi.masscascade.score.WeightedScorer;

public class LibrarySearch extends CallableSearch {

    private double ppmMS1;
    private double amuMSn;
    private double minScore;
    private Constants.MSN msn;
    private SpectrumContainer spectrumContainer;
    private ReferenceContainer referenceContainer;

    private WeightedScorer weightedScorer;

    /**
     * Constructs a MSn reference search task.
     *
     * @param params the parameter map holding all required task parameters
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the web task fails
     */
    public LibrarySearch(ParameterMap params) throws MassCascadeException {

        super(LibrarySearch.class);
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

        ppmMS1 = params.get(Parameter.MZ_WINDOW_PPM, Double.class);
        amuMSn = params.get(Parameter.MZ_WINDOW_AMU, Double.class);
        minScore = params.get(Parameter.SCORE, Double.class);
        msn = params.get(Parameter.MS_LEVEL, Constants.MSN.class);
        spectrumContainer = params.get(Parameter.SPECTRUM_CONTAINER, SpectrumContainer.class);
        referenceContainer = params.get(LibraryParameter.REFERENCE_LIBRARY, ReferenceContainer.class);
    }

    /**
     * Executes the task. The <code> Callable </code> returns a {@link uk.ac.ebi.masscascade.interfaces.container
     * .SpectrumContainer} with the processed data.
     *
     * @return the spectrum container with the processed data
     */
    public SpectrumContainer call() {

        String id = spectrumContainer.getId() + IDENTIFIER;
        SpectrumContainer outContainer = spectrumContainer.getBuilder().newInstance(SpectrumContainer.class, id,
                spectrumContainer.getWorkingDirectory());

        weightedScorer = new WeightedScorer(amuMSn);

        if (msn == Constants.MSN.MS1) {
            for (Spectrum spectrum : spectrumContainer) {
                score(spectrum);
                outContainer.addSpectrum(spectrum);
            }
        } else {
            for (Spectrum spectrum : spectrumContainer) {
                for (Profile profile : spectrum) {
                    if (profile.hasMsnSpectra(msn)) score(profile);
                }
                outContainer.addSpectrum(spectrum);
            }
        }

        outContainer.finaliseFile();
        return outContainer;
    }

    private void score(Spectrum spectrum) {

    }

    private void score(Profile profile) {

        for (Spectrum unknown : profile.getMsnSpectra(msn)) {
            for (ReferenceSpectrum reference : referenceContainer.getSpectra(unknown.getParentMz(), ppmMS1)) {
                double score = weightedScorer.getScore(unknown, reference);

                if (score < minScore) continue;

                if (msn == Constants.MSN.MS2) {
                    Identity identity =
                            new Identity(reference.getId(), reference.getName(), reference.getNotation(), score,
                                    reference.getSource(), msn.name(), reference.getTitle());
                    profile.setProperty(identity);
                } else {
                    for (Spectrum msnSpectrum : profile.getMsnSpectra(msn.up())) {
                        if (msnSpectrum.getProfile(unknown.getParentScan()) != null) {
                            Profile msnProfile = msnSpectrum.getProfile(unknown.getParentScan());

                            Identity identity =
                                    new Identity(reference.getId(), reference.getName(), reference.getNotation(), score,
                                            reference.getSource(), msn.name(), reference.getTitle());
                            msnProfile.setProperty(identity);
                            break;
                        }
                    }
                }
            }
        }
    }
}
