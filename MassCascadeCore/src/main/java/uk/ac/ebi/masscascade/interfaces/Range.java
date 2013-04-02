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

package uk.ac.ebi.masscascade.interfaces;

/**
 * This is a range defined by a lower and upper bound.
 */
public interface Range extends Comparable<Range> {

    /**
     * Returns the lower limit of the range.
     *
     * @return the lower limit
     */
    double getLowerBounds();

    /**
     * Returns the upper limit of the range.
     *
     * @return the upper limit
     */
    double getUpperBounds();

    /**
     * Returns the span of the range.
     *
     * @return the span
     */
    double getSize();

    /**
     * Extends the range given a greater / lower limit.
     *
     * @param limit a new min / max limit
     */
    void extendRange(double limit);

    /**
     * Extends the range given another range.
     *
     * @param range a range
     */
    void extendRange(Range range);

    /**
     * Checks if a given value is within the defined range.
     *
     * @param value the given value to be checked
     * @return boolean if inside range
     */
    boolean contains(double value);

    /**
     * Returns the value that is getClosest to the range midpoint.
     *
     * @param value1 the first value
     * @param value2 the second value
     * @return the getClosest value
     */
    double getClosest(double value1, double value2);

    /**
     * Returns the midway point of the range.
     *
     * @return the midway point
     */
    double getMean();
}
