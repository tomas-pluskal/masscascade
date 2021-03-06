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

package uk.ac.ebi.masscascade.identification;

public class AdductSingle {

    private String name;
    private double mass;
    private int charge;
    private int clusterSize;
    private boolean cluster;

    public AdductSingle(String name, int charge, double mass) {

        this.name = name;
        this.mass = mass;
        this.charge = charge;
        cluster = (name.startsWith("2") || name.startsWith("3"));
        clusterSize = cluster ? Integer.parseInt(name.substring(0, 1)) : 1;
    }

    public String getName() {
        return name;
    }

    public double getMass() {
        return mass;
    }

    public int getCharge() {
        return charge;
    }

    public boolean isCluster() {
        return cluster;
    }

    public int getClusterSize() {
        return clusterSize;
    }
}
