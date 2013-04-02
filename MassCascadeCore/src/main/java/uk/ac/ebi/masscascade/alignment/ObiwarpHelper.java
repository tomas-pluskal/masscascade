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

package uk.ac.ebi.masscascade.alignment;

import org.apache.commons.math3.util.FastMath;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.interfaces.Trace;
import uk.ac.ebi.masscascade.interfaces.container.ProfileContainer;
import uk.ac.ebi.masscascade.utilities.DataUtils;
import uk.ac.ebi.masscascade.utilities.TextUtils;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;
import uk.ac.ebi.masscascade.utilities.range.ToleranceRange;
import uk.ac.ebi.masscascade.utilities.xyz.XYTrace;
import uk.ac.ebi.masscascade.utilities.xyz.XYZPoint;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
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

    private Set<Integer> popRows;
    private TreeSet<Float> times;
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
     * @param mzBins a map of mz bins: m/z bins to bin indices
     */
    public ObiwarpHelper(TreeMap<Double, Integer> mzBins) {
        this.mzBins = mzBins;
    }

    /**
     * Returns the map of mz bins
     *
     * @return the map of mz bins: m/z bins to bin indices
     */
    public TreeMap<Double, Integer> getMzBins() {
        return mzBins;
    }

    /**
     * Takes a list of profile containers and returns a map of m/z bins to bin indices.
     *
     * @param containerList a list of profile containers to be aligned
     * @param ppm           a m/z tolerance value in ppm
     * @return the map of m/z bins to bin indices
     */
    private TreeMap<Double, Integer> instantiate(List<ProfileContainer> containerList, double ppm) {

        TreeSet<Trace> mzs = new TreeSet<Trace>();
        for (ProfileContainer container : containerList) {
            for (Profile profile : container) {
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
     * @param container     the profile container
     * @param timeWindow the time tolerance in seconds
     * @return the lmata file
     */
    public File buildLmataFile(ProfileContainer container, double timeWindow) {

        times = new TreeSet<Float>();
        for (Profile profile : container) {
            for (XYZPoint dp : profile.getData()) {
                if (!times.contains((float) dp.x)) times.add((float) dp.x);
            }
        }

        // calculate the number of bins
        int nTimeBins = (int) FastMath.ceil((times.last() - times.first()) / timeWindow);

        popRows = new LinkedHashSet<Integer>();
        double[][] lmataArray = new double[nTimeBins][mzBins.size()];
        for (Profile profile : container) {
            Double closestMz = DataUtils.getClosestKey(profile.getMz(), mzBins);
            for (XYZPoint dp : profile.getData()) {
                int timeBin = (int) FastMath.floor(((float) dp.x - times.first()) / timeWindow);
                lmataArray[timeBin][mzBins.get(closestMz)] = dp.z;
                if (dp.z > 0) popRows.add(timeBin);
            }
        }

        String path;
        if (container.getWorkingDirectory().isEmpty()) path = System.getProperty("java.io.tmpdir");
        else path = container.getWorkingDirectory();
        File file = new File(path + File.separator + container.getId() + ".lmata");

        try {

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(popRows.size() + "");
            writer.newLine();
            for (int binIndex = 0; binIndex < nTimeBins; binIndex++) {
                if (popRows.contains(binIndex)) writer.write(times.first() + (binIndex * timeWindow) + " ");
            }
            writer.newLine();
            writer.write(mzBins.size() + "");
            writer.newLine();
            for (double mz : mzBins.keySet()) writer.write(mz + " ");

            writer.newLine();
            int row = 0;
            for (int binIndex = 0; binIndex < nTimeBins; binIndex++) {
                if (popRows.contains(binIndex)) {
                    for (int column = 0; column < lmataArray[row].length; column++) {
                        double intensity = lmataArray[row][column];
                        writer.write((intensity == 0) ? "0 " : (intensity + " "));
                    }
                    writer.newLine();
                }
                row++;
            }

            writer.flush();
            TextUtils.close(writer);
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
    public Set<Float> getTimes() {
        return times;
    }

    public Set<Integer> getPopRows() {
        return popRows;
    }
}
