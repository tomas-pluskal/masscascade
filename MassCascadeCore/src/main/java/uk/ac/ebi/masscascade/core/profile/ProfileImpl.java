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

package uk.ac.ebi.masscascade.core.profile;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.log4j.Logger;
import uk.ac.ebi.masscascade.core.MsnManager;
import uk.ac.ebi.masscascade.core.PropertyManager;
import uk.ac.ebi.masscascade.core.PropertyType;
import uk.ac.ebi.masscascade.core.chromatogram.MassChromatogram;
import uk.ac.ebi.masscascade.interfaces.Chromatogram;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.Property;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.interfaces.Spectrum;
import uk.ac.ebi.masscascade.interfaces.container.RawContainer;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.properties.Label;
import uk.ac.ebi.masscascade.utilities.math.MathUtils;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;
import uk.ac.ebi.masscascade.utilities.xyz.XYZList;
import uk.ac.ebi.masscascade.utilities.xyz.XYZPoint;
import uk.ac.ebi.masscascade.utilities.xyz.YMinPoint;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class implementing a mass spectrometry profile.
 */
public class ProfileImpl implements Profile {

    private static final Logger LOGGER = Logger.getLogger(Profile.class);

    private final int id;

    private double minIntensity;
    private double area;
    private XYZPoint baseSignal;
    private XYZList data;
    private double deviation;

    private Range mzRange;

    private MsnManager msnManager;
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
        this(id, new XYZPoint(retentionTime, mzIntDp.x, mzIntDp.y), mzRange);
    }

    /**
     * Constructs a mass spectrometry profile.
     *
     * @param id        the peak identifier
     * @param dataPoint the rt-mz-intensity data point
     * @param mzRange   the mz range
     */
    public ProfileImpl(int id, XYZPoint dataPoint, Range mzRange) {
        this(id, dataPoint, mzRange, new PropertyManager());
    }

    /**
     * Constructs a mass spectrometry profile.
     *
     * @param id        the peak identifier
     * @param dataPoint the rt-mz-intensity data point
     * @param mzRange   the mz range
     * @param manager   the property manager
     */
    public ProfileImpl(int id, XYZPoint dataPoint, Range mzRange, PropertyManager manager) {
        this(id, dataPoint, mzRange, manager, new MsnManager());
    }

    /**
     * Constructs a mass spectrometry profile.
     *
     * @param id         the peak identifier
     * @param dataPoint  the rt-mz-intensity data point
     * @param mzRange    the mz range
     * @param manager    the property manager
     * @param msnManager the msn manager
     */
    public ProfileImpl(int id, XYZPoint dataPoint, Range mzRange, PropertyManager manager, MsnManager msnManager) {

        this.id = id;
        this.mzRange = mzRange;
        this.propertyManager = manager;
        this.msnManager = msnManager;
        this.baseSignal = dataPoint;

        data = new XYZList();
        data.add(dataPoint);
        deviation = 0d;
        area = 0d;
        minIntensity = Double.MAX_VALUE;
    }

    /**
     * Copies the profile including all properties and msn information but excluding the profile data.
     * <p/>
     * The copy method facilitates operations that manipulate the data of the profile but are not supposed to alter any
     * additional information, such as identified signals or msn scan pointers.
     * <p/>
     * The copied profile has the same zero intensity anchor as the original profile.
     *
     * @return the copied profile frame
     */
    @Override
    public Profile copy() {
        XYZPoint dp = data.get(0);
        return new ProfileImpl(id, dp, new ExtendableRange(dp.y), propertyManager, msnManager);
    }

    /**
     * Copies the profile including all properties and msn information but excluding the profile data.
     * <p/>
     * The copy method facilitates operations that manipulate the data of the profile but are not supposed to alter any
     * additional information, such as identified signals or msn scan pointers.
     * <p/>
     * The copied profile has a zero intensity anchor at the given time.
     *
     * @param rt the time for the zero intensity anchor
     * @return the copied profile frame
     */
    @Override
    public Profile copy(double rt) {
        XYZPoint dp = data.get(0);
        return new ProfileImpl(id, new XYZPoint(rt, dp.y, Constants.MIN_ABUNDANCE), new ExtendableRange(dp.y),
                propertyManager, msnManager);
    }

    /**
     * Returns the property manager instance.
     *
     * @return the property manager instance
     */
    @Override
    public PropertyManager getPropertyManager() {
        return propertyManager;
    }

    /**
     * Adds a single point to the profile and its trace.
     *
     * @param mzIntDp the mz int data pair
     * @param rt      the retention time
     */
    @Override
    public void addProfilePoint(XYPoint mzIntDp, double rt) {
        addProfilePoint(new XYZPoint(rt, mzIntDp.x, mzIntDp.y));
    }

    /**
     * Adds a single point to the profile and its trace.
     *
     * @param mz      the last mz
     * @param rtIntDp the last rt intensity data pair
     */
    @Override
    public void addProfilePoint(double mz, XYPoint rtIntDp) {
        addProfilePoint(new XYZPoint(rtIntDp.x, mz, rtIntDp.y));
    }

    /**
     * Adds a single point to the profile and its trace.
     *
     * @param dataPoint the last rt-mz-intensity triple
     */
    @Override
    public void addProfilePoint(XYZPoint dataPoint) {

        if (dataPoint.z > baseSignal.z) baseSignal = dataPoint;
        if (dataPoint.z < minIntensity && dataPoint.z != Constants.MIN_ABUNDANCE) minIntensity = dataPoint.z;
        data.add(dataPoint);
        mzRange.extendRange(dataPoint.y);
    }

    /**
     * Returns the minimum intensity.
     *
     * @return the minimum intensity
     */
    @Override
    public double getMinIntensity() {
        return minIntensity;
    }

    /**
     * Returns the intensity span.
     *
     * @return the intensity span
     */
    @Override
    public double getDifIntensity() {
        return getIntensity() - getMinIntensity();
    }

    /**
     * Closes the profile and adds a final data point with the minimum intensity.
     *
     * @param mzIntDp   the last mz intensity data pair
     * @param closingRt the retention time
     */
    @Override
    public void closeProfile(XYPoint mzIntDp, double closingRt) {
        addProfilePoint(new YMinPoint(mzIntDp.x), closingRt);
        closeProfile();
    }

    /**
     * Closes the profile and adds a final data point with the minimum intensity, extending the previous data point.
     *
     * @param closingRt the retention time
     */
    @Override
    public void closeProfile(double closingRt) {
        addProfilePoint(new XYPoint(data.get(data.size() - 1).y, Constants.MIN_ABUNDANCE), closingRt);
        closeProfile();
    }

    /**
     * Closes the profile and caluclates the profile stats.
     */
    @Override
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

            if (dp.equals(baseSignal)) rtIntCenter = MathUtils.getParabolaVertex(data.get(i - 1), dp, data.get(i + 1));
        }

        double meanMz = cMean.evaluate(mzs, ints);
        baseSignal = new XYZPoint(rtIntCenter.x, meanMz, rtIntCenter.y);
        deviation = cDeviation.evaluate(mzs);
        area = MathUtils.getTrapezoidArea(data);

        propertyManager.addProperty(new Label("Label", getId() + ": " + MathUtils.roundToThreeDecimals(meanMz)));
    }

    /**
     * Returns the central point of the profile.
     *
     * @return the central point
     */
    @Override
    public XYZPoint getCenter() {
        return baseSignal;
    }

    /**
     * Returns the retention time (seconds).
     *
     * @return the retention time
     */
    @Override
    public double getRetentionTime() {
        return baseSignal.x;
    }

    /**
     * Returns the m/z value.
     *
     * @return the m/z value
     */
    @Override
    public double getMz() {
        return baseSignal.y;
    }

    /**
     * Returns the intensity.
     *
     * @return the intensity
     */
    @Override
    public double getIntensity() {
        return baseSignal.z;
    }

    /**
     * Gets the mz-intensity point.
     *
     * @return the mz-intensity point
     */
    @Override
    public XYPoint getMzIntDp() {
        return new XYPoint(baseSignal.y, baseSignal.z);
    }

    /**
     * Returns the width of the profile.
     *
     * @return the profile width
     */
    @Override
    public Range getRtRange() {
        return new ExtendableRange(data.get(0).x, getLast().x);
    }

    /**
     * Returns the m/z standard deviation.
     *
     * @return the standard deviation
     */
    @Override
    public double getDeviation() {
        return deviation;
    }

    /**
     * Gets the mz-int data.
     *
     * @return the data
     */
    @Override
    public XYList getMzData() {
        return data.getYZSlice();
    }

    /**
     * Gest the last mz-int data point.
     *
     * @return the last data point
     */
    @Override
    public XYPoint getMzDataLast() {
        return new XYPoint(data.get(data.size() - 1).y, data.get(data.size() - 1).z);
    }

    /**
     * Gets the absolute mz range.
     *
     * @return the mz range
     */
    @Override
    public Range getMzRange() {
        return mzRange;
    }

    /**
     * Gets the peak id.
     *
     * @return the peak id
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * Gets the peak trace.
     *
     * @return the peak trace
     */
    @Override
    public Chromatogram getTrace(int width) {

        XYZList paddedData = getPaddedData(width);
        return new MassChromatogram(id + "", baseSignal.y, deviation, paddedData.getXZSlice());
    }

    /**
     * Gets the peak trace.
     *
     * @return the peak trace
     */
    @Override
    public Chromatogram getTrace() {
        return getTrace(0);
    }

    public XYZList getPaddedData(int width) {

        if (width == 0) return data;

        double deltaTime = data.get(1).x - data.get(0).x;
        XYZList paddedData = data.subList(1, data.size() - 1);
        for (int i = 0; i < width; i++) {
            paddedData.add(0, new XYZPoint(paddedData.get(0).x - deltaTime, paddedData.get(0).y, minIntensity));
            int paddedSize = paddedData.size() - 1;
            paddedData.add(
                    new XYZPoint(paddedData.get(paddedSize).x + deltaTime, paddedData.get(paddedSize).y, minIntensity));
        }

        return paddedData;
    }

    /**
     * Sets a property.
     *
     * @param property the property object
     */
    @Override
    public void setProperty(Property property) {

        if (property.getType() == PropertyType.Label) propertyManager.setProperty(property);
        else propertyManager.addProperty(property);
    }

    /**
     * Gets the property list identified by its name.
     *
     * @param type the property name
     * @return the property list
     */
    @Override
    public <T> Set<T> getProperty(PropertyType type, Class<T> typeClass) {
        return propertyManager.getProperty(type, typeClass);
    }

    /**
     * Whecks whether a particular property is set.
     *
     * @param type the property title
     * @return whether property is set
     */
    @Override
    public boolean hasProperty(PropertyType type) {
        return propertyManager.hasProperty(type);
    }

    /**
     * Gets the area of the peak.
     *
     * @return the peak area
     */
    @Override
    public double getArea() {
        return area;
    }

    /**
     * Sets the daughter scan index list for the peak.
     *
     * @param msnScans a daughter scan index list
     */
    @Override
    public void setMsnScans(Map<Constants.MSN, Set<Integer>> msnScans) {
        msnManager.addMsnToScanIds(msnScans);
    }

    /**
     * Gets the daughter scan index list for the peak.
     *
     * @return the scan index list
     */
    @Override
    public Map<Constants.MSN, Set<Integer>> getMsnScans() {
        return msnManager.getMsnToScanIds();
    }

    /**
     * Gets the daughter scan index list for this profile specific for the given time range.
     *
     * @param container the raw data container
     * @param timeRange the time range
     * @return the scan index list
     */
    @Override
    public Map<Constants.MSN, Set<Integer>> getMsnScans(RawContainer container, Range timeRange) {

        Map<Constants.MSN, Set<Integer>> msnToScanIds = msnManager.getMsnToScanIds();
        if (msnToScanIds.isEmpty()) return msnToScanIds;

        Map<Constants.MSN, Set<Integer>> resultMap = new HashMap<>();
        Set<Integer> resultSet = new HashSet<>();
        for (Integer scanId : msnToScanIds.get(Constants.MSN.MS2)) {
            double scanRt = container.getScan(scanId).getRetentionTime();
            if (scanRt >= timeRange.getLowerBounds() - 2 && scanRt <= timeRange.getUpperBounds() + 2)
                resultSet.add(scanId);
        }
        resultMap.put(Constants.MSN.MS2, resultSet);

        recoverMSn(msnToScanIds, resultMap, Constants.MSN.MS3);

        return resultMap;
    }

    private void recoverMSn(Map<Constants.MSN, Set<Integer>> oldMap, Map<Constants.MSN, Set<Integer>> newMap,
            Constants.MSN msn) {

        if (oldMap.containsKey(msn)) {
            Set<Integer> resultSet = new HashSet<>();
            for (int parentScanId : oldMap.get(msn)) {
                if (newMap.get(msn.up()).contains(parentScanId)) resultSet.add(parentScanId);
            }
            if (!resultSet.isEmpty()) {
                newMap.put(msn, resultSet);
                recoverMSn(oldMap, newMap, msn.down());
            }
        }
    }

    /**
     * Adds a spectrum to a particular MSn level.
     *
     * @param msn      a MSn level
     * @param spectrum a spectrum
     */
    @Override
    public void addMsnSpectrum(Constants.MSN msn, Spectrum spectrum) {
        msnManager.addMsnSpectrum(msn, spectrum);
    }

    /**
     * Whether the profile has MSn spectra.
     *
     * @param msn the msn level
     * @return whether spectra for the MSn exist
     */
    @Override
    public boolean hasMsnSpectra(Constants.MSN msn) {
        return msnManager.getSpectra(msn).size() > 0;
    }

    /**
     * Returns the list of MSn spectra of a particular level.
     *
     * @param msn the msn level
     * @return the list of MSn spectra
     */
    @Override
    public List<Spectrum> getMsnSpectra(Constants.MSN msn) {
        return msnManager.getSpectra(msn);
    }

    /**
     * Returns whether the profile has MSn scan pointers.
     *
     * @return whether MSn scan pointers exist
     */
    @Override
    public boolean hasMsnScans() {
        return msnManager.getMsnToScanIds().size() > 0;
    }

    /**
     * Gets the chromatogram time-intensity data.
     *
     * @return the time-intensity data set
     */
    @Override
    public XYZList getData() {
        return data;
    }

    /**
     * Gets the last data point.
     *
     * @return the tailing data point
     */
    @Override
    public XYZPoint getLast() {
        return data.get(data.size() - 1);
    }

    @Override
    public int hashCode() {

        int hash = 13;

        hash = (hash * 7) + Long.valueOf(Double.doubleToLongBits(deviation)).hashCode();
        hash = (hash * 7) + Long.valueOf(Double.doubleToLongBits(area)).hashCode();
        hash = (hash * 7) + baseSignal.hashCode();
        hash = (hash * 7) + mzRange.hashCode();

        return hash;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == this) return true;

        if (obj == null) return false;
        if (!(obj instanceof ProfileImpl)) return false;

        ProfileImpl profile = (ProfileImpl) obj;

        return (profile.getArea() == this.area && profile.getCenter().equals(baseSignal) && profile.getMzRange().equals(
                mzRange));
    }
}
