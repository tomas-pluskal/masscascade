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
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

/**
 * Class defining a range in the time domain representing a window used for Biehman deconvolution.
 */
public class BiehmanWindow {

    private XYPoint maxDp;
    private int maxDpIndex;

    private XYList xicData;
    private int oriLeftBoundary;
    private int oriRightBoundary;
    private double noiseEstimate;

    private XYPoint rightMinDp;
    private int rightMinDpIndex;
    private int rightBoundary;

    private XYPoint leftMinDp;
    private int leftMinDpIndex;
    private int leftBoundary;

    private static final double MIN_PERCENT = 0.01;

    /**
     * Constructs a Biehman Deconvolution window.
     *
     * @param xicData          the data from the extracted ion chromatogram
     * @param oriLeftBoundary  the left bound
     * @param oriRightBoundary the right bound
     * @param noiseEstimate    the noise estimate
     */
    public BiehmanWindow(XYList xicData, int oriLeftBoundary, int oriRightBoundary, double noiseEstimate) {

        this.xicData = xicData;

        this.maxDp = xicData.get(oriLeftBoundary);
        this.maxDpIndex = oriLeftBoundary;

        this.oriLeftBoundary = oriLeftBoundary;
        this.oriRightBoundary = oriRightBoundary;

        findMaximumDp();

        this.noiseEstimate = noiseEstimate;

        rightMinDp = maxDp;
        rightMinDpIndex = maxDpIndex;
        rightBoundary = maxDpIndex;

        leftMinDp = maxDp;
        leftMinDpIndex = maxDpIndex;
        leftBoundary = maxDpIndex;

        estimateWindow();
    }

    /**
     * Finds the maximum data point within the range.
     */
    private void findMaximumDp() {

        for (int i = oriLeftBoundary; i <= oriRightBoundary; i++) {
            if (maxDp.y < xicData.get(i).y) {
                maxDp = xicData.get(i);
                maxDpIndex = i;
            }
        }
    }

    /**
     * Perceives the Biehman deconvolution window.
     */
    private void estimateWindow() {

        double threshold = maxDp.y * MIN_PERCENT;

        XYPoint curDp;

        int windowLength = oriRightBoundary - maxDpIndex;
        for (int i = maxDpIndex + 1; i <= (maxDpIndex + windowLength); i++) {

            curDp = xicData.get(i);
            if (curDp.y < threshold) {
                rightBoundary = i;
                rightMinDp = curDp;
                rightMinDpIndex = i;
                break;
            } else if (curDp.y < rightMinDp.y) {
                rightMinDp = curDp;
                rightMinDpIndex = i;
            } else if (curDp.y > noiseEstimate * Math.sqrt(rightMinDp.y)) {
                rightBoundary = i - 1;
                break;
            }
            rightBoundary = i;
        }

        windowLength = oriLeftBoundary;
        for (int i = maxDpIndex - 1; i >= windowLength; i--) {

            curDp = xicData.get(i);
            if (curDp.y < threshold) {
                leftBoundary = i;
                leftMinDp = curDp;
                leftMinDpIndex = i;
                break;
            } else if (curDp.y < leftMinDp.y) {
                leftMinDp = curDp;
                leftMinDpIndex = i;
            } else if (curDp.y > noiseEstimate * Math.sqrt(leftMinDp.y)) {
                leftBoundary = i + 1;
                break;
            }
            leftBoundary = i;
        }
    }

    /**
     * Returns the maximum data point.
     *
     * @return the max. data point
     */
    public XYPoint getMaxDp() {
        return maxDp;
    }

    /**
     * Returns the index of the maximum data point.
     *
     * @return the index of the max. data point
     */
    public int getMaxDpIndex() {
        return maxDpIndex;
    }

    /**
     * Returns the minimum data point on the right side.
     *
     * @return the min. data point
     */
    public XYPoint getRightMinDp() {
        return rightMinDp;
    }

    /**
     * Returns the minimum data point index of the right side.
     *
     * @return the min. data point index.
     */
    public int getRightMinDpIndex() {
        return rightMinDpIndex;
    }

    /**
     * Returns the index of the right boundary.
     *
     * @return the index
     */
    public int getRightBoundary() {
        return rightBoundary;
    }

    /**
     * eturns the minimum data point on the left side.
     *
     * @return the min. data point
     */
    public XYPoint getLeftMinDp() {
        return leftMinDp;
    }

    /**
     * Returns the minimum data point index of the left side.
     *
     * @return the min. data point index
     */
    public int getLeftMinDpIndex() {
        return leftMinDpIndex;
    }

    /**
     * Returns the index of the left boundary.
     *
     * @return the left boundary
     */
    public int getLeftBoundary() {
        return leftBoundary;
    }
}
