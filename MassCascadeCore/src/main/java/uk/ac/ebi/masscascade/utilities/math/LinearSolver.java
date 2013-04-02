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

package uk.ac.ebi.masscascade.utilities.math;

/**
 * Gauss-Jordan implementation for linear equations.
 */
public class LinearSolver {

    private int pivotRow, pivotCol;

    /**
     * Solves the augmented matrix and writes the solution into the matrix.
     *
     * @param matrix    a matrix representing the linear equations and all factors
     * @param gaussOnly if gauss solution only
     */
    public void solve(Matrix matrix, boolean gaussOnly) {

        pivotRow = pivotCol = -1;
        doGauss(matrix);
        if (!gaussOnly) doGaussJordan(matrix);
    }

    /**
     * Executes Gauss solution.
     *
     * @param matrix the augmented matrix
     */
    private void doGauss(Matrix matrix) {

        for (int rank = 0, nbRow = matrix.getNbRow(); rank < nbRow; rank++) {

            //step 1: find pivot and swap it to first row
            findPivot(matrix, rank);
            if (pivotRow < 0) return;
            if (pivotRow != rank) matrix.swapRow(pivotRow, rank);

            //step 2: leading value set to 1
            double leadingValue = matrix.get(rank, pivotCol);
            if (leadingValue != 1) {
                matrix.multiplyRow(rank, 1.0 / leadingValue);
                //force value to 1 for better precision
                matrix.set(rank, pivotCol, 1.0);
            }

            //step 3: reducing other rows
            for (int i = rank + 1; i < nbRow; i++) {
                leadingValue = matrix.get(i, pivotCol);
                if (leadingValue != 0) {
                    matrix.combineRow(i, rank, -leadingValue);
                    //force value to exactly 0
                    matrix.set(i, pivotCol, 0.0);
                }
            }
        }
    }

    /**
     * Finds the pivot given a rank.
     *
     * @param matrix the augmented matrix
     * @param rank   the rank
     */
    private void findPivot(Matrix matrix, int rank) {

        for (int col = pivotCol + 1, nbCol = matrix.getNbCol(); col < nbCol; col++) {
            for (int row = rank, nbRow = matrix.getNbRow(); row < nbRow; row++) {
                if (matrix.get(row, col) != 0) {
                    pivotRow = row;
                    pivotCol = col;
                    return;
                }
            }
        }
        pivotRow = pivotCol = -1;
    }

    /**
     * Executes Gauss-Jordan solution.
     *
     * @param matrix the augmented matrix
     */
    private void doGaussJordan(Matrix matrix) {

        for (int row = matrix.getNbRow() - 1; row > 0; row--) {
            //find leading 1
            int col = findLeadingValue(matrix, row);
            if (col == -1) continue;

            //reduce all other rows
            for (int i = row - 1; i >= 0; i--) {
                double value = matrix.get(i, col);
                if (value != 0) matrix.combineRow(i, row, -value);
            }
        }
    }

    /**
     * Finds the first non null value in the row.
     *
     * @param matrix the augmented matrix
     * @param row    the row
     * @return the row index of the first non null value
     */
    private int findLeadingValue(Matrix matrix, int row) {

        for (int i = 0, nbCol = matrix.getNbCol(); i < nbCol; i++)
            if (matrix.get(row, i) != 0.0) return i;
        return -1;
    }
}
