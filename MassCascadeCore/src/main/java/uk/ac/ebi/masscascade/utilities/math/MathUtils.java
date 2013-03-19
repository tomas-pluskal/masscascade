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

import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.utilities.xyz.XYZList;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;
import uk.ac.ebi.masscascade.utilities.xyz.XYZPoint;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Class providing math utility functions.
 */
public class MathUtils {

    public static final DecimalFormat SCIENTIFIC_FORMAT = new DecimalFormat("##0.00E0#;(##0.00E0#)");
    public static final DecimalFormat THREE_DECIMAL_FORMAT = new DecimalFormat("##0.000#;(##0.000#)");

    /**
     * Creates a range according to an initial value and its tolerance value (absolute).
     *
     * @param x   an initial x value
     * @param abs a tolerance [abs]
     * @return the tolerance range
     */
    public static Range getRangeFromAbs(double x, double abs) {
        return new ExtendableRange(x - abs, x + abs);
    }

    /**
     * Creates a range according to an initial value and its tolerance value (ppm).
     *
     * @param x   an initial x value
     * @param ppm the tolerance [ppm]
     * @return the tolerance range
     */
    public static Range getRangeFromPPM(double x, double ppm) {
        return getRangeFromAbs(x, ppm * x / Constants.PPM);
    }

    /**
     * Returns the absolute tolerance value.
     *
     * @param x   an inital x value
     * @param abs the tolerance [ppm]
     * @return the absolute tolerance value
     */
    public static double getAbsTolerance(double x, double abs) {
        return (x * abs / Constants.PPM);
    }

    /**
     * Returns the median from a sorted array of values.
     *
     * @param valueArray an array of values
     */
    public static double getMedian(double[] valueArray) {

        double median;
        int arrayLength = valueArray.length;
        if (valueArray.length % 2 == 0) {
            median = valueArray[arrayLength / 2];
        } else {
            int low = (int) Math.floor(arrayLength / 2);
            int high = (int) Math.ceil(arrayLength / 2);
            median = (valueArray[low] + valueArray[high]) / 2;
        }

        return median;
    }

    /**
     * Returns the median from a sorted array of values.
     *
     * @param valueArray an array of values
     */
    public static double getMedian(List<Double> valueArray) {

        double median;
        int arrayLength = valueArray.size();
        if (valueArray.size() % 2 == 0) {
            median = valueArray.get(arrayLength / 2);
        } else {
            int low = (int) Math.floor(arrayLength / 2);
            int high = (int) Math.ceil(arrayLength / 2);
            median = (valueArray.get(low) + valueArray.get(high)) / 2;
        }

        return median;
    }

    /**
     * Convenience method to format a double value to three digit precision.
     */
    public static double roundToThreeDecimals(double value) {
        return ((int) (value * 1000)) / 1000.0;
    }

    /**
     * Returns the mean y value of the given x y point list.
     *
     * @param data the x y point list
     * @return the mean y value
     */
    public static double getMeanY(List<XYPoint> data) {

        double mean = 0;
        for (XYPoint dp : data) mean += dp.y;

        return (mean / data.size());
    }

    /**
     * Returns if the first value is closer to the target value than the second value.
     *
     * @param target a target value
     * @param value1 a first value
     * @param value2 a second value
     * @return if value 1 is closer to target than value 2
     */
    public static boolean isAbsClosest(double target, double value1, double value2) {
        return (Math.abs(target - value1) <= Math.abs(target - value2));
    }

    /**
     * Performs least squares estimation on the data points.
     *
     * @param data the input data
     * @return the resulting linear equation
     */
    public static LinearEquation getLeastSquares(List<XYPoint> data) {

        double sumx = 0d;
        double sumy = 0d;

        double count = 0;
        for (XYPoint dp : data) {
            sumx += dp.x;
            sumy += dp.y;
            count++;
        }

        double xbar = sumx / count;
        double ybar = sumy / count;

        double xxbar = 0d;
        double xybar = 0d;

        for (XYPoint dp : data) {
            xxbar += (dp.x - xbar) * (dp.x - xbar);
            xybar += (dp.x - xbar) * (dp.y - ybar);
        }
        double mSq = xybar / xxbar;
        double bSq = ybar - mSq * xbar;

        return new LinearEquation(mSq, bSq);
    }

    /**
     * Gets the vertex from a parabola fitted through three points.
     *
     * @param dpL   the left data point
     * @param maxDp the middle data point
     * @param dpR   the right data point
     * @return the resulting vertex
     */
    public static XYPoint getParabolaVertex(XYPoint dpL, XYPoint maxDp, XYPoint dpR) {

        double denom = (dpL.x - maxDp.x) * (dpL.x - dpR.x) * (maxDp.x - dpR.x);
        double aPol = (dpR.x * (maxDp.y - dpL.y) + maxDp.x * (dpL.y - dpR.y) +
                dpL.x * (dpR.y - maxDp.y)) / denom;
        double bPol = (dpR.x * dpR.x * (dpL.y - maxDp.y) +
                maxDp.x * maxDp.x * (dpR.y - dpL.y) +
                dpL.x * dpL.x * (maxDp.y - dpR.y)) / denom;
        double cPol = (maxDp.x * dpR.x * (maxDp.x - dpR.x) * dpL.y +
                dpR.x * dpL.x * (dpR.x - dpL.x) * maxDp.y +
                dpL.x * maxDp.x * (dpL.x - maxDp.x) * dpR.y) / denom;

        return new XYPoint(-bPol / (2 * aPol), cPol - bPol * bPol / (4 * aPol));
    }

    /**
     * Gets the vertex from a parabola fitted through three points.
     *
     * @param dpL   the left data point
     * @param maxDp the middle data point
     * @param dpR   the right data point
     * @return the resulting vertex
     */
    public static XYPoint getParabolaVertex(XYZPoint dpL, XYZPoint maxDp, XYZPoint dpR) {

        double denom = (dpL.x - maxDp.x) * (dpL.x - dpR.x) * (maxDp.x - dpR.x);
        double aPol = (dpR.x * (maxDp.z - dpL.z) + maxDp.x * (dpL.z - dpR.z) +
                dpL.x * (dpR.z - maxDp.z)) / denom;
        double bPol = (dpR.x * dpR.x * (dpL.z - maxDp.z) +
                maxDp.x * maxDp.x * (dpR.z - dpL.z) +
                dpL.x * dpL.x * (maxDp.z - dpR.z)) / denom;
        double cPol = (maxDp.x * dpR.x * (maxDp.x - dpR.x) * dpL.z +
                dpR.x * dpL.x * (dpR.x - dpL.x) * maxDp.z +
                dpL.x * maxDp.x * (dpL.x - maxDp.x) * dpR.z) / denom;

        return new XYPoint(-bPol / (2 * aPol), cPol - bPol * bPol / (4 * aPol));
    }

    public static XYList getMeanCenteredData(XYList data) {

        double mean = MathUtils.getMeanY(data);

        XYList meanCenteredData = new XYList();
        for (XYPoint dp : data) {
            meanCenteredData.add(new XYPoint(dp.x, dp.y - mean));
        }

        return meanCenteredData;
    }

    private XYList data;

    /**
     * Gets the area under the curve using a simple data point driven approach.
     */
    public static double getTrapezoidArea(XYZList data) {

        double dX;
        double dYTri;
        double dYRec;
        double area = 0;

        for (int i = 1; i < data.size(); i++) {
            dX = data.get(i).x - data.get(i - 1).x;
            dYTri = Math.abs(data.get(i).z - data.get(i - 1).z);
            dYRec = data.get(i - 1).z;
            area += ((dX * dYTri / 2d) + (dX * dYRec));
        }

        return area;
    }
}
