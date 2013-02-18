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

import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

/**
 * This is a scan that is comprised of m/z-intensity data points.
 */
public interface Scan {

    /**
     * Returns the scan index.
     *
     * @return the scan index
     */
    int getIndex();

    /**
     * Returns the MSn level.
     *
     * @return the MSn level
     */
    Constants.MSN getMsn();

    /**
     * Returns the retention time.
     *
     * @return the retention time
     */
    double getRetentionTime();

    /**
     * Returns the range in the mass domain.
     *
     * @return the range
     */
    Range getMzRange();

    /**
     * Returns the ion mode.
     *
     * @return the ion mode
     */
    Constants.ION_MODE getIonMode();

    /**
     * Returns the scan data.
     *
     * @return the scan data
     */
    XYList getData();

    /**
     * Returns the base profile.
     *
     * @return the base profile
     */
    XYList getBasePeak();

    /**
     * Returns the parent's charge.
     *
     * @return the charge
     */
    int getParentCharge();

    /**
     * Returns the parent's mass.
     *
     * @return the mass
     */
    double getParentMz();

    /**
     * Returns the parent scan.
     *
     * @return the parent scan
     */
    int getParentScan();

    /**
     * Returns the total ion current.
     *
     * @return the total ion current
     */
    double getTotalIonCurrent();

    /**
     * Returns the minimum intensity found in the scan.
     *
     * @return the minimum intensity
     */
    double getMinIntensity();

    /**
     * Returns the nearest data point to the given mass.
     *
     * @param mz        the mass
     * @param tolerance the absolute mass tolerance
     * @return the nearest data point
     */
    XYPoint getNearestPoint(double mz, double tolerance);

    /**
     * Checks if the content of two scans are identicial.
     *
     * @param scan the scan for comparison
     * @return true if the scans are identical
     */
    @Override
    boolean equals(Object scan);

    /**
     * Returns the hash code of the scan.
     *
     * @return the value
     */
    @Override
    int hashCode();
}
