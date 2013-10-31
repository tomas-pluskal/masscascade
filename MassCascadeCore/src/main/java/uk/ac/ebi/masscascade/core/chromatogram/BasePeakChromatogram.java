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
package uk.ac.ebi.masscascade.core.chromatogram;

import uk.ac.ebi.masscascade.interfaces.Chromatogram;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import java.io.Serializable;

/**
 * Class representing a base feature chromatogram where the most intense signal of a particular scan in the mass domain
 * represents a single base feature.
 */
public class BasePeakChromatogram implements Chromatogram, Serializable {

    private static final long serialVersionUID = -3942470252084409988L;

    private final String id;
    private final Constants.MSN msn;
    private final XYList basePeakData;

    /**
     * Constructs a default base feature chromatogram.
     */
    public BasePeakChromatogram() {

        this.id = "";
        this.msn = Constants.MSN.MS1;
        this.basePeakData = null;
    }

    /**
     * Constructs a custom base feature chromatogram based on the input scan list.
     *
     * @param id           the identifier
     * @param msn        the setup level
     * @param basePeakData base feature data points
     */
    public BasePeakChromatogram(String id, Constants.MSN msn, XYList basePeakData) {

        this.id = id;
        this.msn = msn;
        this.basePeakData = basePeakData;
    }

    /**
     * Gets all data points.
     *
     * @return the xy data
     */
    public XYList getData() {

        return basePeakData;
    }

    /**
     * Gets the last data point.
     *
     * @return the tailing data point
     */
    public XYPoint getLast() {

        return basePeakData.get(basePeakData.size() - 1);
    }

    /**
     * Returns the string representation.
     *
     * @return the string representation
     */
    @Override
    public String toString() {

        return id;
    }

    /**
     * Returns the experimental level.
     *
     * @return the experimental level
     */
    public Constants.MSN getMSn() {

        return msn;
    }
}
