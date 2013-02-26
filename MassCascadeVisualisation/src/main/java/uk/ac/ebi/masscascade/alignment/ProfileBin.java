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

package uk.ac.ebi.masscascade.alignment;

import uk.ac.ebi.masscascade.core.chromatogram.MassChromatogram;
import uk.ac.ebi.masscascade.interfaces.Chromatogram;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.utilities.NumberAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * A binned row for use with the {@link ProfileBinTableModel}. This row keeps track of the average value of its core
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

        containerIndexToProfileId = new HashMap<Integer, Integer>();
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
     * Tests if a profile is present in the bin that belongs to the profile container at the specified index.
     *
     * @param index the profile container index
     * @return the intensity of the profile if present, otherwise 0
     */
    public double isPresent(int index) {
        return present[index];
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
}
