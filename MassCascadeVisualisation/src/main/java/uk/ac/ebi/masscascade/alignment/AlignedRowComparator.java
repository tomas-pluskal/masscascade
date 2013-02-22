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

package uk.ac.ebi.masscascade.alignment;

import java.util.Comparator;

/**
 * A comparator for {@link AlignedRow} objects. The rows objects are compared by their m/z and retention time values.
 */
public class AlignedRowComparator implements Comparator<AlignedRow> {

    private double timeWindow;

    /**
     * Constructs a comparator with a absolute time tolerance value.
     *
     * @param timeWindow the time tolerance value
     */
    public AlignedRowComparator(double timeWindow) {
        this.timeWindow = timeWindow / 2d;
    }

    /**
     * Compares two row objects based on their m/z and retention time values.
     *
     * @param row1 the first row to be compared to
     * @param row2 the second row for comparison
     * @return the comparison result
     */
    @Override
    public int compare(AlignedRow row1, AlignedRow row2) {

        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        if (row1.getMz() < row2.getMz() && isWithinRt(row1, row2)) return BEFORE;
        if (row1.getMz() > row2.getMz() && isWithinRt(row1, row2)) return AFTER;
        if (row1.getMz() < row2.getMz()) return BEFORE;
        if (row1.getMz() > row2.getMz()) return AFTER;

        return EQUAL;
    }

    /**
     * Tests if rt values of two rows are within the defined rt tolerance range of each other.
     *
     * @param alignedRow the target row to be compared to
     * @param row        the query row to be used for comparison
     * @return if the row is in range of the aligned row
     */
    private boolean isWithinRt(AlignedRow alignedRow, AlignedRow row) {
        return alignedRow.getRt() - timeWindow <= row.getRt() && alignedRow.getRt() + timeWindow > row.getRt();
    }
}
