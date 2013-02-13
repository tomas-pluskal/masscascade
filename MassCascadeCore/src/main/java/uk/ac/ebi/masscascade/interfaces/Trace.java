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

package uk.ac.ebi.masscascade.interfaces;

import uk.ac.ebi.masscascade.utilities.xyz.XYZTrace;

public interface Trace extends Comparable<Trace> {

    /**
     * Returns the anchor of the trace.
     *
     * @return the anchor
     */
    double getAnchor();

    /**
     * Returns the size of the trace.
     *
     * @return the size
     */
    int size();

    /**
     * Returns the m/z average of the trace.
     *
     * @return the m/z average
     */
    double getAvg();
}
