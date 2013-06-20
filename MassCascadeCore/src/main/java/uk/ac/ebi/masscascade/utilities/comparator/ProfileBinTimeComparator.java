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

import uk.ac.ebi.masscascade.alignment.profilebins.ProfileBin;

import java.util.Comparator;

/**
 * Class implementing a comparator for profile bin objects. The bins are compared by their retention time.
 */
public class ProfileBinTimeComparator implements Comparator<ProfileBin> {

    /**
     * Compares two profile bins by their retention time.
     */
    public int compare(ProfileBin bin1, ProfileBin bin2) {

        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        if (bin1.getRt() < bin2.getRt()) return BEFORE;
        if (bin1.getRt() > bin2.getRt()) return AFTER;

        return EQUAL;
    }
}
