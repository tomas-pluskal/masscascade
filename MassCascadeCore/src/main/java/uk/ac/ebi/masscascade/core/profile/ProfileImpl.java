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

package uk.ac.ebi.masscascade.core.profile;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import uk.ac.ebi.masscascade.core.PropertyManager;
import uk.ac.ebi.masscascade.core.chromatogram.MassChromatogram;
import uk.ac.ebi.masscascade.interfaces.Chromatogram;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.Property;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.properties.Label;
import uk.ac.ebi.masscascade.utilities.xyz.XYZList;
import uk.ac.ebi.masscascade.utilities.xyz.XYZPoint;
import uk.ac.ebi.masscascade.utilities.xyz.YMinPoint;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;
import uk.ac.ebi.masscascade.utilities.math.MathUtils;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Class implementing a mass spectrometry profile.
 */
public class ProfileImpl implements Profile {

    private static final Logger LOGGER = Logger.getLogger(Profile.class);

    private final int id;

    private double area;
    private XYZPoint centerPoint;
    private XYZList data;
    private double deviation;

    private List<Integer> msnScans;
    private Range mzRange;

    private PropertyManager propertyManager;

    /**
     * Constructor for serialization purposes.
     */
    public ProfileImpl() {
        id = -1;
    }

    /**
     * Constructs a mass spectrometry profile.
     *
     * @param id            the peak identifier
     * @param mzIntDp       the mz-intensity data point
     * @param retentionTime the retention time (seconds)
     * @param mzRange       the mz range
     */
    public ProfileImpl(int id, XYPoint mzIntDp, double retentionTime, Range mzRange) {

        this.id = id;
        this.mzRange = mzRange;

        centerPoint = new XYZPoint(retentionTime, mzIntDp.x, mzIntDp.y);
        data = new XYZList();
        data.add(new XYZPoint(retentionTime, mzIntDp.x, mzIntDp.y));
        deviation = 0d;
        area = 0d;

        propertyManager = new PropertyManager();
        msnScans = new ArrayList<Integer>();
    }

    /**
     * Constructs a mass spectrometry profile.
     *
     * @param id        the peak identifier
     * @param dataPoint the rt-mz-intensity data point
     * @param mzRange   the mz range
     */
    public ProfileImpl(int id, XYZPoint dataPoint, Range mzRange) {

        this.id = id;
        this.mzRange = mzRange;

        centerPoint = dataPoint;
        data = new XYZList();
        data.add(dataPoint);
        deviation = 0d;
        area = 0d;

        propertyManager = new PropertyManager();
        msnScans = new ArrayList<Integer>();
    }

    /**
     * Adds a single point to the profile and its trace.
     *
     * @param mzIntDp the mz int data pair
     * @param rt      the retention time
     */
    public void addProfilePoint(XYPoint mzIntDp, double rt) {

        if (mzIntDp.y > centerPoint.z) centerPoint = new XYZPoint(rt, mzIntDp.x, mzIntDp.y);

        data.add(new XYZPoint(rt, mzIntDp.x, mzIntDp.y));
        mzRange.extendRange(mzIntDp.x);
    }

    /**
     * Adds a single point to the profile and its trace.
     *
     * @param mz      the last mz
     * @param rtIntDp the last rt intensity data pair
     */
    public void addProfilePoint(double mz, XYPoint rtIntDp) {

        if (rtIntDp.y > centerPoint.z) centerPoint = new XYZPoint(rtIntDp.x, mz, rtIntDp.y);
        data.add(new XYZPoint(rtIntDp.x, mz, rtIntDp.y));
        mzRange.extendRange(mz);
    }

    /**
     * Adds a single point to the profile and its trace.
     *
     * @param dataPoint the last rt-mz-intensity triple
     */
    public void addProfilePoint(XYZPoint dataPoint) {

        if (dataPoint.z > centerPoint.z) centerPoint = dataPoint;
        data.add(dataPoint);
        mzRange.extendRange(dataPoint.y);
    }

    /**
     * Closes the profile and adds a final data point with the minimum intensity.
     *
     * @param mzIntDp   the last mz intensity data pair
     * @param closingRt the retention time
     */
    public void closeProfile(XYPoint mzIntDp, double closingRt) {
        addProfilePoint(new YMinPoint(mzIntDp.x), closingRt);
        closeProfile();
    }

    /**
     * Closes the profile and adds a final data point with the minimum intensity, extending the previous data point.
     *
     * @param closingRt the retention time
     */
    public void closeProfile(double closingRt) {
        addProfilePoint(new XYPoint(data.get(data.size() - 1).y, Constants.MIN_ABUNDANCE), closingRt);
        closeProfile();
    }

    /**
     * Closes the profile and caluclates the profile stats.
     */
    public void closeProfile() {

        Mean cMean = new Mean();
        StandardDeviation cDeviation = new StandardDeviation();

        XYPoint rtIntCenter = null;
        double[] mzs = new double[data.size() - 2];
        double[] ints = new double[data.size() - 2];
        for (int i = 1, k = 0; i < data.size() - 1; i++, k++) {
            XYZPoint dp = data.get(i);
            mzs[k] = dp.y;
            ints[k] = dp.z;

            if (dp.equals(centerPoint)) rtIntCenter = MathUtils.getParabolaVertex(data.get(i - 1), dp, data.get(i + 1));
        }

        if (centerPoint == null) {
            System.out.println("Aye");
        }

        double meanMz = cMean.evaluate(mzs, ints);
        centerPoint = new XYZPoint(rtIntCenter.x, meanMz, rtIntCenter.y);
        deviation = cDeviation.evaluate(mzs);
        area = MathUtils.getTrapezoidArea(data);

        propertyManager.addProperty(new Label("Label", getId() + ": " + MathUtils.roundToThreeDecimals(meanMz)));
    }

    /**
     * Returns the central point of the profile.
     *
     * @return the central point
     */
    public XYZPoint getCenter() {
        return centerPoint;
    }

    /**
     * Returns the retention time (seconds).
     *
     * @return the retention time
     */
    public double getRetentionTime() {
        return centerPoint.x;
    }

    /**
     * Returns the m/z value.
     *
     * @return the m/z value
     */
    public double getMz() {
        return centerPoint.y;
    }

    /**
     * Returns the intensity.
     *
     * @return the intensity
     */
    public double getIntensity() {
        return centerPoint.z;
    }

    /**
     * Gets the mz-intensity point.
     *
     * @return the mz-intensity point
     */
    public XYPoint getMzIntDp() {
        return new XYPoint(centerPoint.y, centerPoint.z);
    }

    /**
     * Returns the width of the profile.
     *
     * @return the profile width
     */
    public Range getRtRange() {
        return new ExtendableRange(data.get(0).x, getLast().x);
    }

    /**
     * Returns the m/z standard deviation.
     *
     * @return the standard deviation
     */
    public double getDeviation() {
        return deviation;
    }

    /**
     * Gets the mz-int data.
     *
     * @return the data
     */
    public XYList getMzData() {
        return data.getYZSlice();
    }

    /**
     * Gest the last mz-int data point.
     *
     * @return the last data point
     */
    public XYPoint getMzDataLast() {
        return new XYPoint(data.get(data.size() - 1).y, data.get(data.size() - 1).z);
    }

    /**
     * Gets the absolute mz range.
     *
     * @return the mz range
     */
    public Range getMzRange() {
        return mzRange;
    }

    /**
     * Gets the peak id.
     *
     * @return the peak id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the peak trace.
     *
     * @return the peak trace
     */
    public Chromatogram getTrace() {
        return new MassChromatogram(id + "", centerPoint.y, deviation, data.getXZSlice());
    }

    /**
     * Sets a property.
     *
     * @param property the property object
     */
    public void setProperty(Property property) {

        if (property.getType() == PropertyManager.TYPE.Label) propertyManager.setProperty(property);
        else propertyManager.addProperty(property);
    }

    /**
     * Gets the property list identified by its name.
     *
     * @param type the property name
     * @return the property list
     */
    public Set<Property> getProperty(PropertyManager.TYPE type) {
        return propertyManager.getProperty(type);
    }

    /**
     * Whecks whether a particular property is set.
     *
     * @param type the property title
     * @return whether property is set
     */
    public boolean hasProperty(PropertyManager.TYPE type) {
        return propertyManager.hasProperty(type);
    }

    /**
     * Gets the area of the peak.
     *
     * @return the peak area
     */
    public double getArea() {
        return area;
    }

    /**
     * Sets the daughter scan index list for the peak.
     *
     * @param msnScans a daughter scan index list
     */
    public void setMsnScans(List<Integer> msnScans) {
        this.msnScans = msnScans;
    }

    /**
     * Gets the daughter scan index list for the peak.
     *
     * @return the scan index list
     */
    public List<Integer> getMsnScans() {
        return msnScans;
    }

    /**
     * Gets the chromatogram time-intensity data.
     *
     * @return the time-intensity data set
     */
    public XYZList getData() {
        return data;
    }

    /**
     * Gets the last data point.
     *
     * @return the tailing data point
     */
    public XYZPoint getLast() {
        return data.get(data.size() - 1);
    }

    @Override
    public int hashCode() {

        int hash = 13;

        hash = (hash * 7) + Long.valueOf(Double.doubleToLongBits(deviation)).hashCode();
        hash = (hash * 7) + Long.valueOf(Double.doubleToLongBits(area)).hashCode();
        hash = (hash * 7) + centerPoint.hashCode();
        hash = (hash * 7) + mzRange.hashCode();

        return hash;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == this) return true;

        if (obj == null) return false;
        if (!(obj instanceof ProfileImpl)) return false;

        ProfileImpl profile = (ProfileImpl) obj;

        return (profile.getArea() == this.area && profile.getCenter().equals(
                centerPoint) && profile.getMzRange().equals(mzRange));
    }
}
