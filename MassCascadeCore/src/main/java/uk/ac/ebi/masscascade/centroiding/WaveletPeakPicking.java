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

package uk.ac.ebi.masscascade.centroiding;

import uk.ac.ebi.masscascade.core.raw.RawContainer;
import uk.ac.ebi.masscascade.core.raw.ScanImpl;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.ACallableTask;
import uk.ac.ebi.masscascade.interfaces.Scan;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Class implementing a signal detection method using a continuous wavelet transform.
 * <ul>
 * <li>Parameter <code> NOISE INTENSITY </code>- The estimate of the background noise.</li>
 * <li>Parameter <code> SCALE FACTOR </code>- The scale of the wavelet.</li>
 * <li>Parameter <code> WAVELET WIDTH </code>- The width of the wavelet.</li>
 * <li>Parameter <code> RAW FILE </code>- The input raw container.</li>
 * </ul>
 */
public class WaveletPeakPicking extends ACallableTask {

    private int scaleLevel;
    private double noiseLevel;
    private double windowSize;
    private RawContainer rawContainer;

    /**
     * Parameter of the wavelet, NPOINTS is the number of wavelet values to use
     * The WAVELET_ESL & WAVELET_ESL indicates the Effective Support boundaries
     */
    private static final double NPOINTS = 60000;
    private static final int WAVELET_ESL = -5;
    private static final int WAVELET_ESR = 5;

    /**
     * Constructor for the wavelet mass detection task.
     *
     * @param params the parameter map
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public WaveletPeakPicking(ParameterMap params) throws MassCascadeException {

        super(WaveletPeakPicking.class);
        setParameters(params);
    }

    /**
     * Sets the parameters for the wavelet task.
     *
     * @param params the new parameters value
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public void setParameters(ParameterMap params) throws MassCascadeException {

        noiseLevel = params.get(Parameter.NOISE_INTENSITY, Double.class);
        scaleLevel = params.get(Parameter.SCALE_FACTOR, Integer.class);
        windowSize = params.get(Parameter.WAVELET_WIDTH, Double.class);
        rawContainer = params.get(Parameter.RAW_CONTAINER, RawContainer.class);
    }

    /**
     * Executes the task.
     *
     * @return the mass detected mass spec sample
     */
    public RawContainer call() {

        String id = rawContainer.getId() + IDENTIFIER;
        RawContainer outRawContainer =
                new RawContainer(id, rawContainer.getRawInfo(), rawContainer.getWorkingDirectory());

        for (int level = 1; level <= rawContainer.getRawLevels().size(); level++) {

            Scan scan;
            List<XYPoint> dataDataPoints;
            XYList processedData;
            List<XYPoint> waveletDataPoints;

            if (level == Constants.MSN.MS1.getLvl()) {

                for (int number : rawContainer.getScanNumbers(Constants.MSN.MS1).keySet()) {

                    scan = rawContainer.getScan(number);
                    dataDataPoints = scan.getData();
                    processedData = new XYList();

                    int length = dataDataPoints.size();
                    waveletDataPoints = new ArrayList<XYPoint>();

                    double wstep = ((WAVELET_ESR - WAVELET_ESL) / NPOINTS);
                    double[] W = new double[(int) NPOINTS];

                    double waveletIndex = WAVELET_ESL;
                    for (int j = 0; j < NPOINTS; j++) {
                        // Pre calculate the values of the wavelet
                        W[j] = cwtMEXHATreal(waveletIndex, windowSize, 0.0);
                        waveletIndex += wstep;
                    }

                    /*
                    * We only perform Translation of the wavelet in the selected scale
                    */
                    int d = (int) NPOINTS / (WAVELET_ESR - WAVELET_ESL);
                    int a_esl = scaleLevel * WAVELET_ESL;
                    int a_esr = scaleLevel * WAVELET_ESR;
                    double sqrtScaleLevel = Math.sqrt(scaleLevel);
                    for (int dx = 0; dx < length; dx++) {

                        /* Compute wavelet boundaries */
                        int t1 = a_esl + dx;
                        if (t1 < 0) t1 = 0;
                        int t2 = a_esr + dx;
                        if (t2 >= length) t2 = (length - 1);

                        /* Perform convolution */
                        double intensity = 0.0;
                        for (int i = t1; i <= t2; i++) {
                            int ind = (int) (NPOINTS / 2) - (((int) d * (i - dx) / scaleLevel) * (-1));
                            if (ind < 0) ind = 0;
                            if (ind >= NPOINTS) ind = (int) NPOINTS - 1;
                            intensity += dataDataPoints.get(i).y * W[ind];
                        }
                        intensity /= sqrtScaleLevel;
                        // Eliminate the negative part of the wavelet map
                        if (intensity < 0) intensity = 0;
                        waveletDataPoints.add(new XYPoint(dataDataPoints.get(dx).x, (double) intensity));
                    }

                    int peakMaxInd = 0;
                    int stopInd = waveletDataPoints.size() - 1;
                    List<XYPoint> rawDataPoints = new ArrayList<XYPoint>();

                    for (int ind = 0; ind <= stopInd; ind++) {

                        while ((ind <= stopInd) && (waveletDataPoints.get(ind).y == 0)) {
                            ind++;
                        }
                        peakMaxInd = ind;
                        if (ind >= stopInd) {
                            break;
                        }

                        // While profile is on
                        while ((ind <= stopInd) && (waveletDataPoints.get(ind).y > 0)) {
                            // Check if this is the maximum point of the profile
                            if (waveletDataPoints.get(ind).y > waveletDataPoints.get(peakMaxInd).y) {
                                peakMaxInd = ind;
                            }
                            rawDataPoints.add(dataDataPoints.get(ind));
                            ind++;
                        }

                        if (ind >= stopInd) {
                            break;
                        }

                        rawDataPoints.add(dataDataPoints.get(ind));

                        if (dataDataPoints.get(peakMaxInd).y > noiseLevel) {
                            XYPoint peakDataDataPoint =
                                    new XYPoint(dataDataPoints.get(peakMaxInd).x, calcAproxIntensity(rawDataPoints));

                            processedData.add(peakDataDataPoint);
                        }

                        rawDataPoints.clear();
                    }

                    if (processedData.size() == 0) continue;

                    outRawContainer.addScan(
                            new ScanImpl(scan.getIndex(), scan.getMsn(), scan.getIonMode(), processedData,
                                    scan.getRetentionTime(), scan.getParentScan(), scan.getParentCharge(),
                                    scan.getParentMz()));
                }
            } else {

                for (int number : rawContainer.getScanNumbers(Constants.MSN.get(level)).keySet()) {

                    scan = rawContainer.getScan(number);
                    outRawContainer.addScan(scan);
                }
            }
        }

        outRawContainer.finaliseFile(rawContainer.getRawInfo().getDate());
        return outRawContainer;
    }

    /**
     * Calculates the wavelets's coefficients in the time domain.
     *
     * @param x step of the wavelet
     * @param a window width of the wavelet
     * @param b offset from the center of the profile
     */
    private double cwtMEXHATreal(double x, double a, double b) {
        /* c = 2 / ( sqrt(3) * pi^(1/4) ) */
        double c = 0.8673250705840776;
        double TINY = 1E-200;
        double x2;

        if (a == 0.0) a = TINY;
        x = (x - b) / a;
        x2 = x * x;
        return c * (1.0 - x2) * Math.exp(-x2 / 2);
    }

    /**
     * Calculates the maximum intensity of the set of points.
     *
     * @param rawDataPoints set of points
     * @return the maximum intensity
     */
    private double calcAproxIntensity(List<XYPoint> rawDataPoints) {

        double aproxIntensity = 0;

        for (XYPoint d : rawDataPoints) {
            if (d.y > aproxIntensity) aproxIntensity = d.y;
        }

        return aproxIntensity;
    }
}
