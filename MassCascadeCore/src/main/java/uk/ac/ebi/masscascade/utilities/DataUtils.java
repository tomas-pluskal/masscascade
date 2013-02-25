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
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.utilities.math.MathUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Class providing utility methods for list and set operations.
 */
public class DataUtils {

    /**
     * Method returning the nearest set key to a given value. The value must lie within the defined tolerance.
     *
     * @param value     a value for lookup
     * @param tolerance a tolerance (ppm)
     * @param map       a set of values to be searched
     * @return the nearest set key in the set
     */
    public static double getNearestIndexRel(double value, double tolerance, Set<Double> map) {

        List<Double> traceSourceList = new ArrayList<Double>(map);
        return getNearestIndexRel(value, tolerance, traceSourceList);
    }

    /**
     * Method returning the nearest set key to a given value. The value must lie within the defined tolerance.
     *
     * @param value     a value for lookup
     * @param tolerance a tolerance (ppm)
     * @param map       a list of values to be searched
     * @return the nearest set key in the set
     */
    public static double getNearestIndexRel(double value, double tolerance, List<Double> map) {

        double result = -1;
        if (map.size() == 0) return -1;

        int traceMapIndex = Collections.binarySearch(map, value);

        if (traceMapIndex >= 0) return map.get(traceMapIndex);
        // corrected insertion point: point above the position where the value would have been inserted
        traceMapIndex = Math.abs(traceMapIndex + 1);

        if (traceMapIndex == 0) { // if smaller than smallest entry

            double deltaPos = map.get(traceMapIndex) - value;
            double tolerancePos = MathUtils.getAbsTolerance(map.get(traceMapIndex), tolerance);
            if (deltaPos < tolerancePos) result = map.get(traceMapIndex);
        } else if (traceMapIndex == map.size()) { // if greater than greatest entry

            double deltaNeg = value - map.get(traceMapIndex - 1);
            double toleranceNeg = map.get(traceMapIndex - 1) * tolerance / Constants.PPM;
            if (deltaNeg < toleranceNeg) result = map.get(traceMapIndex - 1);
        } else { // find the nearest neighbour to the value in the list

            double deltaPos = map.get(traceMapIndex) - value;
            double tolerancePos = map.get(traceMapIndex) * tolerance / Constants.PPM;
            double deltaNeg = value - map.get(traceMapIndex - 1);
            double toleranceNeg = map.get(traceMapIndex - 1) * tolerance / Constants.PPM;
            if (deltaPos < deltaNeg && deltaPos <= tolerancePos) result = map.get(traceMapIndex);
            if (deltaPos >= deltaNeg && deltaNeg < toleranceNeg) result = map.get(traceMapIndex - 1);
        }

        return result;
    }

    /**
     * Method returning the nearest set key to a given value. The value must lie within the defined tolerance.
     *
     * @param value     the value for lookup
     * @param tolerance the tolerance (absolute)
     * @param map       the set of values to be searched
     * @return the nearest set key in the set
     */
    public static double getNearestIndexAbs(double value, double tolerance, Set<Double> map) {

        double result = -1;
        if (map.size() == 0) return result;

        List<Double> traceSourceList = new ArrayList<Double>(map);
        int traceMapIndex = Collections.binarySearch(traceSourceList, value);

        if (traceMapIndex >= 0) return traceSourceList.get(traceMapIndex);

        // corrected insertion point: point above the position where the value would have been inserted
        traceMapIndex = Math.abs(traceMapIndex + 1);

        if (traceMapIndex == traceSourceList.size()) { // if greater than greatest entry
            double deltaNeg = value - traceSourceList.get(traceMapIndex - 1);
            if (deltaNeg < tolerance) result = traceSourceList.get(traceMapIndex - 1);
        } else if (traceMapIndex == 0) { // if smaller than smallest entry
            double deltaPos = traceSourceList.get(traceMapIndex) - value;
            if (deltaPos < tolerance) result = traceSourceList.get(traceMapIndex);
        } else { // find the nearest neighbour to the value in the list
            double deltaPos = traceSourceList.get(traceMapIndex) - value;
            double deltaNeg = value - traceSourceList.get(traceMapIndex - 1);
            if (deltaPos < deltaNeg && deltaPos < tolerance) result = traceSourceList.get(traceMapIndex);
            if (deltaPos >= deltaNeg && deltaNeg < tolerance) result = traceSourceList.get(traceMapIndex - 1);
        }

        traceSourceList = null;

        return result;
    }

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
