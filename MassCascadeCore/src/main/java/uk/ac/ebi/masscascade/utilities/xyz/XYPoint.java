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

package uk.ac.ebi.masscascade.utilities.xyz;

import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;

import java.io.Serializable;

/**
 * Class implementing a 2D data point. The implementation's default comparison uses the x value only.
 */
public class XYPoint implements Comparable<XYPoint>, Serializable {

    private static final long serialVersionUID = 4650855209472738308L;

    public double x;
    public double y;

    /**
     * Constructs a data point where x = 0 and y = 0.
     */
    public XYPoint() {

        x = 0;
        y = 0;
    }

    /**
     * Constructs a custom data point.
     *
     * @param x the x value
     * @param y the y value
     */
    public XYPoint(double x, double y) {

        this.x = x;
        this.y = y;
    }

    /**
     * Calculates the x range for a given tolerance.
     *
     * @param ppm the tolerance value in ppm
     * @return the x tolerance range
     */
    public Range getRange(double ppm) {

        double lower = x - (x * ppm / Constants.PPM);
        double upper = x + (x * ppm / Constants.PPM);

        return new ExtendableRange(lower, upper);
    }

    @Override
    public int hashCode() {

        final int PRIME = 31;
        int result = 1;
        result += PRIME * result + x;
        result += PRIME * result + y;

        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;

        if (obj == null) return false;

        if (getClass() != obj.getClass()) return false;

        final XYPoint other = (XYPoint) obj;
        return !(this.x != other.x || this.y != other.y);
    }

    @Override
    public int compareTo(XYPoint dp) {

        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        if (this == dp) return EQUAL;
        if (x < dp.x) return BEFORE;
        if (x > dp.x) return AFTER;

        return EQUAL;
    }
}
