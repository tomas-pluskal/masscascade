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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import uk.ac.ebi.masscascade.compound.CompoundEntity;
import uk.ac.ebi.masscascade.compound.CompoundSpectrum;
import uk.ac.ebi.masscascade.compound.NotationUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Judge filtering compound entities by element presence. Only entities with CHNOPS and Halogen elements are kept.
 */
public class ElementJudge implements Judge {

    private static final Logger LOGGER = Logger.getLogger(ElementJudge.class);

    private final Set<String> elements = new HashSet<>();

    {
        elements.add("C");
        elements.add("H");
        elements.add("N");
        elements.add("O");
        elements.add("P");
        elements.add("S");
        elements.add("Cl");
        elements.add("Br");
        elements.add("F");
        elements.add("I");
    }

    private int removed = 0;

    /**
     * The core method of the judge executing the filtering process.
     *
     * @param compoundSpectra the input list of compound spectra
     * @return the filtered input list
     */
    @Override
    public List<CompoundSpectrum> judge(List<CompoundSpectrum> compoundSpectra) {

        List<CompoundSpectrum> filteredCS = new ArrayList<>();

        for (CompoundSpectrum cs : compoundSpectra) {
            Iterator<CompoundEntity> iter = cs.getCompounds().iterator();
            while (iter.hasNext()) {
                CompoundEntity ce = iter.next();
                boolean filter = false;
                String notation = ce.getNotation(ce.getId());
                IAtomContainer molecule = NotationUtil.getMoleculePlain(notation);
                if (molecule.isEmpty()) {
                    filter = true;
                } else {
                    for (IAtom heavyAtom : AtomContainerManipulator.getHeavyAtoms(molecule)) {
                        if (!elements.contains(heavyAtom.getSymbol())) {
                            filter = true;
                            break;
                        }
                    }
                }
                if (filter) {
                    LOGGER.log(Level.DEBUG, "Removed " + notation);
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
