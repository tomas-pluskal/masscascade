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

package uk.ac.ebi.masscascade.utilities.xyz;

import uk.ac.ebi.masscascade.interfaces.Range;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class implementing a x y point list.
 */
public class XYList extends ArrayList<XYPoint> implements Serializable {

    private static final long serialVersionUID = 6899220458994197501L;

    /**
     * Constructs an empty list.
     */
    public XYList() {
        super();
    }

    /**
     * Constructs a populated list.
     *
     * @param dataPoint the initial list
     */
    public XYList(List<XYPoint> dataPoint) {
        super(dataPoint);
    }

    /**
     * Returns the first point entry.
     *
     * @return the first point
     */
    public XYPoint getFirst() {
        return get(0);
    }

    /**
     * Returns the last point entry.
     *
     * @return the last entry
     */
    public XYPoint getLast() {
        return get(size() - 1);
    }

    /**
     * Returns an array of x values.
     *
     * @return the x value array
     */
    public double[] getXs() {

        int i = 0;
        double[] xs = new double[size()];
        for (XYPoint xyPoint : this)
            xs[i++] = xyPoint.x;
        return xs;
    }

    /**
     * Returns an array of y values.
     *
     * @return the y value array
     */
    public double[] getYs() {

        int i = 0;
        double[] ys = new double[size()];
        for (XYPoint xyPoint : this)
            ys[i++] = xyPoint.y;
        return ys;
    }

    /**
     * Returns a list of point indices that lie within the given x-value range (absolute values).
     * <p/>
     * Policy: [..[
     *
     * @param range a range
     * @return the list of point indices within the range
     */
    public List<Integer> getXsIndices(Range range) {

        int index = 0;
        List<Integer> indices = new ArrayList<Integer>();

        for (XYPoint xyPoint : this) {
            if (xyPoint.x - range.getLowerBounds() < 0) {
                index++;
                continue;
            } else if (range.contains(xyPoint.x)) {
                indices.add(index);
            } else {
                break;
            }
            index++;
        }

        return indices;
    }
}
