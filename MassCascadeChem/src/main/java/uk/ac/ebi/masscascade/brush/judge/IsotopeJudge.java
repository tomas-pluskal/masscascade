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

package uk.ac.ebi.masscascade.brush.judge;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.openscience.cdk.formula.IsotopeContainer;
import org.openscience.cdk.formula.IsotopePattern;
import org.openscience.cdk.formula.IsotopePatternGenerator;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;
import uk.ac.ebi.masscascade.commons.Evidence;
import uk.ac.ebi.masscascade.commons.Status;
import uk.ac.ebi.masscascade.compound.CompoundEntity;
import uk.ac.ebi.masscascade.compound.CompoundSpectrum;
import uk.ac.ebi.masscascade.compound.NotationUtil;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.utilities.range.ToleranceRange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Judge resolving the isotope patterns based on detected isotope signals and compound entity annotations.
 * <p/>
 * The judge can increase the total score of a compound entity by a maximum of 200.
 */
public class IsotopeJudge implements Judge {

    private static final Logger LOGGER = Logger.getLogger(IsotopeJudge.class);

    private final double TOLERANCE_PPM = 100000.0; // 10%
    private final double STEPSIZE_PPM = 50000.0;  //  5%

    private int removed = 0;

    /**
     * The core method of the judge executing the filtering process.
     *
     * @param compoundSpectra the input list of compound spectra
     * @return the filtered input list
     */
    @Override
    public List<CompoundSpectrum> judge(List<CompoundSpectrum> compoundSpectra) {

        LOGGER.log(Level.DEBUG, "Starting Isotope Judge...");

        List<CompoundSpectrum> filteredCS = new ArrayList<>();

        for (CompoundSpectrum cs : compoundSpectra) {
            Set<Integer> isotopes = cs.getIndexToIsotope().keySet();

            if (isotopes.isEmpty()) {
                filteredCS.add(cs);
                continue;
            }

            double[] intensities = new double[isotopes.size()];
            int i = 0;
            for (int isotope : isotopes) {
                intensities[i++] = cs.getPeakList().get(isotope - 1).y;
            }
            // ascending numerical order and reversed (or use comparator...)
            Arrays.sort(intensities);
            ArrayUtils.reverse(intensities);
            // normalise to 1
            double maxIntensity = intensities[0];
            for (int j = 0; j < intensities.length; j++) {
                intensities[j] = intensities[j] / maxIntensity;
            }

            Iterator<CompoundEntity> iter = cs.getCompounds().iterator();
            while (iter.hasNext()) {
                CompoundEntity ce = iter.next();
                boolean filter = false;
                String notation = ce.getNotation(ce.getId());
                IAtomContainer molecule = NotationUtil.getMoleculePlain(notation);
                IMolecularFormula mf = MolecularFormulaManipulator.getMolecularFormula(molecule);

                double[] isoIntensities = getIsoIntensities(mf);

                String isoLog = "";
                for (int j = 0; j < intensities.length; j++) {
                    isoLog += isoIntensities[j] + " \\ " + new ToleranceRange(intensities[j],
                            TOLERANCE_PPM).toString() + "\n";
                    if (!(new ToleranceRange(intensities[j], TOLERANCE_PPM + STEPSIZE_PPM * j)).contains(
                            isoIntensities[j])) {
                        filter = true;
                        break;
                    }
                }

                if (filter) {
                    LOGGER.log(Level.DEBUG, "Removed " + notation + ":\n" + isoLog);
                    iter.remove();
                    removed++;
                } else {
                    ce.setStatus(Status.INTERMEDIATE);
                    ce.setEvidence(Evidence.MSI_3);
                    ce.addScore(200);
                }
            }

            if (cs.getCompounds().size() > 0) {
                filteredCS.add(cs);
            }
        }

        return filteredCS;
    }

    private double[] getIsoIntensities(IMolecularFormula mf) {

        IsotopePatternGenerator ipg = new IsotopePatternGenerator(0);
        IsotopePattern ip = ipg.getIsotopes(mf);
        double halfedProton = Constants.PARTICLES.PROTON.getMass() / 2d;

        int isoIndex = 0;
        IsotopeContainer pIsotope = null;
        double[] isoIntensities = new double[ip.getNumberOfIsotopes()];

        for (IsotopeContainer isotope : ip.getIsotopes()) {
            if (pIsotope == null) {
                isoIntensities[isoIndex] = isotope.getIntensity();
            } else if (isotope.getMass() - pIsotope.getMass() < halfedProton) {
                if (isotope.getIntensity() >= isoIntensities[isoIndex]) {
                    isoIntensities[isoIndex] = isotope.getIntensity();
                }
            } else {
                isoIntensities[++isoIndex] = isotope.getIntensity();
            }
            pIsotope = isotope;
        }

        return isoIntensities;
    }

    /**
     * Returns the number of removed or filtered compound spectra.
     *
     * @return the number of removed or filtered compound spectra
     */
    @Override
    public int removed() {
        return removed;
    }
}