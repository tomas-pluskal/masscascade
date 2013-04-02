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

import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.Trace;
import uk.ac.ebi.masscascade.utilities.NumberAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Implementation of a simple m/z trace with a m/z "anchor" and a list of mz-intensity pairs that make up the trace.
 * The average m/z value is stored and updated every time a m/z-intensity pair is added.
 */
public class XYTrace implements Trace, Comparable<Trace>, Iterable<XYPoint> {

    public final double mzAnchor;
    private final List<XYPoint> mzs;

    // number of data points and average
    private int n;
    private double avg;

    /**
     * Constructs a trace with a m/z-intensity pair as anchor.
     *
     * @param anchor a m/z-intensity pair anchor
     */
    public XYTrace(XYPoint anchor) {

        this.mzAnchor = anchor.x;

        mzs = new ArrayList<XYPoint>();
        mzs.add(anchor);

        n = 1;
        avg = mzAnchor;
    }

    /**
     * Adds a m/z-intensity pair to the trace.
     *
     * @param mzIPair the m/z-intensity pair
     */
    public void add(XYPoint mzIPair) {

        mzs.add(mzIPair);
        avg = ((avg * n) + mzIPair.x) / (n + 1);
        n++;
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
     * Returns the size of the trace.
     *
     * @return the size
     */
    public int size() {
        return mzs.size();
    }

    /**
     * Returns the m/z-intensity pair at the given index.
     *
     * @param i a index
     * @return the m/z-intensity pair
     */
    public XYPoint get(int i) {
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
        if (!(obj instanceof XYTrace)) return false;

        XYTrace trace = (XYTrace) obj;
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
    public Iterator<XYPoint> iterator() {
        return mzs.iterator();
    }
}
