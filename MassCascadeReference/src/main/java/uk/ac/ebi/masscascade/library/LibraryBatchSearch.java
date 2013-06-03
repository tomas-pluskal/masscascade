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

import org.apache.commons.math3.util.FastMath;
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
import uk.ac.ebi.masscascade.score.MzScorer;
import uk.ac.ebi.masscascade.score.WeightedScorer;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Task to query custom libraries, matching MSn spectra where indicated.
 * <ul>
 * <li>Parameter <code> MZ WINDOW PPM </code>- The m/z tolerance value for the precursor ions in ppm.</li>
 * <li>Parameter <code> MZ WINDOW AMU </code>- The m/z tolerance value for the MSn signals in dalton.</li>
 * <li>Parameter <code> ION MODE </code>- The ion mode.</li>
 * <li>Parameter <code> MS LEVEL </code>- The MSn level to be queried.</li>
 * <li>Parameter <code> SCORE </code>- The minimum query score (0-1000).</li>
 * <li>Parameter <code> COLLISION ENERGY </code>- The collision energy.</li>
 * <li>Parameter <code> SPECTRUM CONTAINER </code>- The input spectrum container.</li>
 * </ul>
 */
public class LibraryBatchSearch extends CallableSearch {

    private double ppmMS1;
    private double amuMSn;
    private double minScore;
    private int collisionEnergy;
    private Constants.MSN msn;
    private Constants.ION_MODE ionMode;
    private SpectrumContainer spectrumContainer;
    private List<ReferenceContainer> referenceContainer;

    private MzScorer mzScorer;
    private WeightedScorer weightedScorer;

    /**
     * Constructs a MSn reference batch search task.
     *
     * @param params the parameter map holding all required task parameters
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the web task fails
     */
    public LibraryBatchSearch(ParameterMap params) throws MassCascadeException {

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
        ionMode = params.get(Parameter.ION_MODE, Constants.ION_MODE.class);
        collisionEnergy = params.get(Parameter.COLLISION_ENERGY, Integer.class);
        spectrumContainer = params.get(Parameter.SPECTRUM_CONTAINER, SpectrumContainer.class);
        referenceContainer =
                params.get(LibraryParameter.REFERENCE_LIBRARY_LIST, new ArrayList<ReferenceContainer>().getClass());
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

        mzScorer = new MzScorer(ppmMS1);
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

        for (ReferenceContainer singleRefCont : referenceContainer) {
            if (singleRefCont.getMsn() != msn) continue;
            for (ReferenceSpectrum reference : singleRefCont) {
                if (reference.getIonMode() != ionMode || (reference.getCollisionEnergy() != collisionEnergy &&
                        collisionEnergy != 0))
                    continue;

                double mass = 0;

                if (reference.getPrecursorMass() != 0) {
                    XYPoint nearestDp = spectrum.getNearestPoint(reference.getPrecursorMass(), ppmMS1);
                    mass = nearestDp.x;
                } else if (reference.getMass() != 0) {
                    mass = reference.getMass();
                    if (ionMode == Constants.ION_MODE.POSITIVE) mass += Constants.PARTICLES.PROTON.getMass();
                    else if (ionMode.equals(Constants.ION_MODE.NEGATIVE)) mass -= Constants.PARTICLES.PROTON.getMass();
                }

                // simple m/z match
                if (reference.getMzIntList().size() == 1) {
                    for (Profile profile : spectrum) {
                        double score = mzScorer.score(profile, reference);
                        if (score < minScore) continue;
                        Identity identity =
                                new Identity(reference.getId(), reference.getName(), reference.getNotation(), score,
                                        singleRefCont.getSource(), msn.name(), reference.getTitle());
                        profile.setProperty(identity);
                    }
                } else { // spectrum match
                    double score = weightedScorer.getScore(spectrum, reference);
                    if (score < minScore) continue;

                    Identity identity =
                            new Identity(reference.getId(), reference.getName(), reference.getNotation(), score,
                                    singleRefCont.getSource(), msn.name(), reference.getTitle());

                    XYPoint nearestDp = spectrum.getNearestPoint(mass, ppmMS1);
                    if (nearestDp == null) continue;
                    for (Profile profile : spectrum) {
                        if (profile.getMz() == nearestDp.x) {
                            profile.setProperty(identity);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void score(Profile profile) {

        for (Spectrum unknown : profile.getMsnSpectra(msn)) {
            for (ReferenceContainer singleRefCont : referenceContainer) {
                if (singleRefCont.getMsn() != msn) continue;
                for (ReferenceSpectrum reference : singleRefCont.getSpectra(unknown.getParentMz(), ppmMS1)) {

                    if (reference.getIonMode() != ionMode) continue;

                    double score = weightedScorer.getScore(unknown, reference);

                    if (score <= minScore) continue;

                    if (msn == Constants.MSN.MS2) {
                        Identity identity =
                                new Identity(reference.getId(), reference.getName(), reference.getNotation(), score,
                                        singleRefCont.getSource(), msn.name(), reference.getTitle());
                        profile.setProperty(identity);
                    } else {
                        for (Spectrum msnSpectrum : profile.getMsnSpectra(msn.up())) {
                            if (msnSpectrum.getProfile(unknown.getParentScan()) != null) {
                                Profile msnProfile = msnSpectrum.getProfile(unknown.getParentScan());

                                Identity identity =
                                        new Identity(reference.getId(), reference.getName(), reference.getNotation(),
                                                score, singleRefCont.getSource(), msn.name(), reference.getTitle());
                                msnProfile.setProperty(identity);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}
