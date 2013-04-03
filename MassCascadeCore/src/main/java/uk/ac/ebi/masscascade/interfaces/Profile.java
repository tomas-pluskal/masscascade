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

package uk.ac.ebi.masscascade.interfaces;

import uk.ac.ebi.masscascade.core.PropertyManager;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;
import uk.ac.ebi.masscascade.utilities.xyz.XYZList;
import uk.ac.ebi.masscascade.utilities.xyz.XYZPoint;

import java.util.Map;
import java.util.Set;

/**
 * This is a profile that is comprised of data points defining time, m/z, and intensity values.
 */
public interface Profile {

    /**
     * Returns the profile identifier.
     *
     * @return the profile identifier
     */
    int getId();

    Profile copy();

    Profile copy(double rt);

    PropertyManager getPropertyManager();

    /**
     * Returns the m/z value.
     *
     * @return the m/z value
     */
    double getMz();

    /**
     * Returns the intensity.
     *
     * @return the intensity
     */
    double getIntensity();

    /**
     * Returns the minimum intensity.
     *
     * @return the minimum intensity
     */
    double getMinIntensity();

    /**
     * Returns the rt-mz-intensity data list.
     *
     * @return the data list
     */
    XYZList getData();

    /**
     * Returns the representative mass intensity data point.
     *
     * @return the data point
     */
    XYPoint getMzIntDp();

    /**
     * Returns the mz-intensity data.
     *
     * @return the mz-int data
     */
    XYList getMzData();

    /**
     * Returns the last mz-intensity data point.
     *
     * @return the last mz-int data point
     */
    XYPoint getMzDataLast();

    /**
     * Returns the retention time.
     *
     * @return the retention time
     */
    double getRetentionTime();

    /**
     * Returns a property list.
     *
     * @param type the property name
     * @return the property list
     */
    Set<Property> getProperty(PropertyManager.TYPE type);

    /**
     * Whether a particular property is set.
     *
     * @param type the property title
     * @return whether property is set
     */
    boolean hasProperty(PropertyManager.TYPE type);

    /**
     * Returns the extracted ion chromatogram.
     *
     * @return the extracted ion chromatogram
     */
    Chromatogram getTrace();

    /**
     * Returns the extracted ion chromatogram.
     *
     * @return the extracted ion chromatogram
     */
    Chromatogram getTrace(int width);

    XYZList getPaddedData(int width);

    /**
     * Sets a profile property.
     *
     * @param property the property object
     */
    void setProperty(Property property);

    /**
     * Returns the profile area.
     *
     * @return the profile area
     */
    double getArea();

    /**
     * Sets the daughter scan index list for this profile.
     *
     * @param msnScans a daughter scan index list
     */
    void setMsnScans(Map<Integer, Set<Integer>> msnScans);

    /**
     * Gets the daughter scan index list for this profile.
     *
     * @return the scan index list
     */
    Map<Integer, Set<Integer>> getMsnScans();

    /**
     * Returns the absolute mz range.
     *
     * @return the mz range
     */
    Range getMzRange();

    /**
     * Returns the width of the profile.
     *
     * @return the profile width
     */
    Range getRtRange();

    /**
     * Returns the m/z standard deviation.
     *
     * @return the standard deviation
     */
    double getDeviation();

    /**
     * Adds a single profile data point to the existing profile. Requires 'close' method afterwards to update
     * underlying objects.
     */
    void addProfilePoint(XYPoint mzIntDp, double rt);

    /**
     * Adds a single profile data point to the existing profile. Requires 'close' method afterwards to update
     * underlying objects.
     */
    void addProfilePoint(double mz, XYPoint rtIntDp);

    /**
     * Adds a single profile data point to the existing profile. Requires 'close' method afterwards to update
     * underlying objects.
     *
     * @param dataPoint the last rt-mz-intensity triple
     */
    public void addProfilePoint(XYZPoint dataPoint);

    /**
     * Closes the profile and updates all underlying objects.
     */
    void closeProfile(XYPoint lastDp, double lastRt);

    /**
     * Closes the profile and adds a final data point with the minimum intensity, extending the previous data point.
     */
    void closeProfile(double closingRt);

    /**
     * Closes the profile and updates all underlying objects.
     */
    void closeProfile();
}
