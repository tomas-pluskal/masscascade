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

package uk.ac.ebi.masscascade.utilities.range;

import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.parameters.Constants;

public class MovingRange extends ToleranceRange {

    // number of data points and average
    private int n;
    private double mean;

    private int id;
    private double ppm;

    public MovingRange() {

        super(0, 0);
    }

    /**
     * Constructs a moving range according to x +/- (x * ppm / 10^6).
     *
     * @param x   a x value
     * @param ppm a ppm value
     */
    public MovingRange(int id, double x, double ppm) {

        super(x, ppm);

        this.id = id;
        this.ppm = ppm;

        n = 1;
        mean = x;
    }

    public int getId() {
        return id;
    }

    /**
     * Extends the range according to the updated average after adding the value.
     *
     * @param value a new value
     */
    @Override
    public void extendRange(double value) {

        mean = ((mean * n) + value) / (n + 1);
        n++;

        lowerBounds = mean - (mean * ppm / Constants.PPM);
        upperBounds = mean + (mean * ppm / Constants.PPM);
    }

    @Override
    public void extendRange(Range range) {

        throw new MassCascadeException("Method not implemented.");
    }

    /**
     * Returns the mean.
     *
     * @return the mean
     */
    @Override
    public double getMean() {
        return mean;
    }

    @Override
    public boolean equals(Object aRange) {

        if (this == aRange) return true;

        if (!(aRange instanceof MovingRange)) return false;
        if (aRange == null) return false;

        MovingRange range = (MovingRange) aRange;
        return (id == range.getId());
    }

    @Override
    public int hashCode() {

        int hash = 23;

        hash = hash * 37 + id;

        return hash;
    }
}
