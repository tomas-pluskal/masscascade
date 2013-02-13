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

package uk.ac.ebi.masscascade.utilities.comparator;

import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import java.util.Comparator;

/**
 * Class implementing a comparator for x y point objects. The points are compared by their y values.
 */
public class PointIntensityComparator implements Comparator<XYPoint> {

    /**
     * Compares two xy data points by their intensity.
     */
    public int compare(XYPoint dataPoint1, XYPoint dataPoint2) {

        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        if (dataPoint1.y < dataPoint2.y) return BEFORE;
        if (dataPoint1.y > dataPoint2.y) return AFTER;

        return EQUAL;
    }
}
