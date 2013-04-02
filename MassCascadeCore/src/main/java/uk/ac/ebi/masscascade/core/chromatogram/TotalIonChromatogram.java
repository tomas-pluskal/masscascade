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
 * Class representing a total ion chromatogram where the all signals of a scan in the mass domain represent
 * a single ion profile.
 */
public class TotalIonChromatogram implements Chromatogram, Serializable {

    private final String id;
    private final Constants.MSN msn;
    private final XYList ticData;

    /**
     * Default constructor for deserialization purposes.
     */
    public TotalIonChromatogram() {

        this.id = "";
        this.msn = Constants.MSN.MS1;
        this.ticData = null;
    }

    /**
     * Constructs a total ion chromatogram based on the provided scan list.
     *
     * @param id      an identifier
     * @param msn   a experimental msn
     * @param ticData total ion values
     */
    public TotalIonChromatogram(String id, Constants.MSN msn, XYList ticData) {

        this.id = id;
        this.msn = msn;
        this.ticData = ticData;
    }

    /**
     * Gets all data points.
     *
     * @return the xy data
     */
    public XYList getData() {

        return ticData;
    }

    /**
     * Gets the last data point.
     *
     * @return the tailing data point
     */
    public XYPoint getLast() {

        return ticData.get(ticData.size() - 1);
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
     * Returns the experimental msn.
     *
     * @return the experimental msn
     */
    public Constants.MSN getMSn() {

        return msn;
    }
}
