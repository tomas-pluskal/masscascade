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

package uk.ac.ebi.masscascade.deconvolution;

import org.apache.commons.math3.util.FastMath;
import uk.ac.ebi.masscascade.core.feature.FeatureImpl;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Feature;
import uk.ac.ebi.masscascade.interfaces.container.FeatureContainer;
import uk.ac.ebi.masscascade.interfaces.container.ScanContainer;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;
import uk.ac.ebi.masscascade.utilities.xyz.XYZPoint;
import uk.ac.ebi.masscascade.utilities.xyz.YMinPoint;
import uk.ac.ebi.masscascade.utilities.comparator.PointIntensityComparator;
import uk.ac.ebi.masscascade.utilities.math.LinearEquation;
import uk.ac.ebi.masscascade.utilities.math.MathUtils;
import uk.ac.ebi.masscascade.utilities.math.Parabola;
import uk.ac.ebi.masscascade.utilities.math.QuadraticEquation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Class implementing deconvolution using a modified Biller Biehman algorithm.
 * <ul>
 * <li>Parameter <code> SCAN_WINDOW </code>- The number of scans defining the time window.</li>
 * <li>Parameter <code> CENTER </code>- If found peaks should be centered around their apex.</li>
 * <li>Parameter <code> SCAN_FILE </code>- The input scan container.</li>
 * <li>Parameter <code> FEATURE_FILE </code>- The input feature container.</li>
 * </ul>
 */
public class BiehmanDeconvolution extends CallableTask {

    private static final int MIN_SIZE = 5;

    private boolean center;
    private double noiseEstimate;

    private int featureId;

    private int noiseFactor;
    private FeatureContainer featureContainer;
    private ScanContainer scanContainer;
    private NoiseEstimation noiseEstimation;

    /**
     * Constructor for the Biehman deconvolution task.
     *
     * @param params the parameter map
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public BiehmanDeconvolution(ParameterMap params) throws MassCascadeException {

        super(BiehmanDeconvolution.class);
        setParameters(params);
    }

    /**
     * Sets the parameters for the Biehman deconvolution task.
     *
     * @param params the new parameter values
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public void setParameters(ParameterMap params) throws MassCascadeException {

        center = params.get(Parameter.CENTER, Boolean.class);
        noiseFactor = params.get(Parameter.NOISE_FACTOR, Integer.class);
        scanContainer = params.get(Parameter.SCAN_CONTAINER, ScanContainer.class);
        featureContainer = params.get(Parameter.FEATURE_CONTAINER, FeatureContainer.class);

        noiseEstimation = new NoiseEstimation();
        noiseEstimate = 0;
    }

    /**
     * Executes the task.
     *
     * @return the extracted mass traces
     */
    public FeatureContainer call() {

        String id = featureContainer.getId() + IDENTIFIER;
        FeatureContainer outFeatureContainer = featureContainer.getBuilder().newInstance(FeatureContainer.class, id,
                featureContainer.getIonMode(), featureContainer.getWorkingDirectory());

        featureId = 1;

        for (Feature feature : featureContainer) {

            if (feature.getMzData().size() < MIN_SIZE) continue;
            if (isNoise(feature.getTrace(2).getData())) continue;

            noiseEstimate = noiseEstimation.getNoiseEstimate(feature);

            List<Feature> features = new ArrayList<>();
            perceiveAll(feature, 0, feature.getMzData().size() - 1, features, -1);

            outFeatureContainer.addFeatureList(features);
        }
        outFeatureContainer.finaliseFile();
        return outFeatureContainer;
    }

    /**
     * Checks if the feature looks like noise
     *
     * @param xicData the trace data
     * @return boolean if noise
     */
    private boolean isNoise(XYList xicData) {

        double avgInt = 0;
        double maxInt = 0;

        for (XYPoint dp : xicData) {
            avgInt += dp.y;
            maxInt = maxInt < dp.y ? dp.y : maxInt;
        }

        avgInt /= xicData.size();

        return (avgInt > maxInt / 2.0);
    }

    /**
     * Perceives all putative peaks within the trace.
     *
     * @param feature          the feature
     * @param oriLeftBoundary  the left bound
     * @param oriRightBoundary the right bound
     * @param features         the feature list containing perceived putative peaks
     */
    private void perceiveAll(Feature feature, int oriLeftBoundary, int oriRightBoundary, List<Feature> features,
                             int pBoundary) {

        // define deconvolution window
        XYList xicData = feature.getTrace().getData();
        BiehmanWindow window =
                new BiehmanWindow(xicData, oriLeftBoundary, oriRightBoundary, noiseEstimate * noiseFactor);

        XYPoint maxDp = window.getMaxDp();
        int leftBoundary = window.getLeftBoundary();
        int rightBoundary = window.getRightBoundary();

        // if the maximum data point is at the boundary: ignore artifcat of previous deconvolution
        if (window.getMaxDpIndex() > leftBoundary && window.getMaxDpIndex() < rightBoundary) {
            // linear background estimation
            LinearEquation background = new LinearEquation(window.getLeftMinDp(), window.getRightMinDp());

            // get deviation from background and sort by intensity
            TreeSet<XYPoint> dpDevs = new TreeSet<>(new PointIntensityComparator());
            for (int i = leftBoundary; i <= rightBoundary; i++) {
                double corY = FastMath.abs(xicData.get(i).y - background.getY(xicData.get(i).x));
                dpDevs.add(new XYPoint(i, corY));
            }

            // least squares background estimation from the lower half of the deviation array
            int half = 0;
            XYList xicDataHalf = new XYList();
            for (XYPoint dp : dpDevs) {
                xicDataHalf.add(xicData.get((int) dp.x));
                if (half == (dpDevs.size() / 2)) break;
                half++;
            }
            LinearEquation backgroundSq = MathUtils.getLeastSquares(xicDataHalf);

            // check height of maximum data point
            double maxHeight = maxDp.y - backgroundSq.getY(maxDp.x);
            if (maxHeight >= noiseFactor * noiseEstimate * Math.sqrt(maxDp.y)) {

                // calculate precise retention time via three-point parabola
                XYPoint dpL = xicData.get(window.getMaxDpIndex() - 1);
                XYPoint dpR = xicData.get(window.getMaxDpIndex() + 1);
                XYPoint apex = MathUtils.getParabolaVertex(dpL, maxDp, dpR);

                Feature deconFeature =
                        center ? buildCenteredFeature(feature, xicData, apex, window) : buildFeature(feature, xicData,
                                window);
                deconFeature.setMsnScans(feature.getMsnScans(scanContainer, deconFeature.getRtRange()));
                features.add(deconFeature);
            }
        }

        // moving forward
        if (oriRightBoundary == xicData.size() - 1 && oriRightBoundary != rightBoundary && rightBoundary != pBoundary)
            perceiveAll(feature, window.getRightMinDpIndex(), xicData.size() - 1, features, rightBoundary);

        // moving backward
        if (oriLeftBoundary == 0 && oriLeftBoundary != leftBoundary && leftBoundary != pBoundary)
            perceiveAll(feature, 0, window.getLeftMinDpIndex(), features, leftBoundary);
    }

    /**
     * Builds an extracted feature from the original feature.
     *
     * @param feature the original feature
     * @param xicData the trace data of the feature
     * @param window  the Biehman deconvolution window
     * @return the time-shifted feature
     */
    private Feature buildFeature(Feature feature, XYList xicData, BiehmanWindow window) {

        int leftBoundary = window.getLeftBoundary();
        int rightBoundary = window.getRightBoundary();

        Feature deconFeature;
        if (xicData.get(leftBoundary).y != Constants.MIN_ABUNDANCE) {
            double mz = feature.getMzData().get(leftBoundary - 1).x;
            deconFeature = new FeatureImpl(featureId, new YMinPoint(mz), xicData.get(leftBoundary - 1).x,
                    feature.getMzRange());
            deconFeature.addFeaturePoint(feature.getMzData().get(leftBoundary).x, xicData.get(leftBoundary));
        } else {
            double mz = feature.getMzData().get(leftBoundary).x;
            deconFeature =
                    new FeatureImpl(featureId, new YMinPoint(mz), xicData.get(leftBoundary).x, feature.getMzRange());
        }
        featureId++;

        for (int i = leftBoundary + 1; i <= rightBoundary; i++)
            deconFeature.addFeaturePoint(feature.getMzData().get(i).x, xicData.get(i));

        if (xicData.get(rightBoundary).y != Constants.MIN_ABUNDANCE)
            deconFeature.closeFeature(feature.getMzData().get(rightBoundary + 1), xicData.get(rightBoundary + 1).x);
        else deconFeature.closeFeature();

        return deconFeature;
    }

    /**
     * Builds an extracted, time-shifted feature from the original feature.
     *
     * @param feature the original feature
     * @param xicData the trace data of the feature
     * @param apex    the parabola apex
     * @param window  the Biehman deconvolution window
     * @return the time-shifted feature
     */
    public Feature buildCenteredFeature(Feature feature, XYList xicData, XYPoint apex, BiehmanWindow window) {

        TreeMap<XYPoint, Double> rtIntMz = new TreeMap<XYPoint, Double>();

        XYList mzData = feature.getMzData();
        double rtShift = apex.x - window.getMaxDp().x;

        rtIntMz.put(apex, mzData.get(window.getMaxDpIndex()).x);
        Parabola parabola = new Parabola();
        QuadraticEquation eq;
        XYPoint shiftedDp;

        // reverse
        for (int i = window.getMaxDpIndex() - 1; i >= window.getLeftBoundary(); i--) {

            if (i == window.getLeftBoundary()) {

                if (xicData.get(i).y == Constants.MIN_ABUNDANCE) {
                    rtIntMz.put(new YMinPoint(xicData.get(i).x + rtShift), mzData.get(i).x);
                } else {
                    XYPoint zDp = new YMinPoint(xicData.get(i - 1).x);
                    eq = parabola.solveParabola(zDp, xicData.get(i), rtIntMz.firstKey());
                    shiftedDp = eq.getDpY(xicData.get(i).x + rtShift);
                    if (shiftedDp.y <= Constants.MIN_ABUNDANCE) {
                        rtIntMz.put(new YMinPoint(shiftedDp.x), mzData.get(i).x);
                    } else {
                        rtIntMz.put(shiftedDp, mzData.get(i).x);
                        rtIntMz.put(new YMinPoint(xicData.get(i - 1).x + rtShift), mzData.get(i).x);
                    }
                }
                break;
            }

            eq = parabola.solveParabola(xicData.get(i - 1), xicData.get(i), rtIntMz.firstKey());
            shiftedDp = eq.getDpY(xicData.get(i).x + rtShift);

            if (shiftedDp.y <= Constants.MIN_ABUNDANCE) {
                rtIntMz.put(new YMinPoint(shiftedDp.x), mzData.get(i).x);
            } else {
                rtIntMz.put(shiftedDp, mzData.get(i).x);
            }
        }

        // forward
        for (int i = window.getMaxDpIndex() + 1; i <= window.getRightBoundary(); i++) {

            if (i == window.getRightBoundary()) {

                if (xicData.get(i).y == Constants.MIN_ABUNDANCE) {
                    rtIntMz.put(new YMinPoint(xicData.get(i).x + rtShift), mzData.get(i).x);
                } else {
                    XYPoint zDp = new YMinPoint(xicData.get(i + 1).x);
                    eq = parabola.solveParabola(zDp, xicData.get(i), rtIntMz.lastKey());
                    shiftedDp = eq.getDpY(xicData.get(i).x + rtShift);
                    if (shiftedDp.y <= Constants.MIN_ABUNDANCE) {
                        rtIntMz.put(new YMinPoint(shiftedDp.x), mzData.get(i).x);
                    } else {
                        rtIntMz.put(shiftedDp, mzData.get(i).x);
                        rtIntMz.put(new YMinPoint(xicData.get(i + 1).x + rtShift), mzData.get(i).x);
                    }
                }
                break;
            }

            eq = parabola.solveParabola(xicData.get(i + 1), xicData.get(i), rtIntMz.lastKey());
            shiftedDp = eq.getDpY(xicData.get(i).x + rtShift);

            if (shiftedDp.y <= Constants.MIN_ABUNDANCE) {
                rtIntMz.put(new YMinPoint(shiftedDp.x), mzData.get(i).x);
            } else {
                rtIntMz.put(shiftedDp, mzData.get(i).x);
            }
        }

        Feature centeredFeature = new FeatureImpl(featureId,
                new XYZPoint(rtIntMz.firstKey().x, rtIntMz.firstEntry().getValue(), rtIntMz.firstKey().y),
                feature.getMzRange());

        Iterator<XYPoint> it = rtIntMz.keySet().iterator();
        it.next();

        while (it.hasNext()) {
            shiftedDp = it.next();
            centeredFeature.addFeaturePoint(new XYPoint(rtIntMz.get(shiftedDp), shiftedDp.y), shiftedDp.x);
        }
        featureId++;

        centeredFeature.closeFeature();

        return centeredFeature;
    }
}
