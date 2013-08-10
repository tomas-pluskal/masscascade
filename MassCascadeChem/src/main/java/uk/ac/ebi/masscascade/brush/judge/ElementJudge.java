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
import uk.ac.ebi.masscascade.commons.Converter;
import uk.ac.ebi.masscascade.compound.CompoundEntity;
import uk.ac.ebi.masscascade.compound.CompoundSpectrum;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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

    @Override
    public List<CompoundSpectrum> judge(List<CompoundSpectrum> compoundSpectra) {

        List<CompoundSpectrum> filteredCS = new ArrayList<>();

        for (CompoundSpectrum cs : compoundSpectra) {
            Iterator<CompoundEntity> iter = cs.getCompounds().iterator();
            while (iter.hasNext()) {
                CompoundEntity ce = iter.next();
                boolean filter = false;
                String notation = ce.getNotation(ce.getId());
                IAtomContainer molecule = Converter.lineNotationToCDK(notation);
                for (IAtom heavyAtom : AtomContainerManipulator.getHeavyAtoms(molecule)) {
                    if (!elements.contains(heavyAtom.getSymbol())) {
                        filter = true;
                        break;
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

    @Override
    public int removed() {
        return removed;
    }
}
