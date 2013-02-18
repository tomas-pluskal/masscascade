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
package uk.ac.ebi.masscascade.core.chromatogram;

import uk.ac.ebi.masscascade.interfaces.Chromatogram;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import java.io.Serializable;

/**
 * Class representing an extracted ion chromatogram in the time domain.
 */
public class MassChromatogram implements Chromatogram, Serializable {

    private static final long serialVersionUID = -6719770232095785012L;

    private final String id;
    private final XYList xicData;
    private double mass;
    private double deviation;

    /**
     * Constructor for deserialization.
     */
    public MassChromatogram() {

        id = "";
        mass = Double.NaN;
        deviation = Double.NaN;
        xicData = new XYList();
    }

    /**
     * Constructs a new ion chromatogram.
     *
     * @param id        the chromatogram identifier
     * @param mass      the representative mass
     * @param xicData   the chromatogram data set
     * @param deviation the mass deviation
     */
    public MassChromatogram(String id, double mass, double deviation, XYList xicData) {

        this.id = id;
        this.mass = mass;
        this.xicData = xicData;
        this.deviation = deviation;
    }

    /**
     * Gets the chromatogram identifier.
     *
     * @return the chromatogram id
     */
    public String getId() {

        return id;
    }

    /**
     * Gets the chromatogram data set.
     *
     * @return the chromatogram data
     */
    public XYList getData() {

        return xicData;
    }

    /**
     * Gets the last data point.
     *
     * @return the tailing data point
     */
    public XYPoint getLast() {

        return xicData.get(xicData.size() - 1);
    }

    public Range getRange() {

        return new ExtendableRange(xicData.get(0).x, getLast().x);
    }


    /**
     * Gets the representative mass.
     *
     * @return the representative mass
     */
    public double getMass() {

        return mass;
    }

    /**
     * Sets the representative mass.
     *
     * @param mass the representative mass
     */
    public void setMass(double mass) {

        this.mass = mass;
    }

    /**
     * Gets the mass deviation.
     *
     * @return the deviation
     */
    public double getDeviation() {

        return deviation;
    }

    /**
     * Sets the mass deviation.
     *
     * @param deviation the mass deviation
     */
    public void setDeviation(double deviation) {

        this.deviation = deviation;
    }

    /**
     * Gets the id of the chromatogram.
     *
     * @return the chromatogram id
     */
    @Override
    public String toString() {

        return id;
    }
}
