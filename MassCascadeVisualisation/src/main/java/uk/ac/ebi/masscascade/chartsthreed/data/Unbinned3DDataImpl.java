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

package uk.ac.ebi.masscascade.chartsthreed.data;

import org.freehep.j3d.plot.Binned2DData;
import org.freehep.j3d.plot.Unbinned3DData;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;

import javax.vecmath.Color3b;
import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Unbinned3DDataImpl implements Unbinned3DData {

    private static final int X = 1;
    private static final int Y = 2;
    private static final int Z = 3;

    private static final int SCALE_FACTOR = 1000;

    private List<Point3f> unbinnedDataPoints;

    private Range xRange;
    private Range yRange;

    public Unbinned3DDataImpl() {

        unbinnedDataPoints = new ArrayList<Point3f>();

        xRange = new ExtendableRange(Double.MAX_VALUE, Double.MIN_VALUE);
        yRange = new ExtendableRange(Double.MAX_VALUE, Double.MIN_VALUE);
    }

    public void addDataPoint(Point3f dataPoint) {

        unbinnedDataPoints.add(dataPoint);

        xRange.extendRange(dataPoint.x);
        yRange.extendRange(dataPoint.y);
    }

    public void clearDataPoints() {

        unbinnedDataPoints.clear();

        xRange = new ExtendableRange(Double.MAX_VALUE, Double.MIN_VALUE);
        yRange = new ExtendableRange(Double.MAX_VALUE, Double.MIN_VALUE);
    }

    private float getMinValue(int xyz) {

        float min = Float.MAX_VALUE;

        if (xyz == X) {
            for (Point3f xyzDataPoint : unbinnedDataPoints) {
                if (xyzDataPoint.x < min) min = xyzDataPoint.x;
            }
        } else if (xyz == Y) {
            for (Point3f xyzDataPoint : unbinnedDataPoints) {
                if (xyzDataPoint.y < min) min = xyzDataPoint.y;
            }
        } else if (xyz == Z) {
            for (Point3f xyzDataPoint : unbinnedDataPoints) {
                if (xyzDataPoint.z < min) min = xyzDataPoint.z;
            }
        }

        return min;
    }

    private float getMaxValue(int xyz) {

        float max = Float.MIN_VALUE;

        if (xyz == X) {
            for (Point3f xyzDataPoint : unbinnedDataPoints) {
                if (xyzDataPoint.x > max) max = xyzDataPoint.x;
            }
        } else if (xyz == Y) {
            for (Point3f xyzDataPoint : unbinnedDataPoints) {
                if (xyzDataPoint.y > max) max = xyzDataPoint.y;
            }
        } else if (xyz == Z) {
            for (Point3f xyzDataPoint : unbinnedDataPoints) {
                if (xyzDataPoint.z > max) max = xyzDataPoint.z;
            }
        }

        return max;
    }

    /**
     * Axis minimum on the X Axis.
     */
    public float xMin() {

        return getMinValue(X);
    }

    /**
     * Axis maximum on the X Axis.
     */
    public float xMax() {

        return getMaxValue(X);
    }

    /**
     * Axis minimum on the Y Axis.
     */
    public float yMin() {

        return getMinValue(Y);
    }

    /**
     * Axis maximum on the Y Axis.
     */
    public float yMax() {

        return getMaxValue(Y);
    }

    /**
     * Minimum data value on the Z Axis.
     */
    public float zMin() {

        return getMinValue(Z);
    }

    /**
     * Maximum data value on the Z Axis.
     */
    public float zMax() {

        return getMaxValue(Z);
    }

    /**
     * The number of points in the scatter plot.
     */
    public int getNPoints() {

        return unbinnedDataPoints.size();
    }

    /**
     * The x,y,z coordinate of the specified point.
     */
    public Point3f pointAt(int i) {

        if (unbinnedDataPoints.size() < i) return unbinnedDataPoints.get(i);

        return new Point3f();
    }

    /**
     * Get the Color of the specified point.
     */
    public Color3b colorAt(int i) {

        return new Color3b(Color.BLUE);
    }

    public Binned2DData getBinned2DData(int xBins, int yBins) {

        float xBinWidth = getBinWidth(X, xBins);
        float yBinWidth = getBinWidth(Y, yBins);

        float offsetX = (float) xRange.getLowerBounds();
        float offsetY = (float) yRange.getLowerBounds();

        float[][] binned2DArray = new float[xBins + 1][yBins + 1];

        int xBinIndex;
        int yBinIndex;

        // mx+b
        double mx = xBins / (xRange.getUpperBounds() - xRange.getLowerBounds());
        double bx = xBins - (mx * xRange.getUpperBounds());

        double my = yBins / (yRange.getUpperBounds() - yRange.getLowerBounds());
        double by = yBins - (my * yRange.getUpperBounds());

        for (Point3f unbinnedDataPoint : unbinnedDataPoints) {

            xBinIndex = (int) (unbinnedDataPoint.x * mx + bx);
            yBinIndex = (int) (unbinnedDataPoint.y * my + by);

            if (binned2DArray[xBinIndex][yBinIndex] == 0) {
                binned2DArray[xBinIndex][yBinIndex] = unbinnedDataPoint.z;
            } else {
                binned2DArray[xBinIndex][yBinIndex] = binned2DArray[xBinIndex][yBinIndex] + unbinnedDataPoint.z;
            }
        }

        float zMax = 0;
        for (int xIndex = 0; xIndex < binned2DArray.length; xIndex++) {
            for (int yIndex = 0; yIndex < binned2DArray[xIndex].length; yIndex++) {
                if (binned2DArray[xIndex][yIndex] > zMax) zMax = binned2DArray[xIndex][yIndex];
            }
        }

        List<Point2f> binnedValues = new ArrayList<Point2f>();
        for (int xIndex = 0; xIndex < binned2DArray.length; xIndex++) {
            for (int yIndex = 0; yIndex < binned2DArray[xIndex].length; yIndex++) {
                binned2DArray[xIndex][yIndex] = scaleRelativeToMax(binned2DArray[xIndex][yIndex], zMax);
                binnedValues.add(new Point2f(xIndex * xBinWidth + offsetX, yIndex * yBinWidth + offsetY));
            }
        }

        return new Binned2DDataImpl(binned2DArray, binnedValues);
    }

    private float scaleRelativeToMax(float value, float max) {

        return (value * SCALE_FACTOR / max);
    }

    private float getBinWidth(int axis, int bins) {

        float binWidth = 0;

        if (axis == X) {
            binWidth = (float) (xRange.getSize() / bins);
        } else if (axis == Y) {
            binWidth = (float) (yRange.getSize() / bins);
        }

        return binWidth;
    }
}
