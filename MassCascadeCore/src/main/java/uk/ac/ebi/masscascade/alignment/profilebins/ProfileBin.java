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

package uk.ac.ebi.masscascade.alignment.profilebins;

import org.apache.commons.math3.util.FastMath;
import uk.ac.ebi.masscascade.core.chromatogram.MassChromatogram;
import uk.ac.ebi.masscascade.interfaces.Chromatogram;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.interfaces.Scan;
import uk.ac.ebi.masscascade.interfaces.container.RawContainer;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.utilities.NumberAdapter;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A binned profile row across samples. This row keeps track of the average value of its core
 * values and collects profile - profile container associations for profiles that fall into this bin.
 */
public class ProfileBin extends NumberAdapter {

    public static final int COLUMNS = 6;
    private static final long serialVersionUID = -5372748569405945605L;

    private double mz;
    private double rt;
    private String label;
    private double area;
    private double mzDev;
    private Chromatogram chromatogram;
    private double[] present;

    private Map<Integer, Integer> containerIndexToProfileId;
    private int nProfiles;

    /**
     * Constructs a binned row with a number of columns defined by its core values plus the number of columns required
     * by the maximum number of profiles that could potentially fall into this bin. All values are set to zero by
     * default.
     *
     * @param fileColumns the number of columns
     */
    public ProfileBin(int fileColumns) {

        mz = 0;
        rt = 0;
        label = "";
        area = 0;
        mzDev = 0;
        chromatogram = new MassChromatogram();
        present = new double[fileColumns];

        containerIndexToProfileId = new HashMap<>();
        nProfiles = 1;
    }

    /**
     * Constructs a binned row with a number of columns defined by its core values plus the number of columns required
     * by the maximum number of profiles that could potentially fall into this bin. The row values are set to the
     * values in the profile.
     *
     * @param index       the index of the profile container to which the profile belongs
     * @param profile     the first profile for the bin
     * @param fileColumns the number of columns
     */
    public ProfileBin(int index, Profile profile, int fileColumns) {

        this(fileColumns);
        add(index, profile);
    }

    /**
     * Adds a profile to the row (bin). Updates all averaged values by the values provided in the profile. For the
     * first
     * profile that falls into the bin, the mass chromatogram is recorded.
     *
     * @param index   the index of the profile container to which the profile belongs
     * @param profile the first profile for the bin
     */
    public void add(int index, Profile profile) {

        mz = mz + ((profile.getMz() - mz) / nProfiles);
        rt = rt + ((profile.getRetentionTime() - rt) / nProfiles);
        area = area + ((profile.getArea() - area) / nProfiles);
        mzDev = mzDev + ((profile.getDeviation() - mzDev) / nProfiles);

        if (nProfiles == 1) chromatogram = profile.getTrace();

        present[index] = profile.getIntensity();
        containerIndexToProfileId.put(index, profile.getId());

        nProfiles++;
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
     * Returns the mass chromatogram of the first profile.
     *
     * @return the mass chromatogram
     */
    public Chromatogram getChromatogram() {
        return chromatogram;
    }

    /**
     * Returns the profile container index to profile id map.
     *
     * @return the profile container index to profile id map
     */
    public Map<Integer, Integer> getContainerIndexToProfileId() {
        return containerIndexToProfileId;
    }

    /**
     * Returns the profile id for the container.
     * @param containerIndex the container index
     * @return the profile id
     */
    public int getProfileId(int containerIndex) {
        return containerIndexToProfileId.get(containerIndex);
    }

    /**
     * Tests if a profile is present in the bin that belongs to the profile container at the specified index.
     *
     * @param index the profile container index
     * @return the intensity of the profile if present, otherwise 0
     */
    public double isPresent(int index) {
        return present[index];
    }

    /**
     * Returns the number of container in which this trace is present.
     *
     * @return the number of profiles
     */
    public int getnProfiles() {
        return containerIndexToProfileId.size();
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
     * Returns a mz-intensity value pair containing the looked up intensity value in the raw container.
     *
     * @param rawContainer the raw container
     * @param deltaPpm     the m/z tolerance in ppm
     * @param deltaRt      the time tolerance in seconds
     * @return the mz-intensity value pair
     */
    public XYPoint reverseFill(RawContainer rawContainer, double deltaPpm, double deltaRt) {

        Range rtRange = new ExtendableRange(rt - deltaRt, rt + deltaRt);

        XYPoint absNearestPoint = null;
        double absNearestRt = Double.MAX_VALUE;
        for (Scan scan : rawContainer) {
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
