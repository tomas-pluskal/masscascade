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

package uk.ac.ebi.masscascade.distance;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

/**
 * Calculates the cosine angle between two profiles.
 */
public class CosineSimilarity {

    private int numBins;

    /**
     * Constructs the cosine similarity with the set number of bins for the calculation.
     *
     * @param bins the number of bins
     */
    public CosineSimilarity(int bins) {

        this.numBins = bins;
    }

    /**
     * Calculates the cosine angle between the two profiles using the binned intensity values.
     *
     * @param traceA the first profile
     * @param traceB the second profile
     * @return the cosine similarity
     */
    public double getDistance(XYList traceA, XYList traceB) {

        double[] dataA = new double[numBins];
        double[] dataB = new double[numBins];

        double distance = -1;

        if ((traceA != null) && (traceB != null)) {

            double min = Math.min(traceA.get(0).x, traceB.get(0).x);
            double max = Math.max(traceA.get(traceA.size() - 1).x, traceB.get(traceB.size() - 1).x);
            double width = (max - min) / numBins;

            for (int i = 1; i <= numBins; i++) {
                double lBound = min + (i - 1) * width;
                double rBound = min + i * width;
                double intensityA = 0.0;
                for (XYPoint dp : traceA) {
                    if (dp.x >= lBound) {
                        if (dp.x < rBound) {
                            intensityA += dp.y;
                        } else {
                            break;
                        }
                    }
                }
                double intensityB = 0.0;
                for (XYPoint dp : traceB) {
                    if (dp.x >= lBound) {
                        if (dp.x < rBound) {
                            intensityB += dp.y;
                        } else {
                            break;
                        }
                    }
                }
                dataA[i - 1] = intensityA;
                dataB[i - 1] = intensityB;
            }
            RealVector vecA = new ArrayRealVector(dataA);
            RealVector vecB = new ArrayRealVector(dataB);

            distance = vecA.cosine(vecB);
        }

        return distance;
    }

    /**
     * Getse the current number of bins.
     *
     * @return the number of bins
     */
    public int getNumBins() {

        return numBins;
    }

    /**
     * Sets the number of bins.
     *
     * @param numBins the number of bins
     */
    public void setNumBins(int numBins) {

        this.numBins = numBins;
    }
}
