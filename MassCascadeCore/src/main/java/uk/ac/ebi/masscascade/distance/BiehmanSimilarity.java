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

package uk.ac.ebi.masscascade.distance;

import org.apache.log4j.Level;
import uk.ac.ebi.masscascade.core.spectrum.PseudoSpectrum;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.interfaces.Spectrum;
import uk.ac.ebi.masscascade.interfaces.container.ProfileContainer;
import uk.ac.ebi.masscascade.interfaces.container.SpectrumContainer;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;
import uk.ac.ebi.masscascade.utilities.xyz.XYZList;
import uk.ac.ebi.masscascade.utilities.xyz.XYZPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

/**
 * Groups all profiles into pseudospectra based on their retention time and profile using a modified Biehman approach
 * to component perception.
 * <ul>
 * <li>Parameter <code> PROFILE_CONTAINER </code>- The input raw container.</li>
 * <li>Parameter <code> BINS </code>- The number of bins.</li>
 * <li>Parameter <code> TIME WINDOW </code>- The approximate distance between two scans in seconds.</li>
 * </ul>
 */
public class BiehmanSimilarity extends CallableTask {

    private ProfileContainer profileContainer;
    private SpectrumContainer spectrumContainer;

    private int binNumber;
    private double scanDistance;

    private static final int N_MAX = 3;

    /**
     * Constructs a Biemann similarity task.
     *
     * @param params the parameter map holding all required task parameters
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the task fails
     */
    public BiehmanSimilarity(ParameterMap params) throws MassCascadeException {

        super(BiehmanSimilarity.class);
        setParameters(params);
    }

    /**
     * Sets the task class variables using the parameter map.
     *
     * @param params the parameter map containing the <code> Parameter </code> to <code> Object </code> relations.
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the parameter map does not contain all variables required by this class
     */
    @Override
    public void setParameters(ParameterMap params) throws MassCascadeException {

        profileContainer = params.get(Parameter.PROFILE_CONTAINER, ProfileContainer.class);
        binNumber = params.get(Parameter.BINS, Integer.class);
        scanDistance = params.get(Parameter.TIME_WINDOW, Double.class);
    }

    /**
     * Executes the task. The <code> Callable </code> returns a {@link uk.ac.ebi.masscascade.interfaces.container
     * .RawContainer} with the processed data.
     *
     * @return the spectrum container with the processed data
     */
    @Override
    public SpectrumContainer call() {

        String id = profileContainer.getId() + IDENTIFIER;
        spectrumContainer = profileContainer.getBuilder().newInstance(SpectrumContainer.class, id,
                profileContainer.getWorkingDirectory());

        SortedSet<Double> rtSet = profileContainer.getTimes().keySet();
        Range rtRange = new ExtendableRange(rtSet.first(), rtSet.last());

        double binWidth = scanDistance / binNumber;
        int noOfBins = (int) (rtRange.getSize() / binWidth) + 1;
        Bin[] bins = new Bin[noOfBins];

        Set<Integer> allProfileIds = new HashSet<>();

        for (Profile profile : profileContainer) {
            allProfileIds.add(profile.getId());
            XYZList profileData = profile.getData();

            int traceMax = 0;
            XYZPoint pDp = profileData.get(traceMax);
            for (XYZPoint dp : profileData) {
                if (dp.x > profile.getRetentionTime()) {
                    if (dp.x - profile.getRetentionTime() > profile.getRetentionTime() - pDp.x) traceMax--;
                    break;
                }
                pDp = dp;
                traceMax++;
            }

            int reachMax = 1;
            double sharpnessMaxL = 0;
            for (int leftI = traceMax - 1; leftI > 0; leftI++) {

                sharpnessMaxL = Math.max(sharpnessMaxL,
                        profileData.get(traceMax).z - profileData.get(leftI).z) / (reachMax * Math.sqrt(
                        profileData.get(traceMax).z));

                if (reachMax == N_MAX) break;
                reachMax++;
            }

            reachMax = 1;
            double sharpnessMaxR = 0;
            for (int rightI = traceMax + 1; rightI < profileData.size(); rightI++) {

                sharpnessMaxR = Math.max(sharpnessMaxR,
                        profileData.get(traceMax).z - profileData.get(rightI).z) / (reachMax * Math.sqrt(
                        profileData.get(traceMax).z));

                if (reachMax == N_MAX) break;
                reachMax++;
            }

            int binIndex = (int) ((profile.getRetentionTime() - rtRange.getLowerBounds()) / binWidth);

            if (bins[binIndex] == null) bins[binIndex] = new Bin((sharpnessMaxL + sharpnessMaxR) / 2d, profile.getId());
            else bins[binIndex].add((sharpnessMaxL + sharpnessMaxR) / 2d, profile.getId());
        }

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
                Set<Profile> profileSet = new HashSet<>();

                Range specRange = new ExtendableRange();
                XYList spectrumData = new XYList();
                double rt = 0;

                List<Integer> tmpIds = new ArrayList<>();
                tmpIds.addAll(bins[i].getProfileIds());
                if (bins[i - 1] != null) tmpIds.addAll(bins[i - 1].getProfileIds());
                if (bins[i + 1] != null) tmpIds.addAll(bins[i + 1].getProfileIds());

                Profile profile;
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

        Profile profile;
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


