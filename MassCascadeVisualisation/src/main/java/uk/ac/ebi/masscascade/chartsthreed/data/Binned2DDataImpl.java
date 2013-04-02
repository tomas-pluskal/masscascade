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

package uk.ac.ebi.masscascade.chartsthreed.data;

import org.freehep.j3d.plot.Binned2DData;

import javax.vecmath.Color3b;
import javax.vecmath.Point2f;
import java.awt.*;
import java.util.List;

public class Binned2DDataImpl implements Binned2DData {

    private static final int X = 1;
    private static final int Y = 2;
    private static final int GRADIENT_NUMBER = 31;

    private float zMin;
    private float zMax;

    private List<Point2f> binnedValues;
    private float[][] binned2DArray;

    public Binned2DDataImpl(float[][] binned2DArray, List<Point2f> binnedValues) {

        this.zMin = Float.MAX_VALUE;
        this.zMax = Float.MIN_VALUE;

        for (int xIndex = 0; xIndex < binned2DArray.length; xIndex++) {
            for (int yIndex = 0; yIndex < binned2DArray[xIndex].length; yIndex++) {
                if (binned2DArray[xIndex][yIndex] < zMin) zMin = binned2DArray[xIndex][yIndex];
                if (binned2DArray[xIndex][yIndex] > zMax) zMax = binned2DArray[xIndex][yIndex];
            }
        }

        this.binnedValues = binnedValues;
        this.binned2DArray = binned2DArray;
    }

    /**
     * @return the no. of bins on the x axis
     */
    public int xBins() {

        return binned2DArray.length;
    }

    /**
     * @return the no. of bins on the y axis
     */
    public int yBins() {

        return binned2DArray[0].length;
    }

    private float getMinValue(int xyz) {

        float min = Float.MAX_VALUE;

        if (xyz == X) {
            for (Point2f binnedValue : binnedValues) {
                if (binnedValue.x < min) min = binnedValue.x;
            }
        } else if (xyz == Y) {
            for (Point2f binnedValue : binnedValues) {
                if (binnedValue.y < min) min = binnedValue.y;
            }
        }

        return min;
    }

    private float getMaxValue(int xyz) {

        float max = Float.MIN_VALUE;

        if (xyz == X) {
            for (Point2f binnedValue : binnedValues) {
                if (binnedValue.x > max) max = binnedValue.x;
            }
        } else if (xyz == Y) {
            for (Point2f binnedValue : binnedValues) {
                if (binnedValue.y > max) max = binnedValue.y;
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

        return zMin;
    }

    /**
     * Maximum data value on the Z Axis.
     */
    public float zMax() {

        return zMax;
    }

    /**
     * Returns the z value at grid position i, i1.
     *
     * @param i  grid position i
     * @param i1 grid position i1
     * @return the z value at position i, i1
     */
    public float zAt(int i, int i1) {

        return binned2DArray[i][binned2DArray.length - 1 - i1];
    }

    /**
     * Returns the color value at grid position i, i1.
     *
     * @param i  grid position i
     * @param i1 grid position i1
     * @return the color at position i, i1
     */
    public Color3b colorAt(int i, int i1) {

        return getColorByIntensity(zAt(i, i1));
    }

    private Color3b getColorByIntensity(float z) {

        int totalColorDistance = (int) zMax / GRADIENT_NUMBER;
        int pointColorDistance = (int) (z * GRADIENT_NUMBER / zMax);
        int argb = linearInterpolate(Color.BLUE.getRGB(), Color.RED.getRGB(), pointColorDistance, totalColorDistance);

        return new Color3b(new Color(argb));
    }

    /**
     * Linear interpolation between two points.
     * Returns interpolated color Y at distance l.
     *
     * @param A ARGB for point A.
     * @param B ARGB for point B.
     * @param l Distance Y from A.
     * @param L Distance between A and B.
     * @return Interpolated color Y.
     */
    public int linearInterpolate(int A, int B, int l, int L) {

        // extract r, g, b information
        // A and B is a ARGB-packed int so we use bit operation to extract
        int Ar = (A >> 16) & 0xff;
        int Ag = (A >> 8) & 0xff;
        int Ab = A & 0xff;
        int Br = (B >> 16) & 0xff;
        int Bg = (B >> 8) & 0xff;
        int Bb = B & 0xff;

        // now calculate Y. convert float to avoid early rounding
        // There are better ways but this is for clarity's sake
        int Yr = (int) (Ar + l * (Br - Ar) / (float) L);
        int Yg = (int) (Ag + l * (Bg - Ag) / (float) L);
        int Yb = (int) (Ab + l * (Bb - Ab) / (float) L);

        // pack ARGB with hardcoded alpha
        return 0xff000000 | // alpha
                ((Yr << 16) & 0xff0000) | ((Yg << 8) & 0xff00) | (Yb & 0xff);
    }
}
