/*
 * Copyright (C) 2014 EMBL - European Bioinformatics Institute
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
package uk.ac.ebi.masscascade.utilities.xyz;

import com.google.common.collect.Lists;
import uk.ac.ebi.masscascade.interfaces.Trace;

import java.util.Iterator;
import java.util.List;

public class ExXYZTrace implements Trace, Comparable<Trace>, Iterable<XYZPoint> {

    public final double mzAnchor;
    private final List<XYZPoint> mzs;

    // number of data points and average
    private int n;
    private double avg;
    private double rtSum;
    private double intRSum;
    private double intRSumSq;
    private double intSum;
    private double intSumSq;


    /**
     * Constructs a trace with a rt-m/z-intensity triple as anchor.
     */
    public ExXYZTrace(double mz, double intensity, double ratio, double rt) {

        this.mzAnchor = mz;
        mzs = Lists.newArrayList(new XYZPoint(rt, mz, intensity));

        n = 1;
        avg = mzAnchor;
        rtSum = rt;
        intRSum = ratio;
        intRSumSq = ratio * ratio;
        intSum = intensity;
        intSumSq = intensity * intensity;
    }

    /**
     * Adds a rt-m/z-intensity pair to the trace.
     *
     * @param rtMzIPair the rt-m/z-intensity pair
     */
    public void add(XYZPoint rtMzIPair, double ratio) {

        mzs.add(rtMzIPair);
        avg = ((avg * n) + rtMzIPair.y) / (n + 1);
        intRSum += ratio;
        intRSumSq += ratio * ratio;
        intSum += rtMzIPair.z;
        intSumSq += rtMzIPair.z * rtMzIPair.z;
        rtSum += rtMzIPair.x;
        n++;
    }

    /**
     * Adds a rt-m/z-intensity pair to the beginning of the trace.
     * <p/>
     * Statistics are not updated.
     *
     * @param rtMzIPair the rt-m/z-intensity pair
     */
    public void push(XYZPoint rtMzIPair) {
        mzs.add(0, rtMzIPair);
    }

    /**
     * Returns the anchor of the trace.
     *
     * @return the anchor
     */
    public double getAnchor() {
        return mzAnchor;
    }

    /**
     * Returns the mzs list.
     *
     * @return the mzs points
     */
    public List<XYZPoint> getData() {
        return mzs;
    }

    /**
     * Returns the size of the trace.
     *
     * @return the size
     */
    public int size() {
        return mzs.size();
    }

    /**
     * Returns the rt-m/z-intensity pair at the given index.
     *
     * @param i a index
     * @return the rt-m/z-intensity pair
     */
    public XYZPoint get(int i) {
        return mzs.get(i);
    }

    /**
     * Returns the m/z average of the trace.
     *
     * @return the m/z average
     */
    public double getAvg() {
        return avg;
    }

    /**
     * Returns the intensity average of the trace.
     *
     * @return the intensity average
     */
    public double getAvgInt() {

        double mean = 0.0;
        if (n > 0) {
            mean = this.intSum / this.n;
        }
        return mean;
    }

    /**
     * Returns the intensity ratio average of the trace.
     *
     * @return the intensity ratio average
     */
    public double getAvgIntRatio() {

        double mean = 0.0;
        if (n > 0) {
            mean = this.intRSum / this.n;
        }
        return mean;
    }

    /**
     * Returns the intensity std. deviation of the trace.
     *
     * @return the intensity std. deviation
     */
    public double getStdDevInt() {

        double deviation = 1.0; // careful
        if (n > 1) {
            deviation = Math.sqrt((intSumSq - intSum * intSum / n) / (n - 1));
        }
        return deviation;
    }

    /**
     * Returns the ratio intensity std. deviation of the trace.
     *
     * @return the ratio intensity std. deviation
     */
    public double getStdDevIntRatio() {

        double deviation = 1.0; // careful
        if (n > 1) {
            deviation = Math.sqrt((intRSumSq - intRSum * intRSum / n) / (n - 1));
        }

        return deviation == 0 ? 1 : deviation;
    }

    public double getAvgRt() {

        double mean = 0.0;
        if (n > 0) {
            mean = this.rtSum / this.n;
        }
        return mean;
    }

    @Override
    public int hashCode() {

        int result = 23;
        long aLong = Double.doubleToLongBits(mzAnchor);
        return (37 * result) + (int) (aLong ^ (aLong >> 32));
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;
        if (!(obj instanceof ExXYZTrace)) return false;

        ExXYZTrace trace = (ExXYZTrace) obj;
        return this.mzAnchor == trace.mzAnchor;
    }

    @Override
    public int compareTo(Trace trace) {

        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        if (this == trace) return EQUAL;
        if (this.mzAnchor < trace.getAnchor()) return BEFORE;
        if (this.mzAnchor > trace.getAnchor()) return AFTER;

        assert this.equals(trace) : "compareTo inconsistent with equals";

        return EQUAL;
    }

    @Override
    public Iterator<XYZPoint> iterator() {
        return mzs.iterator();
    }
}