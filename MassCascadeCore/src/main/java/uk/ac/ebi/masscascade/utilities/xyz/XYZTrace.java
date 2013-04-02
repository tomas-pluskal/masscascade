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

package uk.ac.ebi.masscascade.utilities.xyz;

import uk.ac.ebi.masscascade.interfaces.Trace;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class XYZTrace implements Trace, Comparable<Trace>, Iterable<XYZPoint> {

    public final double mzAnchor;
    private final List<XYZPoint> mzs;

    // number of data points and average
    private int n;
    private double avg;

    /**
     * Constructs a trace with a rt-m/z-intensity triple as anchor.
     *
     * @param anchor a rt-m/z-intensity triple anchor
     */
    public XYZTrace(XYPoint anchor, double rt) {

        this.mzAnchor = anchor.x;

        mzs = new ArrayList<XYZPoint>();
        mzs.add(new XYZPoint(rt, anchor.x, anchor.y));

        n = 1;
        avg = mzAnchor;
    }

    /**
     * Constructs a trace with set parameters.
     * @param anchor a m/z anchor
     * @param mzs a list of rt-m/z-intensity points
     * @param avg an average for the rt-m/z-intensity points
     */
    public XYZTrace(double anchor, List<XYZPoint> mzs, double avg) {

        this.mzAnchor = anchor;
        this.mzs = mzs;
        this.n = mzs.size();
        this.avg = avg;
    }

    /**
     * Adds a rt-m/z-intensity pair to the trace.
     *
     * @param rtMzIPair the rt-m/z-intensity pair
     */
    public void add(XYZPoint rtMzIPair) {

        mzs.add(rtMzIPair);
        avg = ((avg * n) + rtMzIPair.y) / (n + 1);
        n++;
    }

    /**
     * Adds a rt-m/z-intensity pair to the beginning of the trace.
     *
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

    @Override
    public int hashCode() {

        int result = 23;
        long aLong = Double.doubleToLongBits(mzAnchor);
        return (37 * result) + (int) (aLong ^ (aLong >> 32));
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;
        if (!(obj instanceof XYZTrace)) return false;

        XYZTrace trace = (XYZTrace) obj;
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
