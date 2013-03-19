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

package uk.ac.ebi.masscascade.utilities;

import uk.ac.ebi.masscascade.interfaces.Trace;

import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Class providing utility methods for list and set operations.
 */
public class DataUtils {

    /**
     * Returns the most proximate key in the trace map for the given trace.
     *
     * @param trace    the query trace
     * @param traceMap the trace map
     * @param <T>      an object
     * @return the most proximate key
     */
    public static <T> Trace getClosestKey(Trace trace, TreeMap<Trace, T> traceMap) {

        if (traceMap == null || traceMap.isEmpty()) return null;

        Trace floorKey = traceMap.floorKey(trace);
        Trace ceilingKey = traceMap.higherKey(trace);

        return getClosest(floorKey, ceilingKey, trace);
    }

    /**
     * Returns the most proximate trace in the trace set for the given query trace.
     *
     * @param trace    the query trace
     * @param valueSet the set of trace
     * @return the most proximate trace
     */
    public static Trace getClosestValue(Trace trace, TreeSet<Trace> valueSet) {

        if (valueSet == null || valueSet.isEmpty()) return null;

        Trace floorValue = valueSet.floor(trace);
        Trace ceilingValue = valueSet.higher(trace);

        return getClosest(floorValue, ceilingValue, trace);
    }

    private static Trace getClosest(Trace floor, Trace ceiling, Trace value) {

        double deltaFloor = (floor != null) ? (value.getAnchor() - floor.getAvg()) : Double.MAX_VALUE;
        double deltaCeiling = (ceiling != null) ? (ceiling.getAvg() - value.getAnchor()) : Double.MAX_VALUE;

        if (floor == null && ceiling == null) return null;

        return (deltaFloor <= deltaCeiling) ? floor : ceiling;
    }

    /**
     * Returns the most proximate key in the value map for the given value.
     *
     * @param value    the query value
     * @param traceMap the value map
     * @param <T>      an object
     * @return the most proximate key
     */
    public static <T extends Number, K> T getClosestKey(T value, TreeMap<T, K> traceMap) {

        if (traceMap == null || traceMap.isEmpty()) return null;

        T floorKey = traceMap.floorKey(value);
        T ceilingKey = traceMap.higherKey(value);

        return getClosest(floorKey, ceilingKey, value);
    }

    /**
     * Returns the most proximate value in the value set for the given query value.
     *
     * @param value    the query value
     * @param valueSet the set of values
     * @return the most proximate value
     */
    public static <T extends Number> T getClosestValue(T value, TreeSet<T> valueSet) {

        if (valueSet == null || valueSet.isEmpty()) return null;

        if (valueSet.contains(value)) return valueSet.floor(value);

        T floorValue = valueSet.floor(value);
        T ceilingValue = valueSet.higher(value);

        return getClosest(floorValue, ceilingValue, value);
    }

    private static <T extends Number> T getClosest(T floor, T ceiling, T value) {

        double deltaFloor = (floor != null) ? (value.doubleValue() - floor.doubleValue()) : Double.MAX_VALUE;
        double deltaCeiling = (ceiling != null) ? (ceiling.doubleValue() - value.doubleValue()) : Double.MAX_VALUE;

        if (floor == null && ceiling == null) return null;

        return (deltaFloor <= deltaCeiling) ? floor : ceiling;
    }
}
