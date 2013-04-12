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
import uk.ac.ebi.masscascade.core.profile.ProfileImpl;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.container.ProfileContainer;
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
 * <li>Parameter <code> SCAN WINDOW </code>- The number of scans defining the time window.</li>
 * <li>Parameter <code> MIN PROFILE INTENSITY </code>- The minimum intensity of the deconvoluted profiles.</li>
 * <li>Parameter <code> DERIVATIVE THRESHOLD </code>- The minimum intensity of the 2nd derivative.</li>
 * <li>Parameter <code> PROFILE FILE </code>- The input profile container.</li>
 * </ul>
 */
public class SavitzkyGolayDeconvolution extends CallableTask {

    private double intensityThreshold;
    private int width;
    private double intensity;
    private int sgFilterWidth;
    private int profileIndex;

    private ProfileContainer profileContainer;

    private static final double RAD_ANGLE_IV = -2.0 * FastMath.PI / 180.0;

    /**
     * Constructor for a profile deconvolution task.
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
     * Sets the parameters for the profile deconvolution task.
     *
     * @param params the new parameters value
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public void setParameters(ParameterMap params) throws MassCascadeException {

        width = params.get(Parameter.SCAN_WINDOW, Integer.class);
        intensity = params.get(Parameter.MIN_PROFILE_INTENSITY, Double.class);
        intensityThreshold = params.get(Parameter.DERIVATIVE_THRESHOLD, Double.class);
        sgFilterWidth = params.get(Parameter.SG_LEVEL, Integer.class);
        profileContainer = params.get(Parameter.PROFILE_CONTAINER, ProfileContainer.class);

        profileIndex = 1;
    }

    /**
     * Executes the profile deconvolution method.
     *
     * @return the deconvoluted profile collection
     */
    @Override
    public ProfileContainer call() {

        String id = profileContainer.getId() + IDENTIFIER;
        ProfileContainer outProfileContainer = profileContainer.getBuilder().newInstance(ProfileContainer.class, id,
                profileContainer.getWorkingDirectory());

        XYList profileData;
        for (Profile profile : profileContainer) {

            profileData = profile.getTrace(sgFilterWidth).getData();

            // Calculate intensity statistics.
            double minIntensity = 0.0;
            double maxIntensity = 0.0;
            double avgIntensity = 0.0;
            for (final XYPoint dp : profileData) {

                minIntensity = FastMath.min(dp.y, minIntensity);
                maxIntensity = FastMath.max(dp.y, maxIntensity);
                avgIntensity += dp.y;
            }

            avgIntensity /= (double) profileData.size();
            final List<Profile> resolvedPeaks = new ArrayList<>(2);

            // If the current chromatogram has characteristics of background or just noise return an empty array.
            if (avgIntensity > maxIntensity / 2.0) continue;

            // Calculate second derivatives of intensity values.
            final double[] secondDerivative =
                    SavitzkyGolayDerivative.calculateDerivative(profileData, false, sgFilterWidth);

            // Calculate noise threshold.
            final double noiseThreshold = calcDerivativeThreshold(secondDerivative, intensityThreshold);

            // Search for peaks.
            final List<Profile> resolvedOriginalPeaks =
                    peaksSearch(profileData, profile, secondDerivative, noiseThreshold);

            // Apply final filter of detected peaks, according with setup parameters.
            for (final Profile p : resolvedOriginalPeaks) {

                if (p.getData().size() >= width && p.getIntensity() - p.getMinIntensity() >= intensity)
                    outProfileContainer.addProfile(p);
            }
        }

        outProfileContainer.finaliseFile();
        return outProfileContainer;
    }

    /**
     * Deconvolutes the given mass trace.
     *
     * @param profileData             the trace data
     * @param profile                 the original profile
     * @param derivativeOfIntensities the derived intensity values
     * @param intensityThreshold      the intensity threshold
     * @return the deconvoluted trace components
     */
    private List<Profile> peaksSearch(final XYList profileData, final Profile profile,
            final double[] derivativeOfIntensities, final double intensityThreshold) {

        List<Profile> profileList = new ArrayList<Profile>();

        // Flag to identify the current and next overlapped peak.
        boolean activeFirstPeak = false;
        boolean activeSecondPeak = false;

        // Flag to indicate the value of 2nd derivative pass noise threshold level.
        boolean passThreshold = false;

        // Number of times that 2nd derivative cross zero value for the current peak detection.
        int crossZero = 0;

        final int totalNumberPoints = derivativeOfIntensities.length;

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

                XYZList data = profile.getData();
                if (corStart < 0) corStart = 0;
                if (data.get(corStart).z > data.get(corStart + 1).z) while (isFoward(corStart, data)) corStart++;
                else while (isBackward(corStart, data)) corStart--;

                if (corEnd >= data.size()) corEnd = data.size() - 1;
                if (data.get(corEnd).z > data.get(corEnd - 1).z) while (isBackward(corEnd, data)) corEnd--;
                else while (isFoward(corEnd, data)) corEnd++;

                if (corEnd - corStart <= 0) continue;

                Profile deconProfile;
                if (data.get(corStart).z == Constants.MIN_ABUNDANCE)
                    deconProfile = new ProfileImpl(profileIndex, data.get(corStart), profile.getMzRange());
                else {
                    deconProfile =
                            new ProfileImpl(profileIndex, new YMinPoint(data.get(corStart).y), data.get(corStart - 1).x,
                                    profile.getMzRange());
                    deconProfile.addProfilePoint(data.get(corStart));
                }
                corStart++;
                profileIndex++;

                for (int current = corStart; current < corEnd; current++)
                    deconProfile.addProfilePoint(data.get(current));
                if (data.get(corEnd - 1).z == Constants.MIN_ABUNDANCE) deconProfile.closeProfile();
                else deconProfile.closeProfile(data.get(corEnd).x);
                deconProfile.setMsnScans(profile.getMsnScans());
                profileList.add(deconProfile);
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

        return profileList;
    }

    /**
     * Checks whether the current profile data index moves one forward:
     * atan2 in IV from 0 - -pi/2
     *
     * @param curr the current profile data index
     * @param data the profile data
     * @return whether to move one forward
     */
    private boolean isFoward(int curr, XYZList data) {
        return curr + 1 < data.size() && FastMath.atan2(data.get(curr + 1).z - data.get(curr).z,
                data.get(curr + 1).x - data.get(curr).x) <= RAD_ANGLE_IV;
    }

    /**
     * Checks whether the current profile data index moves one back:
     * atan2 in III from -pi/2 - -pi transformed to atan2 in IV from 0 - -pi/2 via x-axis inversion
     *
     * @param curr the current profile data index
     * @param data the profile data
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
