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

package uk.ac.ebi.masscascade.utilities;

import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import java.text.DecimalFormat;

/**
 * Class containing utility methods for chart handling.
 */
public class ChartUtils {

    /**
     * Binning modes.
     */
    public static enum BinningType {
        SUM, MAX, MIN, AVG
    }

    /**
     * This method bins values in the x-domain.
     *
     * @param xyList       XY-coordinates of the data
     * @param binRange     lower and upper bounds
     * @param numberOfBins number of bins
     * @param interpolate  if true, then empty bins will be filled with interpolation using other bins
     * @param binningType  Type of binning (sum of all 'y' within a bin, max of 'y', min of 'y', avg of 'y')
     * @return Values for each bin
     */
    public static double[] binValues(XYList xyList, Range binRange, int numberOfBins, boolean interpolate,
            BinningType binningType) {

        Double[] binValues = new Double[numberOfBins];
        double binWidth = binRange.getSize() / numberOfBins;

        double beforeX = Double.MIN_VALUE;
        double beforeY = 0.0f;
        double afterX = Double.MAX_VALUE;
        double afterY = 0.0f;

        double[] noOfEntries = null;

        for (int valueIndex = 0; valueIndex < xyList.size(); valueIndex++) {

            XYPoint xyPoint = xyList.get(valueIndex);

            if ((xyPoint.x - binRange.getLowerBounds()) < 0) {
                if (xyPoint.x > beforeX) {
                    beforeX = xyPoint.x;
                    beforeY = xyPoint.y;
                }
                continue;
            }

            // After last bin?
            if ((binRange.getUpperBounds() - xyPoint.x) < 0) {
                if (xyPoint.x < afterX) {
                    afterX = xyPoint.x;
                    afterY = xyPoint.y;
                }
                continue;
            }

            int binIndex = (int) ((xyPoint.x - binRange.getLowerBounds()) / binWidth);

            // in case x[valueIndex] is exactly lastBinStop, we would overflow the array
            if (binIndex == binValues.length) binIndex--;

            switch (binningType) {
                case MAX:
                    if (binValues[binIndex] == null) {
                        binValues[binIndex] = xyPoint.y;
                    } else {
                        if (binValues[binIndex] < xyPoint.y) {
                            binValues[binIndex] = xyPoint.y;
                        }
                    }
                    break;
                case MIN:
                    if (binValues[binIndex] == null) {
                        binValues[binIndex] = xyPoint.y;
                    } else {
                        if (binValues[binIndex] > xyPoint.y) {
                            binValues[binIndex] = xyPoint.y;
                        }
                    }
                    break;
                case AVG:
                    if (noOfEntries == null) {
                        noOfEntries = new double[binValues.length];
                    }
                    if (binValues[binIndex] == null) {
                        noOfEntries[binIndex] = 1;
                        binValues[binIndex] = xyPoint.y;
                    } else {
                        noOfEntries[binIndex]++;
                        binValues[binIndex] += xyPoint.y;
                    }
                    break;

                case SUM:
                default:
                    if (binValues[binIndex] == null) {
                        binValues[binIndex] = xyPoint.y;
                    } else {
                        binValues[binIndex] += xyPoint.y;
                    }
                    break;
            }
        }

        if (binningType.equals(BinningType.AVG)) {
            for (int binIndex = 0; binIndex < binValues.length; binIndex++) {
                if (binValues[binIndex] != null) {
                    binValues[binIndex] /= noOfEntries[binIndex];
                }
            }
        }

        if (interpolate) {

            for (int binIndex = 0; binIndex < binValues.length; binIndex++) {
                if (binValues[binIndex] == null) {

                    double leftNeighbourValue = beforeY;
                    int leftNeighbourBinIndex =
                            (int) java.lang.Math.floor((beforeX - binRange.getLowerBounds()) / binWidth);
                    for (int anotherBinIndex = binIndex - 1; anotherBinIndex >= 0; anotherBinIndex--) {
                        if (binValues[anotherBinIndex] != null) {
                            leftNeighbourValue = binValues[anotherBinIndex];
                            leftNeighbourBinIndex = anotherBinIndex;
                            break;
                        }
                    }

                    double rightNeighbourValue = afterY;
                    int rightNeighbourBinIndex = (binValues.length - 1) + (int) java.lang.Math.ceil(
                            (afterX - binRange.getUpperBounds()) / binWidth);
                    for (int anotherBinIndex = binIndex + 1; anotherBinIndex < binValues.length; anotherBinIndex++) {
                        if (binValues[anotherBinIndex] != null) {
                            rightNeighbourValue = binValues[anotherBinIndex];
                            rightNeighbourBinIndex = anotherBinIndex;
                            break;
                        }
                    }

                    double slope =
                            (rightNeighbourValue - leftNeighbourValue) / (rightNeighbourBinIndex -
                                    leftNeighbourBinIndex);
                    binValues[binIndex] = new Double(leftNeighbourValue + slope * (binIndex - leftNeighbourBinIndex));
                }
            }
        }

        double[] res = new double[binValues.length];
        for (int binIndex = 0; binIndex < binValues.length; binIndex++) {
            res[binIndex] = binValues[binIndex] == null ? 0 : binValues[binIndex];
        }
        return res;
    }

    private static DecimalFormat dec = new DecimalFormat("0.00");

    /**
     * Formats a double value to "0.00".
     *
     * @param x a double
     * @return the formatted string
     */
    public static String format(double x) {
        return dec.format(x);
    }
}
