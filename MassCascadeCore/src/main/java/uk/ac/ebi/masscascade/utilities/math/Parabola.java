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
 * Class implementing a parabola solver based on three data points.
 */
public class Parabola {

    private LinearSolver solver = new LinearSolver();
    private Matrix matrix = new Matrix(3, 4);

    /**
     * Returns a quadratic equation for the parabola that is described by the three data points.
     *
     * @param p1 the first data point
     * @param p2 the second data point
     * @param p3 the third data point
     * @return the quadratic equation
     */
    public QuadraticEquation solveParabola(XYPoint p1, XYPoint p2, XYPoint p3) {

        if (p1.x == p2.x || p1.x == p3.x || p2.x == p3.x) return new QuadraticEquation(0, 0, 0);

        matrix.set(0, 0, p1.x * p1.x);
        matrix.set(0, 1, p1.x);
        matrix.set(0, 2, 1);
        matrix.set(0, 3, p1.y);

        matrix.set(1, 0, p2.x * p2.x);
        matrix.set(1, 1, p2.x);
        matrix.set(1, 2, 1);
        matrix.set(1, 3, p2.y);

        matrix.set(2, 0, p3.x * p3.x);
        matrix.set(2, 1, p3.x);
        matrix.set(2, 2, 1);
        matrix.set(2, 3, p3.y);

        solver.solve(matrix, false);

        return new QuadraticEquation(matrix.get(0, 3), matrix.get(1, 3), matrix.get(2, 3));
    }
}
