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

package uk.ac.ebi.masscascade.ws.massbank;

import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import java.util.Comparator;

/**
 * Comparator for MassBank Result instances. The query score is used for comparison (descending order).
 */
public class MassBankComparator implements Comparator<MassBankAPIStub.Result> {

    /**
     * Compares two MassBank results by their score.
     */
    @Override
    public int compare(MassBankAPIStub.Result result1, MassBankAPIStub.Result result2) {

        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        double score1 = Double.parseDouble(result1.getScore());
        double score2 = Double.parseDouble(result2.getScore());

        if (score1 > score2) return BEFORE;
        if (score1 < score2) return AFTER;

        return EQUAL;
    }
}
