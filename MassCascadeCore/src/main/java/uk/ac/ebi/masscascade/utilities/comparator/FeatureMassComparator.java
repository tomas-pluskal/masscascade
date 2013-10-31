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

package uk.ac.ebi.masscascade.utilities.comparator;

import uk.ac.ebi.masscascade.interfaces.Feature;

import java.util.Comparator;

/**
 * Class implementing a comparator for features. The features are compared by their m/z value.
 */
public class FeatureMassComparator implements Comparator<Feature> {

    /**
     * Compares two features by their m/z value.
     */
    public int compare(Feature feature1, Feature feature2) {

        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        if (feature1.getMz() < feature2.getMz()) return BEFORE;
        if (feature1.getMz() > feature2.getMz()) return AFTER;

        return EQUAL;
    }
}
