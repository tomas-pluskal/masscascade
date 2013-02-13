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
 * Class representing a quadratic equation.
 */
public class QuadraticEquation {

    private double a;
    private double b;
    private double c;

    /**
     * Constructs a quadratic equation.
     *
     * @param a the first coefficient
     * @param b the second coefficient
     * @param c the third coefficient
     */
    public QuadraticEquation(double a, double b, double c) {

        this.a = a;
        this.b = b;
        this.c = c;
    }

    /**
     * Returns y for a given x.
     *
     * @param x a x value
     * @return the y value
     */
    public double getY(double x) {
        return a * (x * x) + b * x + c;
    }

    /**
     * Returns the xy data point for x.
     *
     * @param x a x value
     * @return the corresponding xy data point
     */
    public XYPoint getDpY(double x) {
        return new XYPoint(x, getY(x));
    }
}
