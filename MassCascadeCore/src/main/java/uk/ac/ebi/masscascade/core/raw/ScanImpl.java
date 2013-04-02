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

package uk.ac.ebi.masscascade.core.raw;

import org.apache.log4j.Logger;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.interfaces.Scan;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;
import uk.ac.ebi.masscascade.utilities.range.ToleranceRange;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import java.util.ListIterator;

/**
 * Class representing a single mass spec scan.
 */
public class ScanImpl implements Scan, Comparable<Scan> {

    private static final Logger LOGGER = Logger.getLogger(ScanImpl.class);

    private final int scanIndex;
    private final Constants.MSN msn;
    private final int parentScan;
    private final int parentCharge;
    private final double parentMz;
    private final double retentionTime;
    private final double totalIonCurrent;
    private final Range mzRange;
    private final Constants.ION_MODE ionMode;
    private final XYList xyList;
    private final XYList basePeak;

    public ScanImpl() {

        this.scanIndex = -1;
        this.msn = Constants.MSN.MS1;
        this.parentScan = -1;
        this.parentCharge = -1;
        this.parentMz = -1;
        this.retentionTime = -1;
        this.totalIonCurrent = -1;
        this.mzRange = null;
        this.ionMode = null;
        this.xyList = null;
        this.basePeak = null;
    }

    /**
     * Constructs a fully configured scan.
     *
     * @param scanIndex       the scan index
     * @param msn             the MSn level
     * @param ionMode         the ion mode
     * @param xyList          the scan data collection
     * @param mzRange         the range in the mass domain
     * @param basePeak        the base profile
     * @param retentionTime   the retention time
     * @param totalIonCurrent the total ion current
     * @param parentScan      the parent scan reference
     * @param parentCharge    the parent scan charge
     * @param parentMz        the parent signal mz
     */
    public ScanImpl(int scanIndex, Constants.MSN msn, Constants.ION_MODE ionMode, XYList xyList, Range mzRange,
            XYList basePeak, double retentionTime, double totalIonCurrent, int parentScan, int parentCharge,
            double parentMz) {

        this.scanIndex = scanIndex;
        this.msn = msn;
        this.parentScan = parentScan;
        this.parentCharge = parentCharge;
        this.parentMz = parentMz;
        this.retentionTime = retentionTime;
        this.totalIonCurrent = totalIonCurrent;
        this.mzRange = mzRange;
        this.ionMode = ionMode;
        this.xyList = xyList;
        this.basePeak = basePeak;
    }

    /**
     * Constructs a minimal scan.
     *
     * @param scanIndex     the scan index
     * @param msn           the MSn level
     * @param ionMode       an ion mode
     * @param xyList        the scan data collection
     * @param retentionTime the retention time
     * @param parentScan    the parent scan reference
     * @param parentCharge  the parent scan charge
     * @param parentMz      the parent signal mz
     */
    public ScanImpl(int scanIndex, Constants.MSN msn, Constants.ION_MODE ionMode, XYList xyList, double retentionTime,
            int parentScan, int parentCharge, double parentMz) {

        this.scanIndex = scanIndex;
        this.msn = msn;
        this.parentScan = parentScan;
        this.parentCharge = parentCharge;
        this.parentMz = parentMz;
        this.retentionTime = retentionTime;
        this.ionMode = ionMode;
        this.xyList = xyList;
        this.basePeak = new XYList();

        if (xyList.isEmpty()) {

            this.mzRange = new ExtendableRange(0);
            this.totalIonCurrent = 0;

        } else {

            int baseIndex = 0;
            double baseIntensity = 0;
            double tmpTic = 0;

            for (int i = 0; i < xyList.size(); i++) {

                if (baseIntensity < xyList.get(i).y) {
                    baseIndex = i;
                    baseIntensity = xyList.get(i).y;
                }
                tmpTic += xyList.get(i).y;
            }

            this.mzRange = new ExtendableRange(xyList.get(0).x, xyList.get(xyList.size() - 1).x);
            this.basePeak.add(xyList.get(baseIndex));
            this.totalIonCurrent = tmpTic;
        }
    }

    /**
     * Gets the scan index.
     *
     * @return the scan index
     */
    public final int getIndex() {

        return scanIndex;
    }

    /**
     * Gets the experimental level.
     *
     * @return the experimental level
     */
    public final Constants.MSN getMsn() {

        return msn;
    }

    /**
     * Gets the parent scan index.
     *
     * @return the parent scan index.
     */
    public final int getParentScan() {

        return parentScan;
    }

    /**
     * Gets the parent charge.
     *
     * @return the parent charge
     */
    public final int getParentCharge() {

        return parentCharge;
    }

    /**
     * Gets the parent mass-to-charge.
     *
     * @return the parent mz
     */
    public final double getParentMz() {

        return parentMz;
    }

    /**
     * Gets the retention time.
     *
     * @return the retention time
     */
    public final double getRetentionTime() {

        return retentionTime;
    }

    /**
     * Gets the total ion current.
     *
     * @return the total ion current
     */
    public final double getTotalIonCurrent() {

        return totalIonCurrent;
    }

    /**
     * Gets the range in the mz domain.
     *
     * @return the mz range
     */
    public final Range getMzRange() {

        return mzRange;
    }

    /**
     * Gets the ion mode.
     *
     * @return the ion mode
     */
    public final Constants.ION_MODE getIonMode() {

        return ionMode;
    }

    /**
     * Gets the xy data.
     *
     * @return the xy data
     */
    public final XYList getData() {
        return xyList;
    }

    /**
     * Gets the base profile.
     *
     * @return the base profile
     */
    public final XYList getBasePeak() {
        return basePeak;
    }

    /**
     * Gets the overall minimum intensity.
     *
     * @return the minimum intensity
     */
    public synchronized double getMinIntensity() {

        double min = Double.MAX_VALUE;
        for (XYPoint dataPoint : xyList) {
            double d = dataPoint.y;
            if (d != 0) {
                min = (min > d) ? d : min;
            }
        }
        return min;
    }

    /**
     * Gets the nearest point for a given value in the mass domain within a tolerance.
     *
     * @param mz  the mz value
     * @param ppm the tolerance in ppm
     * @return the nearest point
     */
    public XYPoint getNearestPoint(double mz, double ppm) {

        int low = 0;
        int high = xyList.size() - 1;
        int mid;

        XYPoint midPoint;
        ListIterator<XYPoint> iter = xyList.listIterator();

        while (low <= high) {
            mid = (low + high) >>> 1;
            midPoint = get(iter, mid);

            if (midPoint.x < mz) low = mid + 1;
            else if (midPoint.x > mz) high = mid - 1;
            else return midPoint;
        }

        int ip = low;
        if (low == xyList.size()) ip = low - 1;
        else if (low == xyList.size() - 1) {
            if (xyList.get(ip).x - mz > xyList.get(ip - 1).x - mz) ip--;
        } else if (low == 0) ip = low;
        else {
            if (xyList.get(ip).x - mz > xyList.get(ip + 1).x - mz) ip++;
            else if (xyList.get(ip).x - mz > xyList.get(ip - 1).x - mz) ip--;
        }

        Range range = new ToleranceRange(mz, ppm);
        if (range.contains(xyList.get(ip).x)) return xyList.get(ip);

        return null;
    }

    /**
     * Gets the ith element from the given list by repositioning the specified
     * list listIterator.
     */
    private XYPoint get(ListIterator<XYPoint> i, int index) {

        XYPoint xyp;
        int pos = i.nextIndex();
        if (pos <= index) {
            do {
                xyp = i.next();
            } while (pos++ < index);
        } else {
            do {
                xyp = i.previous();
            } while (--pos > index);
        }
        return xyp;
    }

    /**
     * Compares two scans based on retention time.
     *
     * @param scan a scan for comparison
     * @return the result indicator
     */
    public int compareTo(Scan scan) {

        if (this.retentionTime == scan.getRetentionTime()) {
            return 0;
        } else if (this.retentionTime > scan.getRetentionTime()) {
            return 1;
        } else {
            return -1;
        }
    }

    /**
     * Checks if the content of two scans is identical.
     *
     * @param aScan the scan for comparison
     * @return true if the content of the two scans is identical
     */
    @Override
    public boolean equals(Object aScan) {

        if (this == aScan) return true;

        if (!(aScan instanceof Scan)) return false;

        Scan scan = (Scan) aScan;

        if (this.scanIndex == scan.getIndex() && this.totalIonCurrent == scan.getTotalIonCurrent() &&
                this.getMzRange().equals(scan.getMzRange()) && this.getRetentionTime() == scan.getRetentionTime() &&
                this.parentCharge == scan.getParentCharge() && this.parentMz == scan.getParentMz() &&
                this.parentScan == scan.getParentScan()) {

            int i = 0;
            for (XYPoint dataPoint : this.xyList) {

                if (dataPoint.x != scan.getData().get(i).x || dataPoint.y != scan.getData().get(i).y) return false;
                i++;
            }
            return true;
        }

        return false;
    }

    /**
     * Returns the hash code of the scan.
     *
     * @return the value
     */
    @Override
    public int hashCode() {

        int hash = 1;

        hash = hash * 17 + new Double(totalIonCurrent).hashCode();
        hash = hash * 17 + mzRange.hashCode();
        hash = hash * 17 + new Double(retentionTime).hashCode();
        hash = hash * 17 + new Double(parentCharge).hashCode();
        hash = hash * 17 + new Double(parentMz).hashCode();
        hash = hash * 17 + new Double(parentScan).hashCode();

        for (XYPoint dataPoint : this.xyList) {
            hash = hash + new Double(dataPoint.x).hashCode();
        }

        return hash;
    }
}
