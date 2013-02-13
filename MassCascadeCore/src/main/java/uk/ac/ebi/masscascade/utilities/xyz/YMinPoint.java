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

import uk.ac.ebi.masscascade.parameters.Constants;

/**
 * Class representing a data point with the y value set to <code> Constants.MIN_ABUNDANCE</code>.
 */
public class YMinPoint extends XYPoint {

    /**
     * Constructs a data point with x = 0 and and y = <code> Constants.MIN_ABUNDANCE</code>.
     */
    public YMinPoint() {
        super(0, Constants.MIN_ABUNDANCE);
    }

    /**
     * Constructs a data point with y = <code> Constants.MIN_ABUNDANCE</code>.
     *
     * @param x the x value
     */
    public YMinPoint(double x) {
        super(x, Constants.MIN_ABUNDANCE);
    }
}
