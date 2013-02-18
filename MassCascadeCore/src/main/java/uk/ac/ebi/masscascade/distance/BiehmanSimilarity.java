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

package uk.ac.ebi.masscascade.distance;

import uk.ac.ebi.masscascade.core.file.profile.FileProfileContainer;
import uk.ac.ebi.masscascade.core.file.spectrum.FileSpectrumContainer;
import uk.ac.ebi.masscascade.core.spectrum.PseudoSpectrum;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.interfaces.Spectrum;
import uk.ac.ebi.masscascade.interfaces.container.SpectrumContainer;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Groups all profiles into pseudospectra based on their retention time and profile using a modified Biehman approach
 * to component perception. The method is still experimental.
 * <ul>
 * <li>Parameter <code> PROFILE_CONTAINER </code>- The input raw container.</li>
 * <li>Parameter <code> BINS </code>- The number of bins.</li>
 * <li>Parameter <code> SCAN WINDOW </code>- The approximate distance between two scans in seconds.</li>
 * </ul>
 */
public class BiehmanSimilarity extends CallableTask {

    private FileProfileContainer profileContainer;
    private SpectrumContainer spectrumContainer;

    private int binNumber;
    private double scanDistance;

    private static final int N_MAX = 3;

    /**
     * Constructs a Biehman similarity task.
     *
     * @param params the parameter map
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public BiehmanSimilarity(ParameterMap params) throws MassCascadeException {

        super(BiehmanSimilarity.class);
        setParameters(params);
    }

    /**
     * Sets the parameters for the Biehman similarity task.
     *
     * @param params the new parameter values
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public void setParameters(ParameterMap params) throws MassCascadeException {

        profileContainer = params.get(Parameter.PROFILE_CONTAINER, FileProfileContainer.class);
        binNumber = params.get(Parameter.BINS, Integer.class);
        scanDistance = params.get(Parameter.SCAN_WINDOW, Double.class);
    }

    /**
     * Executes the Biehman similarity task.
     *
     * @return the trace container
     */
    public SpectrumContainer call() {

        String id = profileContainer.getId() + IDENTIFIER;
        spectrumContainer = new FileSpectrumContainer(id, profileContainer.getWorkingDirectory());

        Range rtRange = new ExtendableRange();
        for (double rt : profileContainer.getTimes().keySet()) {
            rtRange.extendRange(rt);
        }

        double binWidth = scanDistance / binNumber;
        int noOfBins = (int) (rtRange.getSize() / binWidth) + 1;
        Bin[] bins = new Bin[noOfBins];

        Profile profile;
        for (int profileId : profileContainer.getProfileNumbers().keySet()) {

            profile = profileContainer.getProfile(profileId);
            XYList profileData = profile.getTrace().getData();

            int traceMax = -1;
            for (XYPoint dp : profileData) {
                traceMax++;
                if (dp.x == profile.getRetentionTime()) break;
            }

            int reachMax = 1;
            double sharpnessMaxL = 0;
            for (int leftI = traceMax - 1; leftI > 0; leftI++) {

                sharpnessMaxL = Math.max(sharpnessMaxL,
                        profileData.get(traceMax).y - profileData.get(leftI).y) / (reachMax * Math.sqrt(
                        profileData.get(traceMax).y));

                if (reachMax == N_MAX) break;
                reachMax++;
            }

            reachMax = 1;
            double sharpnessMaxR = 0;
            for (int rightI = traceMax + 1; rightI < profileData.size(); rightI++) {

                sharpnessMaxR = Math.max(sharpnessMaxR,
                        profileData.get(traceMax).y - profileData.get(rightI).y) / (reachMax * Math.sqrt(
                        profileData.get(traceMax).y));

                if (reachMax == N_MAX) break;
                reachMax++;
            }

            int binIndex = (int) ((profile.getRetentionTime() - rtRange.getLowerBounds()) / binWidth);

            if (bins[binIndex] == null) {
                bins[binIndex] = new Bin((sharpnessMaxL + sharpnessMaxR) / 2d, profile.getId());
            } else {
                bins[binIndex].add((sharpnessMaxL + sharpnessMaxR) / 2d, profile.getId());
            }
        }

        Set<Integer> allProfileIds = new HashSet<Integer>(profileContainer.getProfileNumbers().keySet());

        int index = 1;
        boolean isComponent = false;
        for (int i = 1; i < bins.length - 1; i++) {

            if (isComponent) {
                isComponent = false;
                continue;
            }
            isComponent = true;

            if (bins[i] == null) continue;

            double m = bins[i].getValue();
            double l = bins[i - 1] == null ? 0 : bins[i - 1].getValue();
            double r = bins[i + 1] == null ? 0 : bins[i + 1].getValue();

            if (m > l && m > r) {
                int reach = (int) Math.round((binNumber / (m + l + r)) * 10);

                int reachMax = 1;
                for (int down = i - 1; down >= 0; down--) {

                    l = bins[down] == null ? 0 : bins[down].getValue();
                    if (l > m) isComponent = false;

                    if (reachMax == reach) break;
                    reachMax++;
                }

                reachMax = 1;
                for (int up = i + 1; up < bins.length; up++) {

                    r = bins[up] == null ? 0 : bins[up].getValue();
                    if (r > m) isComponent = false;

                    if (reachMax == reach) break;
                    reachMax++;
                }
            }

            if (isComponent) {
                Set<Profile> profileSet = new HashSet<Profile>();

                Range specRange = new ExtendableRange();
                XYList spectrumData = new XYList();
                double rt = 0;

                List<Integer> tmpIds = new ArrayList<Integer>();
                tmpIds.addAll(bins[i].getProfileIds());
                if (bins[i - 1] != null) tmpIds.addAll(bins[i - 1].getProfileIds());
                if (bins[i + 1] != null) tmpIds.addAll(bins[i + 1].getProfileIds());

                for (int profileId : tmpIds) {
                    profile = profileContainer.getProfile(profileId);
                    profileSet.add(profile);
                    spectrumData.add(profile.getMzIntDp());
                    specRange.extendRange(profile.getRetentionTime());
                    rt += profile.getRetentionTime();
                }

                rt /= tmpIds.size();
                Collections.sort(spectrumData);

                Spectrum pseudoSpectrum = new PseudoSpectrum(index, spectrumData, specRange, rt, profileSet);
                spectrumContainer.addSpectrum(pseudoSpectrum);

                allProfileIds.removeAll(tmpIds);
                index++;
            }
        }

        for (int profileId : allProfileIds) {

            profile = profileContainer.getProfile(profileId);
            Set<Profile> profileSet = new HashSet<Profile>();
            profileSet.add(profile);

            double rt = profile.getRetentionTime();
            Range specRange = new ExtendableRange(rt);

            XYList spectrumData = new XYList();
            spectrumData.add(profile.getMzIntDp());

            Spectrum pseudoSpectrum = new PseudoSpectrum(index, spectrumData, specRange, rt, profileSet);
            spectrumContainer.addSpectrum(pseudoSpectrum);

            index++;
        }

        spectrumContainer.finaliseFile();
        return spectrumContainer;
    }

    /**
     * Inner class representing a signal bin on the time axis.
     */
    class Bin {

        private double value;
        private List<Integer> profileIds;

        public Bin() {

            value = 0;
            profileIds = new ArrayList<Integer>();
        }

        public Bin(double value, int profileId) {

            this.value = value;

            this.profileIds = new ArrayList<Integer>();
            profileIds.add(profileId);
        }

        public void add(double value, int profileId) {

            this.value += value;
            this.profileIds.add(profileId);
        }

        public double getValue() {

            return value;
        }

        public List<Integer> getProfileIds() {

            return profileIds;
        }
    }
}


