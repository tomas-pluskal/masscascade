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

package uk.ac.ebi.masscascade.utilities.range;

import uk.ac.ebi.masscascade.parameters.Constants;

/**
 * Class implementing a range that is created from an initial value x and a tolerance in ppm. The range is defined as x
 * +/- (x * ppm / 10^6)
 */
public class ToleranceRange extends ExtendableRange {

    /**
     * Constructs a default range.
     */
    public ToleranceRange() {
        super();
    }

    /**
     * Constructs a tolerance range according to x +/- (x * ppm / 10^6).
     *
     * @param x   a x value
     * @param ppm a ppm value
     */
    public ToleranceRange(double x, double ppm) {
        super(x - (x * ppm / Constants.PPM), x + (x * ppm / Constants.PPM));
    }
}
