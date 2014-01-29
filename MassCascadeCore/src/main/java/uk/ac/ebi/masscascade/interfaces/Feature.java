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
import uk.ac.ebi.masscascade.core.PropertyType;
import uk.ac.ebi.masscascade.interfaces.container.ScanContainer;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;
import uk.ac.ebi.masscascade.utilities.xyz.XYZList;
import uk.ac.ebi.masscascade.utilities.xyz.XYZPoint;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is a feature that is comprised of data points defining time, m/z, and intensity values.
 */
public interface Feature {

    /**
     * Returns the feature identifier.
     *
     * @return the feature identifier
     */
    int getId();

    /**
     * Sets the feature identifier.
     *
     * @param id the feature identifier
     */
    void setId(int id);

    /**
     * Copies the feature including all properties and msn information but excluding the feature data.
     * <p/>
     * The copy method facilitates operations that manipulate the data of the feature but are not supposed to alter any
     * additional information, such as identified signals or msn scan pointers.
     * <p/>
     * The copied feature has the same zero intensity anchor as the original feature.
     *
     * @return the copied feature frame
     */
    Feature copy();

    /**
     * Copies the feature including all properties and msn information but excluding the feature data.
     * <p/>
     * The copy method facilitates operations that manipulate the data of the feature but are not supposed to alter any
     * additional information, such as identified signals or msn scan pointers.
     * <p/>
     * The copied feature has a zero intensity anchor at the given time.
     *
     * @param rt the time for the zero intensity anchor
     * @return the copied feature frame
     */
    Feature copy(double rt);

    /**
     * Returns the property manager instance.
     *
     * @return the property manager instance
     */
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
     * Returns the intensity span.
     *
     * @return the intensity span
     */
    double getDifIntensity();

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
     * Returns the central point of the feature.
     *
     * @return the central point
     */
    XYZPoint getCenter();

    /**
     * Gets the last data point.
     *
     * @return the tailing data point
     */
    XYZPoint getLast();

    /**
     * Returns a property list.
     *
     * @param type the property name
     * @return the property list
     */
    <T> Set<T> getProperty(PropertyType type, Class<T> typeClass);

    /**
     * Whether a particular property is set.
     *
     * @param type the property title
     * @return whether property is set
     */
    boolean hasProperty(PropertyType type);

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
     * Sets a feature property.
     *
     * @param property the property object
     */
    void setProperty(Property property);

    /**
     * Returns the feature area.
     *
     * @return the feature area
     */
    double getArea();

    /**
     * Sets the daughter scan index list for this feature.
     *
     * @param msnScans a daughter scan index list
     */
    void setMsnScans(Map<Constants.MSN, Set<Integer>> msnScans);

    /**
     * Gets the daughter scan index list for this feature.
     *
     * @return the scan index list
     */
    Map<Constants.MSN, Set<Integer>> getMsnScans();

    /**
     * Gets the daughter scan index list for this feature specific for the given time range.
     *
     * @param container the scan data container
     * @param timeRange the time range
     * @return the scan index list
     */
    Map<Constants.MSN, Set<Integer>> getMsnScans(ScanContainer container, Range timeRange);

    /**
     * Returns whether the feature has MSn scan pointers.
     *
     * @return whether MSn scan pointers exist
     */
    public boolean hasMsnScans();

    /**
     * Adds a featureset to a particular MSn level.
     *
     * @param msn      a MSn level
     * @param featureSet a featureset
     */
    public void addMsnSpectrum(Constants.MSN msn, FeatureSet featureSet);

    /**
     * Whether the feature has MSn spectra.
     *
     * @param msn the msn level
     * @return whether spectra for the MSn exist
     */
    public boolean hasMsnSpectra(Constants.MSN msn);

    /**
     * Returns the list of MSn spectra of a particular level.
     *
     * @param msn the msn level
     * @return the list of MSn spectra
     */
    public List<FeatureSet> getMsnSpectra(Constants.MSN msn);

    /**
     * Returns the absolute mz range.
     *
     * @return the mz range
     */
    Range getMzRange();

    /**
     * Returns the width of the feature.
     *
     * @return the feature width
     */
    Range getRtRange();

    /**
     * Returns the m/z standard deviation.
     *
     * @return the standard deviation
     */
    double getDeviation();

    /**
     * Adds a single feature data point to the existing feature. Requires 'close' method afterwards to update
     * underlying objects.
     */
    void addFeaturePoint(XYPoint mzIntDp, double rt);

    /**
     * Adds a single feature data point to the existing feature. Requires 'close' method afterwards to update
     * underlying objects.
     */
    void addFeaturePoint(double mz, XYPoint rtIntDp);

    /**
     * Adds a single feature data point to the existing feature. Requires 'close' method afterwards to update
     * underlying objects.
     *
     * @param dataPoint the last rt-mz-intensity triple
     */
    public void addFeaturePoint(XYZPoint dataPoint);

    /**
     * Closes the feature and updates all underlying objects.
     */
    void closeFeature(XYPoint lastDp, double lastRt);

    /**
     * Closes the feature and adds a final data point with the minimum intensity, extending the previous data point.
     */
    void closeFeature(double closingRt);

    /**
     * Closes the feature and updates all underlying objects.
     */
    void closeFeature();
}
