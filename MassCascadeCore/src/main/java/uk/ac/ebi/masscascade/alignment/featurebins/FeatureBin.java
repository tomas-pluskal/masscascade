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

package uk.ac.ebi.masscascade.alignment.featurebins;

import org.apache.commons.math3.util.FastMath;
import uk.ac.ebi.masscascade.core.chromatogram.MassChromatogram;
import uk.ac.ebi.masscascade.interfaces.Chromatogram;
import uk.ac.ebi.masscascade.interfaces.Feature;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.interfaces.Scan;
import uk.ac.ebi.masscascade.interfaces.container.ScanContainer;
import uk.ac.ebi.masscascade.utilities.NumberAdapter;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import java.util.HashMap;
import java.util.Map;

/**
 * A binned feature row across samples. This row keeps track of the average value of its core values and collects
 * feature - feature container associations for features that fall into this bin.
 */
public class FeatureBin extends NumberAdapter {

    public static final int COLUMNS = 6;
    private static final long serialVersionUID = -5372748569405945605L;

    private double mz;
    private double rt;
    private String label;
    private double area;
    private double mzDev;
    private Chromatogram chromatogram;
    private double[] present;

    private Map<Integer, Integer> containerIndexToFeatureId;
    private Map<Integer, Integer> groupToCount;
    private int nFeatures;

    /**
     * Constructs a binned row with a number of columns defined by its core values plus the number of columns required
     * by the maximum number of features that could potentially fall into this bin. All values are set to zero by
     * default.
     *
     * @param fileColumns the number of columns
     */
    public FeatureBin(int fileColumns) {

        mz = 0;
        rt = 0;
        label = "";
        area = 0;
        mzDev = 0;
        chromatogram = new MassChromatogram();
        present = new double[fileColumns];

        containerIndexToFeatureId = new HashMap<>();
        groupToCount = new HashMap<>();
        nFeatures = 1;
    }

    /**
     * Constructs a binned row with a number of columns defined by its core values plus the number of columns required
     * by the maximum number of features that could potentially fall into this bin. The row values are set to the values
     * in the feature.
     *
     * @param index       the index of the feature container to which the feature belongs
     * @param groupId     the group identifier of the container
     * @param feature     the first feature for the bin
     * @param fileColumns the number of columns
     */
    public FeatureBin(int index, int groupId, Feature feature, int fileColumns) {

        this(fileColumns);
        add(index, groupId, feature);
    }

    /**
     * Adds a feature to the row (bin). Updates all averaged values by the values provided in the feature. For the first
     * feature that falls into the bin, the mass chromatogram is recorded.
     *
     * @param index   the index of the feature container to which the feature belongs
     * @param groupId the group identifier of the container
     * @param feature the first feature for the bin
     */
    public void add(int index, int groupId, Feature feature) {

        mz = mz + ((feature.getMz() - mz) / nFeatures);
        rt = rt + ((feature.getRetentionTime() - rt) / nFeatures);
        area = area + ((feature.getArea() - area) / nFeatures);
        mzDev = mzDev + ((feature.getDeviation() - mzDev) / nFeatures);

        if (nFeatures == 1) chromatogram = feature.getTrace();

        present[index] = feature.getIntensity();
        if (!containerIndexToFeatureId.containsKey(index)) {
            if (groupToCount.containsKey(groupId)) {
                groupToCount.put(groupId, groupToCount.get(groupId) + 1);
            } else {
                groupToCount.put(groupId, 1);
            }
        }
        containerIndexToFeatureId.put(index, feature.getId());

        nFeatures++;
    }

    /**
     * Convenience method for reverse intensity lookups. Sets the intensity presence of a particular sample at position
     * i.
     *
     * @param i         the container index
     * @param intensity the intensity value
     */
    public void setPresent(int i, double intensity) {
        present[i] = intensity;
    }

    /**
     * Returns the average m/z value.
     *
     * @return the m/z value
     */
    public double getMz() {
        return mz;
    }

    /**
     * Returns the average rt value.
     *
     * @return the rt value
     */
    public double getRt() {
        return rt;
    }

    /**
     * Returns the row's label.
     *
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns the average area.
     *
     * @return the average area
     */
    public double getArea() {
        return area;
    }

    /**
     * Returns the average m/z deviation.
     *
     * @return the average m/z deviation
     */
    public double getMzDev() {
        return mzDev;
    }

    /**
     * Returns the mass chromatogram of the first feature.
     *
     * @return the mass chromatogram
     */
    public Chromatogram getChromatogram() {
        return chromatogram;
    }

    /**
     * Returns the feature container index to feature id map.
     *
     * @return the feature container index to feature id map
     */
    public Map<Integer, Integer> getContainerIndexToFeatureId() {
        return containerIndexToFeatureId;
    }

    /**
     * Returns the feature id for the container.
     *
     * @param containerIndex the container index
     * @return the feature id
     */
    public int getFeatureId(int containerIndex) {
        return containerIndexToFeatureId.get(containerIndex);
    }

    /**
     * Tests if a feature is present in the bin that belongs to the feature container at the specified index.
     *
     * @param index the feature container index
     * @return the intensity of the feature if present, otherwise 0
     */
    public double isPresent(int index) {
        return present[index];
    }

    /**
     * Returns the number of container in which this trace is present.
     *
     * @return the number of features
     */
    public int getnFeatures() {
        return containerIndexToFeatureId.size();
    }

    /**
     * Returns the number of container by group in which this trace is present.
     *
     * @param group the group identifier
     * @return the number of features
     */
    public int getnFeatures(int group) {

        if (groupToCount.containsKey(group)) {
            return groupToCount.get(group);
        } else {
            return 0;
        }
    }

    /**
     * Returns the value of the specified number as a <code>double</code>. This may involve rounding.
     *
     * @return the numeric value represented by this object after conversion to type <code>double</code>.
     */
    @Override
    public double doubleValue() {
        return mz;
    }

    /**
     * Returns a mz-intensity value pair containing the looked up intensity value in the scan container.
     *
     * @param scanContainer the scan container
     * @param deltaPpm      the m/z tolerance in ppm
     * @param deltaRt       the time tolerance in seconds
     * @return the mz-intensity value pair
     */
    public XYPoint reverseFill(ScanContainer scanContainer, double deltaPpm, double deltaRt) {

        Range rtRange = new ExtendableRange(rt - deltaRt, rt + deltaRt);

        XYPoint absNearestPoint = null;
        double absNearestRt = Double.MAX_VALUE;
        for (Scan scan : scanContainer) {
            if (rtRange.contains(scan.getRetentionTime())) {
                XYPoint nearestPoint = scan.getNearestPoint(mz, deltaPpm);
                if (nearestPoint == null) continue;
                else if (absNearestPoint == null) {
                    absNearestPoint = nearestPoint;
                    absNearestRt = FastMath.abs(scan.getRetentionTime() - rt);
                } else if (FastMath.abs(scan.getRetentionTime()) - rt < absNearestRt) {
                    absNearestPoint = nearestPoint;
                    absNearestRt = FastMath.abs(scan.getRetentionTime() - rt);
                }
            } else if (scan.getRetentionTime() > rtRange.getUpperBounds()) break;
        }

        return absNearestPoint;
    }
}
