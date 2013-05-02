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
import uk.ac.ebi.masscascade.core.container.file.profile.FileProfileContainer;
import uk.ac.ebi.masscascade.core.profile.ProfileImpl;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.container.ProfileContainer;
import uk.ac.ebi.masscascade.interfaces.container.RawContainer;
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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Class implementing deconvolution using a modified Biller Biehman algorithm.
 * <ul>
 * <li>Parameter <code> SCAN WINDOW </code>- The number of scans defining the time window.</li>
 * <li>Parameter <code> CENTER </code>- If found peaks should be centered around their apex.</li>
 * <li>Parameter <code> RAW FILE </code>- The input raw container.</li>
 * <li>Parameter <code> PROFILE FILE </code>- The input profile container.</li>
 * </ul>
 */
public class BiehmanDeconvolution extends CallableTask {

    private static final int MIN_SIZE = 5;

    private int scanWindow;
    private boolean center;
    private double noiseEstimate;

    private int profileId;

    private int noiseFactor;
    private ProfileContainer profileContainer;
    private RawContainer rawContainer;
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

        scanWindow = (params.get(Parameter.SCAN_WINDOW, Integer.class)) / 2;
        center = params.get(Parameter.CENTER, Boolean.class);
        noiseFactor = params.get(Parameter.NOISE_FACTOR, Integer.class);
        rawContainer = params.get(Parameter.RAW_CONTAINER, RawContainer.class);
        profileContainer = params.get(Parameter.PROFILE_CONTAINER, ProfileContainer.class);

        noiseEstimation = new NoiseEstimation();
        noiseEstimate = 0;
    }

    /**
     * Executes the task.
     *
     * @return the extracted mass traces
     */
    public ProfileContainer call() {

        String id = profileContainer.getId() + IDENTIFIER;
        ProfileContainer outProfileContainer = profileContainer.getBuilder().newInstance(ProfileContainer.class, id,
                profileContainer.getWorkingDirectory());

        profileId = 1;

        for (Profile profile : profileContainer) {

            if (profile.getMzData().size() < MIN_SIZE) continue;
            if (isNoise(profile.getTrace().getData())) continue;

            noiseEstimate = noiseEstimation.getNoiseEstimate(profile);

            List<Profile> profiles = new ArrayList<>();
            perceiveAll(profile, 0, profile.getMzData().size() - 1, profiles, -1);

            outProfileContainer.addProfileList(profiles);
        }
        outProfileContainer.finaliseFile();
        return outProfileContainer;
    }

    /**
     * Checks if the profile looks like noise
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
     * @param profile          the profile
     * @param oriLeftBoundary  the left bound
     * @param oriRightBoundary the right bound
     * @param profiles         the profile list containing perceived putative peaks
     */
    private void perceiveAll(Profile profile, int oriLeftBoundary, int oriRightBoundary, List<Profile> profiles, int pBoundary) {

        // define deconvolution window
        XYList xicData = profile.getTrace().getData();
        BiehmanWindow window = new BiehmanWindow(xicData, oriLeftBoundary, oriRightBoundary, scanWindow, noiseEstimate * noiseFactor);

        XYPoint maxDp = window.getMaxDp();
        int leftBoundary = window.getLeftBoundary();
        int rightBoundary = window.getRightBoundary();

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

            Profile deconProfile =
                    center ? buildCenteredProfile(profile, xicData, apex, window) : buildProfile(profile, xicData,
                            window);
            deconProfile.setMsnScans(profile.getMsnScans(rawContainer, deconProfile.getRtRange()));
            profiles.add(deconProfile);
        }

        // moving forward
        if (oriRightBoundary == xicData.size() - 1 && oriRightBoundary != rightBoundary && rightBoundary != pBoundary)
            perceiveAll(profile, window.getRightMinDpIndex(), xicData.size() - 1, profiles, rightBoundary);

        // moving backward
        if (oriLeftBoundary == 0 && oriLeftBoundary != leftBoundary && leftBoundary != pBoundary)
            perceiveAll(profile, 0, window.getLeftMinDpIndex(), profiles, leftBoundary);
    }

    /**
     * Builds an extracted profile from the original profile.
     *
     * @param profile the original profile
     * @param xicData the trace data of the profile
     * @param window  the Biehman deconvolution window
     * @return the time-shifted profile
     */
    private Profile buildProfile(Profile profile, XYList xicData, BiehmanWindow window) {

        int leftBoundary = window.getLeftBoundary();
        int rightBoundary = window.getRightBoundary();

        Profile deconProfile;
        if (xicData.get(leftBoundary).y != Constants.MIN_ABUNDANCE) {
            double mz = profile.getMzData().get(leftBoundary - 1).x;
            deconProfile = new ProfileImpl(profileId, new YMinPoint(mz), xicData.get(leftBoundary - 1).x,
                    profile.getMzRange());
            deconProfile.addProfilePoint(profile.getMzData().get(leftBoundary).x, xicData.get(leftBoundary));
        } else {
            double mz = profile.getMzData().get(leftBoundary).x;
            deconProfile =
                    new ProfileImpl(profileId, new YMinPoint(mz), xicData.get(leftBoundary).x, profile.getMzRange());
        }
        profileId++;

        for (int i = leftBoundary + 1; i <= rightBoundary; i++)
            deconProfile.addProfilePoint(profile.getMzData().get(i).x, xicData.get(i));

        if (xicData.get(rightBoundary).y != Constants.MIN_ABUNDANCE)
            deconProfile.closeProfile(profile.getMzData().get(rightBoundary + 1), xicData.get(rightBoundary + 1).x);
        else deconProfile.closeProfile();

        return deconProfile;
    }

    /**
     * Builds an extracted, time-shifted profile from the original profile.
     *
     * @param profile the original profile
     * @param xicData the trace data of the profile
     * @param apex    the parabola apex
     * @param window  the Biehman deconvolution window
     * @return the time-shifted profile
     */
    public Profile buildCenteredProfile(Profile profile, XYList xicData, XYPoint apex, BiehmanWindow window) {

        TreeMap<XYPoint, Double> rtIntMz = new TreeMap<XYPoint, Double>();

        XYList mzData = profile.getMzData();
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

        Profile centeredProfile = new ProfileImpl(profileId,
                new XYZPoint(rtIntMz.firstKey().x, rtIntMz.firstEntry().getValue(), rtIntMz.firstKey().y),
                profile.getMzRange());

        Iterator<XYPoint> it = rtIntMz.keySet().iterator();
        it.next();

        while (it.hasNext()) {
            shiftedDp = it.next();
            centeredProfile.addProfilePoint(new XYPoint(rtIntMz.get(shiftedDp), shiftedDp.y), shiftedDp.x);
        }
        profileId++;

        centeredProfile.closeProfile();

        return centeredProfile;
    }
}
