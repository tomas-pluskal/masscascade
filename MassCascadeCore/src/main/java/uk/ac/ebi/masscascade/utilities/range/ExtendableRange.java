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

import java.io.Serializable;

/**
 * Class implementing a data range that can be extended on the fly. The default comparison behaviour uses the lower
 * bound of the range only.
 */
public class ExtendableRange implements Range, Serializable, Comparable<Range> {

    private static final long serialVersionUID = -6827623011162004934L;

    protected Double lowerBounds;
    protected Double upperBounds;

    /**
     * Constructs a default range.
     */
    public ExtendableRange() {

        lowerBounds = 0d;
        upperBounds = 0d;
    }

    /**
     * Constructs a custom range.
     *
     * @param limit a start value
     */
    public ExtendableRange(double limit) {

        this.lowerBounds = limit;
        this.upperBounds = limit;
    }

    /**
     * Constructs a custom range.
     *
     * @param lowerBounds a minimum value
     * @param upperBounds a maximum value
     */
    public ExtendableRange(double lowerBounds, double upperBounds) {

        this.lowerBounds = lowerBounds;
        this.upperBounds = upperBounds;
    }

    /**
     * Gets the minimum value.
     *
     * @return the minimum value
     */
    public double getLowerBounds() {
        return lowerBounds;
    }

    /**
     * Gets the maximum value.
     *
     * @return the maximum value
     */
    public double getUpperBounds() {
        return upperBounds;
    }

    /**
     * Gets the interval difference.
     *
     * @return the interval difference
     */
    public double getSize() {
        return (upperBounds - lowerBounds);
    }

    /**
     * Extends the range according to the provided value.
     *
     * @param limit a new minimum or maximum
     */
    public void extendRange(double limit) {

        if (limit > upperBounds) upperBounds = limit;
        if (limit < lowerBounds) lowerBounds = limit;
    }

    /**
     * Extends the range according to the provided range.
     *
     * @param range a range
     */
    public void extendRange(Range range) {

        if (range.getUpperBounds() > upperBounds) upperBounds = range.getUpperBounds();
        if (range.getLowerBounds() < lowerBounds) lowerBounds = range.getLowerBounds();
    }

    /**
     * Checks if a given value is within the defined range.
     * <p/>
     * Policy: [..[
     *
     * @param value a given value to be checked
     * @return boolean if inside range
     */
    public boolean contains(double value) {
        return (value >= lowerBounds && value < upperBounds);
    }

    /**
     * Returns the value that is getClosest to the range midpoint.
     *
     * @param value1 the first value
     * @param value2 the second value
     * @return the getClosest value
     */
    public double getClosest(double value1, double value2) {

        double midway = lowerBounds + (getSize() / 2d);
        return (Math.abs(midway - value1) <= Math.abs(midway - value2)) ? value1 : value2;
    }

    /**
     * Returns the midway point of the range.
     *
     * @return the midway point
     */
    public double getMean() {

        return (lowerBounds + upperBounds) / 2d;
    }

    @Override
    public int compareTo(Range range) {

        return (this.lowerBounds < range.getLowerBounds() ? -1 : (this.lowerBounds == range.getLowerBounds() ? 0 : 1));
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

    @Override
    public String toString() {
        return lowerBounds + "-" + upperBounds;
    }
}
