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
import uk.ac.ebi.masscascade.utilities.xyz.XYZList;
import uk.ac.ebi.masscascade.utilities.xyz.YMinPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class implements a Savitzky-Golay-based deconvolution method. Savitzky-Golay coefficients are used to
 * calculate the first and second derivative (smoothed) of raw data points (intensity) that conforms each peak. The
 * first derivative is used to determine the peak's range, and the second derivative to determine the intensity of
 * the peak.
 * <ul>
 * <li>Parameter <code> SCAN_WINDOW </code>- The number of scans defining the time window.</li>
 * <li>Parameter <code> MIN_FEATURE_INTENSITY </code>- The minimum intensity of the deconvoluted features.</li>
 * <li>Parameter <code> DERIVATIVE_THRESHOLD </code>- The minimum intensity of the 2nd derivative.</li>
 * <li>Parameter <code> SCAN_CONTAINER </code>- The input raw container.</li>
 * <li>Parameter <code> FEATURE_CONTAINER </code>- The input feature container.</li>
 * </ul>
 */
public class SavitzkyGolayDeconvolution extends CallableTask {

    private double intensityThreshold;
    private int width;
    private double intensity;
    private int sgFilterWidth;
    private int featureIndex;

    private FeatureContainer featureContainer;
    private ScanContainer scanContainer;

    private static final double RAD_ANGLE_IV = -2.0 * FastMath.PI / 180.0;

    /**
     * Constructor for a feature deconvolution task.
     *
     * @param params the parameter map
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public SavitzkyGolayDeconvolution(ParameterMap params) throws MassCascadeException {

        super(SavitzkyGolayDeconvolution.class);
        setParameters(params);
    }

    /**
     * Sets the parameters for the feature deconvolution task.
     *
     * @param params the new parameters value
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public void setParameters(ParameterMap params) throws MassCascadeException {

        width = params.get(Parameter.SCAN_WINDOW, Integer.class);
        intensity = params.get(Parameter.MIN_FEATURE_INTENSITY, Double.class);
        intensityThreshold = params.get(Parameter.DERIVATIVE_THRESHOLD, Double.class);
        sgFilterWidth = params.get(Parameter.SG_LEVEL, Integer.class);
        scanContainer = params.get(Parameter.SCAN_CONTAINER, ScanContainer.class);
        featureContainer = params.get(Parameter.FEATURE_CONTAINER, FeatureContainer.class);

        featureIndex = 1;
    }

    /**
     * Executes the feature deconvolution method.
     *
     * @return the deconvoluted feature collection
     */
    @Override
    public FeatureContainer call() {

        String id = featureContainer.getId() + IDENTIFIER;
        FeatureContainer outFeatureContainer = featureContainer.getBuilder().newInstance(FeatureContainer.class, id,
                featureContainer.getIonMode(), featureContainer.getWorkingDirectory());

        XYList featureData;
        for (Feature feature : featureContainer) {

            featureData = feature.getTrace(sgFilterWidth).getData();

            // Calculate intensity statistics.
            double minIntensity = 0.0;
            double maxIntensity = 0.0;
            double avgIntensity = 0.0;
            for (final XYPoint dp : featureData) {

                minIntensity = FastMath.min(dp.y, minIntensity);
                maxIntensity = FastMath.max(dp.y, maxIntensity);
                avgIntensity += dp.y;
            }

            avgIntensity /= (double) featureData.size();
            final List<Feature> resolvedPeaks = new ArrayList<>(2);

            // If the current chromatogram has characteristics of background or just noise return an empty array.
            if (avgIntensity > maxIntensity / 2.0) continue;

            // Calculate second derivatives of intensity values.
            final double[] secondDerivative =
                    SavitzkyGolayDerivative.calculateDerivative(featureData, false, sgFilterWidth);

            // Calculate noise threshold.
            final double noiseThreshold = calcDerivativeThreshold(secondDerivative, intensityThreshold);

            // Search for peaks.
            final List<Feature> resolvedOriginalPeaks = peaksSearch(feature, secondDerivative, noiseThreshold);

            // Apply final filter of detected peaks, according with setup parameters.
            for (final Feature p : resolvedOriginalPeaks) {

                if (p.getData().size() >= width && p.getDifIntensity() - p.getMinIntensity() >= intensity)
                    outFeatureContainer.addFeature(p);
            }
        }

        outFeatureContainer.finaliseFile();
        return outFeatureContainer;
    }

    /**
     * Deconvolutes the given mass trace.
     *
     * @param feature                 the original feature
     * @param derivativeOfIntensities the derived intensity values
     * @param intensityThreshold      the intensity threshold
     * @return the deconvoluted trace components
     */
    private List<Feature> peaksSearch(final Feature feature, final double[] derivativeOfIntensities,
                                      final double intensityThreshold) {

        List<Feature> featureList = new ArrayList<>();

        // Flag to identify the current and next overlapped peak.
        boolean activeFirstPeak = false;
        boolean activeSecondPeak = false;

        // Flag to indicate the value of 2nd derivative pass noise threshold level.
        boolean passThreshold = false;

        // Number of times that 2nd derivative cross zero value for the current peak detection.
        int crossZero = 0;

        final int totalNumberPoints = derivativeOfIntensities.length;

        int pCorStart = Integer.MIN_VALUE;
        int pCorEnd = Integer.MIN_VALUE;

        // Indexes of start and ending of the current peak and beginning of the next.
        int currentPeakStart = totalNumberPoints;
        int nextPeakStart = totalNumberPoints;
        int currentPeakEnd = 0;

        // Shape analysis of derivative of chromatogram "*" represents the original chromatogram shape. "-" represents
        // the shape of chromatogram's derivative.
        for (int i = 1; i < totalNumberPoints; i++) {

            // Changing sign and crossing zero
            if (derivativeOfIntensities[i - 1] < 0.0 && derivativeOfIntensities[i] > 0.0 || derivativeOfIntensities[i
                    - 1] > 0.0 && derivativeOfIntensities[i] < 0.0) {

                if (derivativeOfIntensities[i - 1] < 0.0 && derivativeOfIntensities[i] > 0.0) {

                    if (crossZero == 2) {
                        // After second crossing zero starts the next overlapped
                        // peak, but depending of whether the passThreshold flag is activated.
                        if (passThreshold) {
                            activeSecondPeak = true;
                            nextPeakStart = i;
                        } else {
                            currentPeakStart = i;
                            crossZero = 0;
                            activeFirstPeak = true;
                        }
                    }
                }

                // Finalize the first overlapped peak.
                if (crossZero == 3) {
                    activeFirstPeak = false;
                    currentPeakEnd = i;
                }

                // Increments when detect a crossing zero event
                passThreshold = false;
                if (activeFirstPeak || activeSecondPeak) crossZero++;
            }

            // Filter for noise threshold level.
            if (FastMath.abs(derivativeOfIntensities[i]) > intensityThreshold) passThreshold = true;

            // Start peak region.
            if (crossZero == 0 && derivativeOfIntensities[i] > 0.0 && !activeFirstPeak) {
                activeFirstPeak = true;
                currentPeakStart = i;
                crossZero++;
            }

            // Finalize the peak region in case of zero values.
            if (derivativeOfIntensities[i - 1] == 0.0 && derivativeOfIntensities[i] == 0.0 && activeFirstPeak) {
                currentPeakEnd = crossZero < 3 ? 0 : i;
                activeFirstPeak = false;
                activeSecondPeak = false;
                crossZero = 0;
            }

            int corStart = currentPeakStart - sgFilterWidth + 1;
            int corEnd = currentPeakEnd - sgFilterWidth + 1;

            // If exists a detected area (difference between indexes) create a new resolved peak for this region of
            // the chromatogram.
            if (corEnd - corStart > 0 && !activeFirstPeak) {

                XYZList data = feature.getData();
                if (corStart < 0) corStart = 0;
                if (data.get(corStart).z > data.get(corStart + 1).z) while (isFoward(corStart, data)) corStart++;
                else while (isBackward(corStart, data)) corStart--;

                if (corEnd >= data.size()) corEnd = data.size() - 1;
                if (data.get(corEnd).z > data.get(corEnd - 1).z) while (isBackward(corEnd, data)) corEnd--;
                else while (isFoward(corEnd, data)) corEnd++;

                if (corEnd - corStart <= 0 || (pCorStart == corStart && pCorEnd == corEnd)) continue;

//                if (corEnd - corStart <= 0) continue;

                pCorStart = corStart;
                pCorEnd = corEnd;

                Feature deconFeature;
                if (data.get(corStart).z == Constants.MIN_ABUNDANCE)
                    deconFeature = new FeatureImpl(featureIndex, data.get(corStart), feature.getMzRange());
                else {
                    deconFeature =
                            new FeatureImpl(featureIndex, new YMinPoint(data.get(corStart).y), data.get(corStart - 1).x,
                                    feature.getMzRange());
                    deconFeature.addFeaturePoint(data.get(corStart));
                }
                corStart++;
                featureIndex++;

                for (int current = corStart; current < corEnd; current++)
                    deconFeature.addFeaturePoint(data.get(current));
                if (data.get(corEnd - 1).z == Constants.MIN_ABUNDANCE) deconFeature.closeFeature();
                else deconFeature.closeFeature(data.get(corEnd).x);
                deconFeature.setMsnScans(feature.getMsnScans(scanContainer, deconFeature.getRtRange()));
                featureList.add(deconFeature);
                // If exists next overlapped peak, swap the indexes between next and current, and clean ending index
                // for this new current peak.
                if (activeSecondPeak) {
                    activeSecondPeak = false;
                    activeFirstPeak = true;
                    crossZero = derivativeOfIntensities[i] > 0.0 ? 1 : 2;
                    currentPeakStart = nextPeakStart;
                } else {
                    crossZero = 0;
                    currentPeakStart = totalNumberPoints;
                }

                passThreshold = false;
                nextPeakStart = totalNumberPoints;
                currentPeakEnd = 0;
            }
        }

        return featureList;
    }

    /**
     * Checks whether the current feature data index moves one forward: atan2 in IV from 0 - -pi/2
     *
     * @param curr the current feature data index
     * @param data the feature data
     * @return whether to move one forward
     */
    private boolean isFoward(int curr, XYZList data) {
        return curr + 1 < data.size() && FastMath.atan2(data.get(curr + 1).z - data.get(curr).z,
                data.get(curr + 1).x - data.get(curr).x) <= RAD_ANGLE_IV;
    }

    /**
     * Checks whether the current feature data index moves one back: atan2 in III from -pi/2 - -pi transformed to atan2
     * in IV from 0 - -pi/2 via x-axis inversion
     *
     * @param curr the current feature data index
     * @param data the feature data
     * @return whether to move one back
     */
    private boolean isBackward(int curr, XYZList data) {
        return curr - 1 >= 0 && FastMath.atan2(data.get(curr - 1).z - data.get(curr).z,
                (-1 * data.get(curr - 1).x) - data.get(curr).x) <= RAD_ANGLE_IV;
    }

    /**
     * Calculates the value according with the comparative threshold.
     *
     * @param derivativeIntensities     intensity first derivative.
     * @param comparativeThresholdLevel threshold.
     * @return double derivative threshold level.
     */
    private double calcDerivativeThreshold(final double[] derivativeIntensities,
                                           final double comparativeThresholdLevel) {

        final int length = derivativeIntensities.length;
        final double[] intensities = new double[length];
        for (int i = 0; i < length; i++)
            intensities[i] = Math.abs(derivativeIntensities[i]);

        return calcQuantile(intensities, comparativeThresholdLevel);
    }

    /**
     * Calculates q-quantile value of values. q=0.5 => median
     */
    private double calcQuantile(double[] values, double q) {

        if (values.length == 0) return 0;
        if (values.length == 1) return values[0];

        if (q > 1) q = 1;
        if (q < 0) q = 0;

        double[] vals = values.clone();

        Arrays.sort(vals);

        int ind1 = (int) Math.floor((vals.length - 1) * q);
        int ind2 = (int) Math.ceil((vals.length - 1) * q);

        return (vals[ind1] + vals[ind2]) / (double) 2;
    }
}
