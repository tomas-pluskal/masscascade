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

package uk.ac.ebi.masscascade.deconvolution;

import uk.ac.ebi.masscascade.utilities.xyz.XYList;

public final class SavitzkyGolayDerivative {

    /**
     * This method returns the second smoothed derivative values of an array.
     *
     * @param data            values
     * @param firstDerivative is first derivative
     * @param levelOfFilter   level of filter (1 - 12)
     * @return double[] derivative of values
     */
    public static double[] calculateDerivative(XYList data, boolean firstDerivative, int levelOfFilter) {

        double[] derivative = new double[data.size()];
        int M = 0;

        for (int k = 0; k < derivative.length; k++) {

            // Determine boundaries
            if (k <= levelOfFilter) M = k;
            if (k + M > derivative.length - 1) M = derivative.length - (k + 1);

            // Perform derivative using Savitzky Golay coefficients
            for (int i = -M; i <= M; i++) {
                derivative[k] += data.get(k + i).y * getSGCoefficient(M, i, firstDerivative);
            }
            // if ((Math.abs(derivative[k])) > maxValueDerivative)
            // maxValueDerivative = Math.abs(derivative[k]);

        }

        return derivative;
    }

    /**
     * This method return the Savitzky-Golay 2nd smoothed derivative coefficient
     * from an array
     *
     * @param M
     * @param signedC
     * @return
     */
    private static Double getSGCoefficient(int M, int signedC, boolean firstDerivate) {

        int C = Math.abs(signedC), sign = 1;
        if (firstDerivate) {
            if (signedC < 0) sign = -1;
            return sign * SavitzkyGolayCoefficients.SGCoefficientsFirstDerivativeQuartic[M][C];
        } else {
            return SavitzkyGolayCoefficients.SGCoefficientsSecondDerivative[M][C];
        }
    }
}

