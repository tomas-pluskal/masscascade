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

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class implementing a x y z point list.
 */
public class XYZList extends ArrayList<XYZPoint> implements Serializable {

    /**
     * Constructs an empty list.
     */
    public XYZList() {
        super();
    }

    /**
     * Returns a YZ slice of the data list.
     *
     * @return the data slice
     */
    public XYList getYZSlice() {

        XYList slice = new XYList();
        for (XYZPoint dp : this) slice.add(new XYPoint(dp.y, dp.z));

        return slice;
    }

    /**
     * Returns a XZ slice of the data list.
     *
     * @return the data slice
     */
    public XYList getXZSlice() {

        XYList slice = new XYList();
        for (XYZPoint dp : this) slice.add(new XYPoint(dp.x, dp.z));

        return slice;
    }

    /**
     * Returns a XY slice of the data list.
     *
     * @return the data slice
     */
    public XYList getXYSlice() {

        XYList slice = new XYList();
        for (XYZPoint dp : this) slice.add(new XYPoint(dp.x, dp.y));

        return slice;
    }
}
