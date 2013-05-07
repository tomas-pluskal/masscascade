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

package uk.ac.ebi.masscascade.reference;

import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import java.io.Serializable;
import java.util.TreeSet;

public class ReferenceSpectrum implements Serializable {

    private static final long serialVersionUID = -8616060895807777451L;

    // meta
    private String id;
    private String title;
    private String source;

    // compound
    private String name;
    private String notation;
    private double mass;
    private String formula;

    // instrument
    private String instrument;

    // experimental
    private Constants.ION_MODE ionMode;
    private String precursorType;
    private double precursorMass;
    private int collisionEnergy;
    private TreeSet<XYPoint> mzIntList;

    // meta experimental
    private XYPoint basePeak;

    public ReferenceSpectrum(String id, String title, String source, String name, String notation, double mass,
            String formula, String instrument, Constants.ION_MODE ionMode, String precursorType, double precursorMass,
            int collisionEnergy, TreeSet<XYPoint> mzIntList, XYPoint basePeak) {

        this.id = id;
        this.title = title;
        this.source = source;
        this.name = name;
        this.notation = notation;
        this.mass = mass;
        this.formula = formula;
        this.instrument = instrument;
        this.ionMode = ionMode;
        this.precursorType = precursorType;
        this.precursorMass = precursorMass;
        this.collisionEnergy = collisionEnergy;
        this.mzIntList = mzIntList;
        this.basePeak = basePeak;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSource() {
        return source;
    }

    public String getName() {
        return name;
    }

    public String getNotation() {
        return notation;
    }

    public double getMass() {
        return mass;
    }

    public String getFormula() {
        return formula;
    }

    public String getInstrument() {
        return instrument;
    }

    public Constants.ION_MODE getIonMode() {
        return ionMode;
    }

    public String getPrecursorType() {
        return precursorType;
    }

    public double getPrecursorMass() {
        return precursorMass;
    }

    public int getCollisionEnergy() {
        return collisionEnergy;
    }

    public TreeSet<XYPoint> getMzIntList() {
        return mzIntList;
    }

    public XYPoint getBasePeak() {
        return basePeak;
    }

    public XYPoint getMatchingPeak(XYPoint signal, double ppm) {

        if (mzIntList.contains(signal)) return mzIntList.floor(signal);

        XYPoint floor = mzIntList.floor(signal);
        XYPoint higher = mzIntList.higher(signal);

        double deltaFloor = (floor != null) ? (signal.x - floor.x) : Double.MAX_VALUE;
        double deltaCeiling = (higher != null) ? (higher.x - signal.x) : Double.MAX_VALUE;

        if (floor == null && higher == null) return null;

        XYPoint match = (deltaFloor <= deltaCeiling) ? floor : higher;
        double delta = signal.x * ppm / Constants.PPM;
        return (match.x >= signal.x - delta && match.x < signal.x + delta) ? match : null;
    }
}
