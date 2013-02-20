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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class ObiwarpHelper {

    private static Logger LOGGER = Logger.getLogger(ObiwarpHelper.class);

    private TreeMap<Double, Integer> mzBins;

    public ObiwarpHelper(List<ProfileContainer> containerList, double ppm) {
        this.mzBins = instantiate(containerList, ppm);
    }

    public ObiwarpHelper(TreeMap<Double, Integer> mzBins) {
        this.mzBins = mzBins;
    }

    public TreeMap<Double, Integer> getMzBins() {
        return mzBins;
    }

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

    public File buildLmataFile(ProfileContainer container) {

        int index = 0;
        TreeMap<Double, Integer> timeToIndex = new TreeMap<Double, Integer>();
        SortedSet<Double> oldTimes = container.getTimes().keySet();
        for (double time : oldTimes) timeToIndex.put(time, index++);

        double[][] lmataArray = new double[container.getTimes().size()][mzBins.size()];
        for (Profile profile : container) {
            Double closestMz = DataUtils.getClosestKey(profile.getMz(), mzBins);
            lmataArray[timeToIndex.get(profile.getRetentionTime())][mzBins.get(closestMz)] = profile.getIntensity();
        }

        StringBuilder sb = new StringBuilder();
        sb.append(oldTimes.size());
        sb.append("\n");
        for (double time : oldTimes) {
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
        for (int row = 0; row < oldTimes.size(); row++) {
            for (int column = 0; column < lmataArray[row].length; column++) {
                sb.append(lmataArray[row][column]);
                sb.append(" ");
            }
            sb.append("\n");
        }

        String path;
        if (container.getWorkingDirectory().isEmpty()) path = System.getProperty("java.io.tmpdir");
        else path = container.getWorkingDirectory();

        File file = new File(path + container.getId() + ".lmata");
        try {
            FileUtils.writeStringToFile(file, sb.toString());
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "Error while writing to tmp file: " + e.getMessage());
        }

        return file;
    }
}
