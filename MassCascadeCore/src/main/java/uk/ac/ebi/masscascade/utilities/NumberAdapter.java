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

package uk.ac.ebi.masscascade.utilities;

import uk.ac.ebi.masscascade.exception.MassCascadeException;

public class NumberAdapter extends Number {

    /**
     * Returns the value of the specified number as an <code>int</code>.
     * This may involve rounding or truncation.
     *
     * @return the numeric value represented by this object after conversion
     *         to type <code>int</code>.
     */
    @Override
    public int intValue() {
        throw new MassCascadeException("Method not implemented.");
    }

    /**
     * Returns the value of the specified number as a <code>long</code>.
     * This may involve rounding or truncation.
     *
     * @return the numeric value represented by this object after conversion
     *         to type <code>long</code>.
     */
    @Override
    public long longValue() {
        throw new MassCascadeException("Method not implemented.");
    }

    /**
     * Returns the value of the specified number as a <code>float</code>.
     * This may involve rounding.
     *
     * @return the numeric value represented by this object after conversion
     *         to type <code>float</code>.
     */
    @Override
    public float floatValue() {
        throw new MassCascadeException("Method not implemented.");
    }

    /**
     * Returns the value of the specified number as a <code>double</code>.
     * This may involve rounding.
     *
     * @return the numeric value represented by this object after conversion
     *         to type <code>double</code>.
     */
    @Override
    public double doubleValue() {
        throw new MassCascadeException("Method not implemented.");
    }
}
