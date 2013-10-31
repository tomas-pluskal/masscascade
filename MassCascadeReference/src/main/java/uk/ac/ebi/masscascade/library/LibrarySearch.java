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
import uk.ac.ebi.masscascade.interfaces.Feature;
import uk.ac.ebi.masscascade.interfaces.FeatureSet;
import uk.ac.ebi.masscascade.interfaces.container.FeatureSetContainer;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.properties.Identity;
import uk.ac.ebi.masscascade.reference.ReferenceContainer;
import uk.ac.ebi.masscascade.reference.ReferenceSpectrum;
import uk.ac.ebi.masscascade.score.WeightedScorer;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

/**
 * Task to query custom libraries, matching MSn spectra where indicated.
 * <ul>
 * <li>Parameter <code> MZ WINDOW PPM </code>- The m/z tolerance value for the precursor ions in ppm.</li>
 * <li>Parameter <code> MZ WINDOW AMU </code>- The m/z tolerance value for the MSn signals in dalton.</li>
 * <li>Parameter <code> ION MODE </code>- The ion mode.</li>
 * <li>Parameter <code> MS LEVEL </code>- The MSn level to be queried.</li>
 * <li>Parameter <code> SCORE </code>- The minimum query score (0-1000).</li>
 * <li>Parameter <code> COLLISION ENERGY </code>- The collision energy.</li>
 * <li>Parameter <code> SPECTRUM CONTAINER </code>- The input featureset container.</li>
 * </ul>
 */
public class LibrarySearch extends CallableSearch {

    private double ppmMS1;
    private double amuMSn;
    private double minScore;
    private Constants.MSN msn;
    private Constants.ION_MODE ionMode;
    private FeatureSetContainer featureSetContainer;
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
        ionMode = params.get(Parameter.ION_MODE, Constants.ION_MODE.class);
        featureSetContainer = params.get(Parameter.FEATURE_SET_CONTAINER, FeatureSetContainer.class);
        referenceContainer = params.get(LibraryParameter.REFERENCE_LIBRARY, ReferenceContainer.class);
    }

    /**
     * Executes the task. The <code> Callable </code> returns a {@link uk.ac.ebi.masscascade.interfaces.container
     * .FeatureSetContainer} with the processed data.
     *
     * @return the featureset container with the processed data
     */
    public FeatureSetContainer call() {

        String id = featureSetContainer.getId() + IDENTIFIER;
        FeatureSetContainer outContainer = featureSetContainer.getBuilder().newInstance(FeatureSetContainer.class, id,
                featureSetContainer.getIonMode(), featureSetContainer.getWorkingDirectory());

        weightedScorer = new WeightedScorer(amuMSn);

        if (msn == Constants.MSN.MS1) {
            for (FeatureSet featureSet : featureSetContainer) {
                score(featureSet);
                outContainer.addFeatureSet(featureSet);
            }
        } else {
            for (FeatureSet featureSet : featureSetContainer) {
                for (Feature feature : featureSet) {
                    if (feature.hasMsnSpectra(msn)) score(feature);
                }
                outContainer.addFeatureSet(featureSet);
            }
        }

        outContainer.finaliseFile();
        return outContainer;
    }

    private void score(FeatureSet featureSet) {

        for (ReferenceSpectrum reference : referenceContainer) {
            if (reference.getIonMode() != ionMode) continue;

            double score = weightedScorer.getScore(featureSet, reference);
            if (score < minScore) continue;

            Identity identity = new Identity(reference.getId(), reference.getName(), reference.getNotation(), score,
                    reference.getSource(), msn.name(), reference.getTitle());

            double mass = 0;

            if (reference.getPrecursorMass() != 0) {
                XYPoint nearestDp = featureSet.getNearestPoint(reference.getPrecursorMass(), ppmMS1);
                mass = nearestDp.x;
            } else if (reference.getMass() != 0) {
                mass = reference.getMass();
                if (ionMode == Constants.ION_MODE.POSITIVE) mass += Constants.PARTICLES.PROTON.getMass();
                else if (ionMode.equals(Constants.ION_MODE.NEGATIVE)) mass -= Constants.PARTICLES.PROTON.getMass();
            }

            XYPoint nearestDp = featureSet.getNearestPoint(mass, ppmMS1);
            for (Feature feature : featureSet) {
                if (feature.getMz() == nearestDp.x) {
                    feature.setProperty(identity);
                    break;
                }
            }
        }
    }

    private void score(Feature feature) {

        for (FeatureSet unknown : feature.getMsnSpectra(msn)) {
            for (ReferenceSpectrum reference : referenceContainer.getSpectra(unknown.getParentMz(), ppmMS1)) {

                if (reference.getIonMode() != ionMode) continue;

                double score = weightedScorer.getScore(unknown, reference);
                if (score < minScore) continue;

                if (msn == Constants.MSN.MS2) {
                    Identity identity =
                            new Identity(reference.getId(), reference.getName(), reference.getNotation(), score,
                                    reference.getSource(), msn.name(), reference.getTitle());
                    feature.setProperty(identity);
                } else {
                    for (FeatureSet msnFeatureSet : feature.getMsnSpectra(msn.up())) {
                        if (msnFeatureSet.getFeature(unknown.getParentScan()) != null) {
                            Feature msnFeature = msnFeatureSet.getFeature(unknown.getParentScan());

                            Identity identity =
                                    new Identity(reference.getId(), reference.getName(), reference.getNotation(), score,
                                            reference.getSource(), msn.name(), reference.getTitle());
                            msnFeature.setProperty(identity);
                            break;
                        }
                    }
                }
            }
        }
    }
}
