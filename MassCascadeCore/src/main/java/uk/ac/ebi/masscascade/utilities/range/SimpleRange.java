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

package uk.ac.ebi.masscascade.utilities.range;

import uk.ac.ebi.masscascade.interfaces.Range;

public class SimpleRange extends ExtendableRange{

    public SimpleRange(double lowerLimit, double upperLimit) {
        super(lowerLimit, upperLimit);
    }

    @Override
    public int compareTo(Range range) {

        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        if (this == range) return EQUAL;
        if (this.getLowerBounds() < range.getLowerBounds()) return BEFORE;
        if (this.getLowerBounds() > range.getLowerBounds()) return AFTER;
        if (this.getUpperBounds() < range.getUpperBounds()) return BEFORE;
        if (this.getUpperBounds() > range.getUpperBounds()) return AFTER;

        assert this.equals(range) : "compareTo inconsistent with equals";

        return EQUAL;
    }

    @Override
    public boolean equals(Object aRange) {

        if (this == aRange) return true;

        if (aRange == null) return false;
        if (!(aRange instanceof Range)) return false;

        Range range = (Range) aRange;
        return ((lowerBounds == range.getLowerBounds()) && (upperBounds == range.getUpperBounds()));
    }

    @Override
    public int hashCode() {

        int hash = 13;

        hash = hash * 17 + lowerBounds.hashCode();
        hash = hash * 17 + upperBounds.hashCode();

        return hash;
    }
}

