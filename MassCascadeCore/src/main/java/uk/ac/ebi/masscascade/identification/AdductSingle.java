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

package uk.ac.ebi.masscascade.identification;

import uk.ac.ebi.masscascade.parameters.Constants;

public class AdductSingle {

    private String name;
    private double mass;
    private int charge;
    private int clusterSize;
    private boolean cluster;

    public AdductSingle(String name, int charge, double mass, Constants.ION_MODE ionMode) {

        this.name = name;
        this.charge = charge;
        this.clusterSize = 1;

        if (ionMode.equals(Constants.ION_MODE.POSITIVE)) this.mass = mass - Constants.PARTICLES.PROTON.getMass();
        else if (ionMode.equals(Constants.ION_MODE.NEGATIVE)) this.mass = mass + Constants.PARTICLES.PROTON.getMass();

        cluster = (name.startsWith("2") || name.startsWith("3"));
        if (cluster) clusterSize = Integer.parseInt(name.substring(0, 1));
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
