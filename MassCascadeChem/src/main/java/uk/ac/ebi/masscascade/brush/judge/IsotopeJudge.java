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
import org.openscience.cdk.formula.IsotopeContainer;
import org.openscience.cdk.formula.IsotopePattern;
import org.openscience.cdk.formula.IsotopePatternGenerator;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;
import uk.ac.ebi.masscascade.commons.Converter;
import uk.ac.ebi.masscascade.compound.CompoundEntity;
import uk.ac.ebi.masscascade.compound.CompoundSpectrum;
import uk.ac.ebi.masscascade.utilities.range.ToleranceRange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class IsotopeJudge implements Judge {

    private final double TOLERANCE_PPM = 100000.0; // 10%

    private int removed = 0;

    @Override
    public List<CompoundSpectrum> judge(List<CompoundSpectrum> compoundSpectra) {

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
                intensities[i++] = cs.getPeakList().get(isotope).y;
            }
            // ascending numerical order and reversed (or use comparator...)
            Arrays.sort(intensities);
            ArrayUtils.reverse(intensities);
            // normalise to 1
            for (int j = 0; j < intensities.length; j++) {
                intensities[j] = intensities[j] / intensities[0];
            }

            Iterator<CompoundEntity> iter = cs.getCompounds().iterator();
            while (iter.hasNext()) {
                CompoundEntity ce = iter.next();
                boolean filter = false;
                String notation = ce.getNotation(ce.getId());
                IAtomContainer molecule = Converter.lineNotationToCDK(notation);
                IMolecularFormula mf = MolecularFormulaManipulator.getMolecularFormula(molecule);

                IsotopePatternGenerator ipg = new IsotopePatternGenerator(0);
                IsotopePattern ip = ipg.getIsotopes(mf);
                IsotopeContainer pIsotope = null;
                double pMass = 0;
                int isoIndex = 0;
                double[] isoIntensities = new double[ip.getNumberOfIsotopes()];
                for (IsotopeContainer isotope : ip.getIsotopes()) {
                    if (isotope.getMass() - pMass < 0.5) {
                        if (pIsotope == null || isotope.getIntensity() > pIsotope.getIntensity()) {
                            isoIntensities[isoIndex] = isotope.getIntensity();
                            pIsotope = isotope;
                        }
                    } else {
                        isoIntensities[isoIndex++] = isotope.getIntensity();
                        pIsotope = isotope;
                    }
                }

                for (int j = 0; j < intensities.length; j++) {
                    if (!(new ToleranceRange(intensities[j], TOLERANCE_PPM)).contains(isoIntensities[j])) {
                        filter = true;
                        break;
                    }
                }

                if (filter) {
                    iter.remove();
                    removed++;
                }
            }

            if (cs.getCompounds().size() > 0) {
                filteredCS.add(cs);
            }
        }

        return filteredCS;
    }

    @Override
    public int removed() {
        return removed;
    }
}