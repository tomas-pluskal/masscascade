/*
 * Copyright (c) 2013, Stephan Beisken. All rights reserved.
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
 */

package uk.ac.ebi.masscascade.utilities.comparator;

import uk.ac.ebi.masscascade.interfaces.Profile;

import java.util.Comparator;

/**
 * Class implementing a comparator for profiles. The profiles are compared by their m/z value.
 */
public class ProfileMassComparator implements Comparator<Profile> {

    /**
     * Compares two profiles by their m/z value.
     */
    public int compare(Profile profile1, Profile profile2) {

        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        if (profile1.getMz() < profile2.getMz()) return BEFORE;
        if (profile1.getMz() > profile2.getMz()) return AFTER;

        return EQUAL;
    }
}
