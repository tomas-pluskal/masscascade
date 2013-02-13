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

package uk.ac.ebi.masscascade.deconvolution;

import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;
import uk.ac.ebi.masscascade.utilities.comparator.PointIntensityComparator;
import uk.ac.ebi.masscascade.utilities.math.MathUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Class to estimate the noise of a given mass trace or peak profile.
 */
public class NoiseEstimation {

    private static final int SIMPLE_MIN = 4;
    private static final int MIN_WINDOW_SIZE = 10;
    private static final int MIN_MEAN_CROSSINGS = 6;

    private LinkedList<XYPoint> queue;

    /**
     * Constructs an object for noise estimation.
     */
    public NoiseEstimation() {

    }

    /**
     * Get a noise estimate for the segment. Returns 0 if estimate not available.
     *
     * @param massTrace the mass trace segment
     * @return the noise estimate
     */
    public double getNoiseEstimate(Profile massTrace) {

        List<Double> nfs = new ArrayList<Double>();
        XYList xicData = massTrace.getTrace().getData();

        if (xicData.size() < MIN_WINDOW_SIZE) {
            return simpleEstimate((XYList) xicData.clone());
        }

        queue = new LinkedList<XYPoint>(xicData.subList(0, MIN_WINDOW_SIZE));

        for (int startPos = MIN_WINDOW_SIZE; startPos < xicData.size() - MIN_WINDOW_SIZE; startPos++) {

            double crossings = getMeanCrossings();

            if (crossings >= MIN_MEAN_CROSSINGS) {
                nfs.add(getNoiseFactor());
                break;
            }

            queue.removeFirst();
            queue.addLast(xicData.get(startPos));
        }

        double result;
        if (nfs.isEmpty()) {
            result = simpleEstimate((XYList) xicData.clone());
        } else {
            Collections.sort(nfs);
            result = MathUtils.getMedian(nfs);
        }
        return result;
    }

    /**
     * Gets the number of crossings across the mean.
     *
     * @return the number of crossings
     */
    private double getMeanCrossings() {

        int crossings = 0;
        double pointDelta;
        double lastPointDelta = queue.getFirst().y;

        double mean = MathUtils.getMeanY(queue);

        for (XYPoint dataPoint : queue) {

            pointDelta = dataPoint.y - mean;
            if ((pointDelta < 0 && lastPointDelta > 0) || (pointDelta > 0 && lastPointDelta < 0)) {
                crossings++;
            }
            lastPointDelta = pointDelta;
        }

        return crossings;
    }

    /**
     * Gets the noise factor as median deviation from the mean divided by its square root.
     *
     * @return the noise factor for the segment
     */
    private double getNoiseFactor() {

        double mean = MathUtils.getMeanY(queue);
        double[] deviations = new double[queue.size()];
        int i = 0;
        for (XYPoint dp : queue) {
            deviations[i] = Math.abs(dp.y - mean);
            i++;
        }
        Arrays.sort(deviations);
        double medianDeviation = MathUtils.getMedian(deviations);

        return (medianDeviation / Math.sqrt(mean));
    }

    /**
     * Gets the average of the lowest x intensity data points.
     *
     * @param xicData the input data
     * @return the average
     */
    private double simpleEstimate(XYList xicData) {

        Collections.sort(xicData, new PointIntensityComparator());

        double nf = 0;
        for (int i = 0; i < SIMPLE_MIN; i++) {
            nf += xicData.get(i).y;
        }

        double mean = nf / SIMPLE_MIN;
        double[] medianDeviation = new double[SIMPLE_MIN];
        for (int i = 0; i < SIMPLE_MIN; i++) {
            medianDeviation[i] = Math.abs(xicData.get(i).y - mean);
        }
        Arrays.sort(medianDeviation);

        return ((medianDeviation[1] + medianDeviation[2]) / 2) / Math.sqrt(mean);
    }
}
