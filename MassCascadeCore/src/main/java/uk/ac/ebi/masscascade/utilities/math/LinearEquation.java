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

package uk.ac.ebi.masscascade.utilities.math;

import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

/**
 * Class representing a linear equation (m * x + b).
 */
public class LinearEquation {

    private double m;
    private double b;

    /**
     * Constructs a linear equation of type m * x + b.
     *
     * @param m a slope
     * @param b the y-intercept
     */
    public LinearEquation(double m, double b) {

        this.m = m;
        this.b = b;
    }

    /**
     * Constructs a linear equation of type m * x + b.
     *
     * @param dp1 the first xy data point
     * @param dp2 the second xy data point
     */
    public LinearEquation(XYPoint dp1, XYPoint dp2) {

        m = (dp2.y - dp1.y) / (dp2.x - dp1.x);
        b = dp2.y - (m * dp2.x);
    }

    /**
     * Get y for a given x.
     *
     * @param x a x value
     * @return the y value
     */
    public double getY(double x) {
        return (m * x) + b;
    }

    /**
     * Get x for a given y.
     *
     * @param y a y value
     * @return the x value
     */
    public double getX(double y) {
        return (y - b) / m;
    }
}
