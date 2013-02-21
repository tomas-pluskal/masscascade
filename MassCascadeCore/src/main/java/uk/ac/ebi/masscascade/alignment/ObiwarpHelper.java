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

package uk.ac.ebi.masscascade.alignment;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.Trace;
import uk.ac.ebi.masscascade.interfaces.container.ProfileContainer;
import uk.ac.ebi.masscascade.utilities.DataUtils;
import uk.ac.ebi.masscascade.utilities.range.ToleranceRange;
import uk.ac.ebi.masscascade.utilities.xyz.XYTrace;
import uk.ac.ebi.masscascade.utilities.xyz.XYZPoint;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Helper class for the Obiwarp task. The class provides methods to generate mz bins from the list of profiles to be
 * aligned and convert <link> ProfileContainer </link> into lmata files.
 */
public class ObiwarpHelper {

    private static Logger LOGGER = Logger.getLogger(ObiwarpHelper.class);

    private Set<Double> times;
    private TreeMap<Double, Integer> mzBins;

    /**
     * Constructs an Obiwarp helper and builds the list of mz bins based on the ppm tolerance.
     *
     * @param containerList a list of profiles to be aligned
     * @param ppm           a tolerance in ppm
     */
    public ObiwarpHelper(List<ProfileContainer> containerList, double ppm) {
        this.mzBins = instantiate(containerList, ppm);
    }

    /**
     * Constructs an Obiwarp helper with a provided map of mz bins.
     *
     * @param mzBins a map of mz bins: old times to bin indices
     */
    public ObiwarpHelper(TreeMap<Double, Integer> mzBins) {
        this.mzBins = mzBins;
    }

    /**
     * Returns the map of mz bins
     *
     * @return the map of mz bins: old times to bin indices
     */
    public TreeMap<Double, Integer> getMzBins() {
        return mzBins;
    }

    /**
     * Takes a list of profile containers and returns a map of bin times to bin indices.
     *
     * @param containerList a list of profile containers to be aligned
     * @param ppm           a m/z tolerance value in ppm
     * @return the map of bin times to bin indices
     */
    private TreeMap<Double, Integer> instantiate(List<ProfileContainer> containerList, double ppm) {

        TreeSet<Trace> mzs = new TreeSet<Trace>();
        for (ProfileContainer container : containerList) {
            int index = -1;
            TreeMap<Double, Integer> times = new TreeMap<Double, Integer>();
            for (Profile profile : container) {
                if (!times.containsKey(profile.getRetentionTime())) times.put(profile.getRetentionTime(), ++index);
                Trace trace = new XYTrace(profile.getMzIntDp());
                XYTrace closestTrace = (XYTrace) DataUtils.getClosestValue(trace, mzs);
                if (closestTrace == null) mzs.add(trace);
                else if (new ToleranceRange(closestTrace.getAvg(), ppm).contains(profile.getMz())) {
                    closestTrace.add(profile.getMzIntDp());
                    mzs.add(closestTrace);
                } else mzs.add(trace);
            }
        }

        int index = 0;
        TreeMap<Double, Integer> mzBins = new TreeMap<Double, Integer>();
        for (Trace trace : mzs) mzBins.put(trace.getAvg(), index++);

        return mzBins;
    }

    /**
     * Builds a lmata file from the profile container.
     *
     * @param container a profile container
     * @return the lmata file
     */
    public File buildLmataFile(ProfileContainer container) {

        int index = 0;
        TreeMap<Double, Integer> timeToIndex = new TreeMap<Double, Integer>();
        for (Profile profile : container) {
            for (XYZPoint dp : profile.getData()) {
                if (!timeToIndex.containsKey(dp.x)) timeToIndex.put(dp.x, index++);
            }
        }
        times = timeToIndex.keySet();

        double[][] lmataArray = new double[times.size()][mzBins.size()];
        for (Profile profile : container) {
            Double closestMz = DataUtils.getClosestKey(profile.getMz(), mzBins);
            for (XYZPoint dp : profile.getData()) {
                lmataArray[timeToIndex.get(dp.x)][mzBins.get(closestMz)] = dp.z;
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append(times.size());
        sb.append("\n");
        for (double time : times) {
            sb.append(time);
            sb.append(" ");
        }
        sb.append("\n");
        sb.append(mzBins.size());
        sb.append("\n");
        for (double mz : mzBins.keySet()) {
            sb.append(mz);
            sb.append(" ");
        }
        sb.append("\n");
        for (int row = 0; row < times.size(); row++) {
            for (int column = 0; column < lmataArray[row].length; column++) {
                sb.append(lmataArray[row][column]);
                sb.append(" ");
            }
            sb.append("\n");
        }

        String path;
        if (container.getWorkingDirectory().isEmpty()) path = System.getProperty("java.io.tmpdir");
        else path = container.getWorkingDirectory();

        File file = new File(path + File.separator + container.getId() + ".lmata");
        try {
            FileUtils.writeStringToFile(file, sb.toString());
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "Error while writing to tmp file: " + e.getMessage());
        }

        return file;
    }

    /**
     * Returns a comprehensive set of times established by the lmata file creation method.
     *
     * @return the list of times
     */
    public Set<Double> getTimes() {
        return times;
    }
}
