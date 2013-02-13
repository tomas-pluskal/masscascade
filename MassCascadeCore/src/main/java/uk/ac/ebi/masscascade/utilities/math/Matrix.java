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

/**
 * Implementation of a matrix.
 */
public class Matrix {

    private double[][] values;
    private int nbRow, nbCol;

    /**
     * Constructs a matrix.
     *
     * @param values a row-column data array
     */
    public Matrix(double[][] values) {

        this.values = values;
        nbRow = values.length;
        nbCol = values[0].length;
    }

    /**
     * Constructs an empty matrix.
     *
     * @param m max. row number
     * @param n max. col number
     */
    public Matrix(int m, int n) {

        values = new double[m][n];
        nbRow = m;
        nbCol = n;
    }

    /**
     * Returns the value in row m and column n.
     *
     * @param m a row
     * @param n a column
     * @return the matrix value
     */
    public double get(int m, int n) {
        return values[m][n];
    }

    /**
     * Sets the value in row m and column n.
     *
     * @param m a row
     * @param n a column
     * @param v the matrix value
     */
    public void set(int m, int n, double v) {
        values[m][n] = v;
    }

    /**
     * Swaps two rows with each other.
     *
     * @param i a first row
     * @param j a second row
     */
    public void swapRow(int i, int j) {

        if (i == j) return;

        double[] row = values[i];
        values[i] = values[j];
        values[j] = row;
    }

    /**
     * Multiplies a row by a factor.
     *
     * @param i      a row
     * @param factor a factor
     */
    public void multiplyRow(int i, double factor) {

        double[] row = values[i];
        for (int j = 0, n = row.length; j < n; j++)
            row[j] *= factor;
    }

    /**
     * Moves one row to another and multiplies it by a factor.
     *
     * @param to     a destination row
     * @param from   a source row
     * @param factor a factor
     */
    public void combineRow(int to, int from, double factor) {

        double[] rowTo = values[to];
        double[] rowFrom = values[from];
        for (int i = 0, n = rowTo.length; i < n; i++)
            rowTo[i] += factor * rowFrom[i];
    }

    /**
     * Gets the number of rows.
     *
     * @return the no. of rows
     */
    public int getNbRow() {
        return nbRow;
    }

    /**
     * Gets the number of columns.
     *
     * @return the no. of columns
     */
    public int getNbCol() {
        return nbCol;
    }
}
