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

import uk.ac.ebi.masscascade.core.container.file.profile.FileProfileContainer;
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
 * <li>Parameter <code> SCAN WINDOW </code>- The number of scans definint the time window.</li>
 * <li>Parameter <code> MIN PROFILE INTENSITY </code>- The minimum intensity of the deconvoluted profiles.</li>
 * <li>Parameter <code> DERIVATIVE THRESHOLD </code>- The minimum intensity of the 2nd derivative.</li>
 * <li>Parameter <code> PROFILE FILE </code>- The input profile container.</li>
 * </ul>
 */
public class SavitzkyGolayDeconvolution extends CallableTask {

    // Savitzky-Golay filter width.
    private static final int SG_FILTER_LEVEL = 12;

    private double intensityThreshold;
    private double width;
    private double intensity;
    private int profileIndex;

    private ProfileContainer profileContainer;

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

        width = params.get(Parameter.SCAN_WINDOW, Double.class);
        intensity = params.get(Parameter.MIN_PROFILE_INTENSITY, Double.class);
        intensityThreshold = params.get(Parameter.DERIVATIVE_THRESHOLD, Double.class);
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

            profileData = profile.getTrace().getData();

            // Calculate intensity statistics.
            double maxIntensity = 0.0;
            double avgIntensity = 0.0;
            for (final XYPoint dp : profileData) {

                maxIntensity = Math.max(dp.y, maxIntensity);
                avgIntensity += dp.y;
            }

            avgIntensity /= (double) profileData.size();

            final List<Profile> resolvedPeaks = new ArrayList<Profile>(2);

            // If the current chromatogram has characteristics of background or just noise return an empty array.
            if (avgIntensity > maxIntensity / 2.0) continue;

            // Calculate second derivatives of intensity values.
            final double[] secondDerivative =
                    SavitzkyGolayDerivative.calculateDerivative(profileData, false, SG_FILTER_LEVEL);

            // Calculate noise threshold.
            final double noiseThreshold = calcDerivativeThreshold(secondDerivative, intensityThreshold);

            // Search for peaks.
            final List<Profile> resolvedOriginalPeaks =
                    peaksSearch(profileData, profile, secondDerivative, noiseThreshold);

            // Apply final filter of detected peaks, according with setup parameters.
            for (final Profile p : resolvedOriginalPeaks) {

                if (p.getTrace().getData().size() >= width && p.getMzIntDp().y >= intensity) {
                    outProfileContainer.addProfile(p);
                }
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
        //
        // " *** " * * + " + * * + + " + x x + "--+-*-+-----+-*---+---- " + + "
        // + + " +
        //
        for (int i = 1; i < totalNumberPoints; i++) {

            // Changing sign and crossing zero
            if (derivativeOfIntensities[i - 1] < 0.0 && derivativeOfIntensities[i] > 0.0 || derivativeOfIntensities[i
                    - 1] > 0.0 && derivativeOfIntensities[i] < 0.0) {

                if (derivativeOfIntensities[i - 1] < 0.0 && derivativeOfIntensities[i] > 0.0) {

                    if (crossZero == 2) {

                        // After second crossing zero starts the next overlapped
                        // peak, but depending of
                        // passThreshold flag is activated.
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
                if (activeFirstPeak || activeSecondPeak) {

                    crossZero++;
                }
            }

            // Filter for noise threshold level.
            if (Math.abs(derivativeOfIntensities[i]) > intensityThreshold) {

                passThreshold = true;
            }

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

            // If the peak starts in a region with no data points, move the start to the first available data point.
            while (currentPeakStart < profileData.size() - 1) {

                if (profileData.get(currentPeakStart) == null) {
                    currentPeakStart++;
                } else {
                    break;
                }
            }

            // Scan the peak from the beginning and if we find a missing data point inside, we have to finish the
            // peak there.
            for (int newEnd = currentPeakStart; newEnd <= currentPeakEnd; newEnd++) {

                if (profileData.get(newEnd) == null) {
                    currentPeakEnd = newEnd - 1;
                    break;
                }
            }

            // If exists a detected area (difference between indexes) create a new resolved peak for this region of
            // the chromatogram.
            if (currentPeakEnd - currentPeakStart > 0 && !activeFirstPeak) {

                Profile deconProfile;
                XYList mzData = profile.getMzData();
                if (mzData.get(currentPeakStart).y == Constants.MIN_ABUNDANCE) {
                    deconProfile = new ProfileImpl(profileIndex, mzData.get(currentPeakStart),
                            profileData.get(currentPeakStart).x, profile.getMzRange());
                } else {
                    deconProfile = new ProfileImpl(profileIndex, new YMinPoint(mzData.get(currentPeakStart).x),
                            profileData.get(currentPeakStart - 1).x, profile.getMzRange());
                }
                profileIndex++;

                for (int current = currentPeakStart; current < currentPeakEnd; current++) {

                    deconProfile.addProfilePoint(mzData.get(current), profileData.get(current).x);
                }
                if (mzData.get(currentPeakEnd - 1).y == Constants.MIN_ABUNDANCE) {
                    deconProfile.closeProfile();
                } else {
                    deconProfile.closeProfile(profileData.get(currentPeakEnd).x);
                }
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
        for (int i = 0; i < length; i++) {

            intensities[i] = Math.abs(derivativeIntensities[i]);
        }

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

        double[] vals = (double[]) values.clone();

        Arrays.sort(vals);

        int ind1 = (int) Math.floor((vals.length - 1) * q);
        int ind2 = (int) Math.ceil((vals.length - 1) * q);

        return (vals[ind1] + vals[ind2]) / (double) 2;
    }
}
