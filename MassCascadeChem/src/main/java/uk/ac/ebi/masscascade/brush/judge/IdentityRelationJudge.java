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

import com.google.common.collect.Multimap;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import uk.ac.ebi.masscascade.compound.CompoundEntity;
import uk.ac.ebi.masscascade.compound.CompoundSpectrum;
import uk.ac.ebi.masscascade.properties.Adduct;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Judge resolving the signal to signal adduct and neutral loss relationships.
 * <p/>
 * The judge can increase the total score of a compound entity by a maximum of 100.
 */
public class IdentityRelationJudge implements Judge {

    // the logger instance
    private static final Logger LOGGER = Logger.getLogger(IdentityRelationJudge.class);

    // the number of removed compound spectra
    private int removed = 0;

    /**
     * The core method of the judge executing the filtering process.
     *
     * @param compoundSpectra the input list of compound spectra
     * @return the filtered input list
     */
    @Override
    public List<CompoundSpectrum> judge(List<CompoundSpectrum> compoundSpectra) {

        LOGGER.log(Level.DEBUG, "Starting Relation Judge...");

        List<CompoundSpectrum> filteredCS = new ArrayList<>();

        for (CompoundSpectrum cs : compoundSpectra) {

            Multimap<Integer, Adduct> indexToAdduct = cs.getIndexToAdduct();
            int score = 0;
            if (indexToAdduct.size() == 0) {
                // fall through
            } else if (indexToAdduct.size() < 2) {
                score = 50;
            } else {
                score = 100;
            }

            for (CompoundEntity ce : cs.getCompounds()) {
                ce.addScore(score);
            }

            filteredCS.add(cs);
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