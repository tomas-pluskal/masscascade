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
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.utilities.DataUtils;
import uk.ac.ebi.masscascade.utilities.TextUtils;
import uk.ac.ebi.masscascade.utilities.range.ToleranceRange;
import uk.ac.ebi.masscascade.utilities.xyz.XYTrace;
import uk.ac.ebi.masscascade.utilities.xyz.XYZPoint;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Helper class for the Obiwarp task. The class provides methods to generate mz and time bins from the list of profiles
 * to be aligned and convert <link> ProfileContainer </link> into lmata files.
 */
public class ObiwarpHelper {

    private static Logger LOGGER = Logger.getLogger(ObiwarpHelper.class);

    private double mzBinSize;
    private Range mzRange;
    private double timeBinSize;
    private Range timeRange;
    private int nTimeBins;

    /**
     * Constructs an Obiwarp helper.
     */
    public ObiwarpHelper(double mzBinSize, Range mzRange, double timeBinSize, Range timeRange) {

        this.mzBinSize = mzBinSize;
        this.mzRange = mzRange;
        this.timeBinSize = timeBinSize;
        this.timeRange = timeRange;

        nTimeBins = (int) FastMath.ceil((timeRange.getUpperBounds() - timeRange.getLowerBounds()) / timeBinSize);
    }

    /**
     * Builds a lmata file from the profile container.
     *
     * @param container the profile container
     * @return the lmata file
     */
    public File buildLmataFile(ProfileContainer container) {

        int nMzBins = (int) FastMath.ceil((mzRange.getUpperBounds() - mzRange.getLowerBounds()) / mzBinSize);

        double zMax = 0;
        double[][] lmataArray = new double[nTimeBins][nMzBins];
        for (Profile profile : container) {
            int mzBin = (int) FastMath.floor((profile.getMz() - mzRange.getLowerBounds()) / mzBinSize);
            for (XYZPoint dp : profile.getData()) {
                int timeBin = (int) FastMath.floor((dp.x - timeRange.getLowerBounds()) / timeBinSize);
                if (timeBin < 0) timeBin = 0;
                if (timeBin >= nTimeBins) timeBin = nTimeBins - 1;
                lmataArray[timeBin][mzBin] += dp.z;
                if (lmataArray[timeBin][mzBin] > zMax) zMax = lmataArray[timeBin][mzBin];
            }
        }

        String path;
        if (container.getWorkingDirectory().isEmpty()) path = System.getProperty("java.io.tmpdir");
        else path = container.getWorkingDirectory();
        File file = new File(path + File.separator + container.getId() + ".lmata");
        file.deleteOnExit();

        try {

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(nTimeBins + "");
            writer.newLine();
            for (int binIndex = 0; binIndex < nTimeBins; binIndex++)
                writer.write(timeRange.getLowerBounds() + (binIndex * timeBinSize) + " ");
            writer.newLine();

            writer.write(nMzBins + "");
            writer.newLine();
            for (int binIndex = 0; binIndex < nMzBins; binIndex++) writer.write(binIndex * mzBinSize + " ");
            writer.newLine();

            for (int row = 0; row < nTimeBins; row++) {
                for (int column = 0; column < nMzBins; column++) {
                    double intensity = lmataArray[row][column] * Constants.MAX_ABUNDANCE / zMax;
                    writer.write((intensity == 0) ? "0 " : (intensity + " "));
                }
                writer.newLine();
            }

            writer.flush();
            TextUtils.close(writer);
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "Error while writing to tmp file: " + e.getMessage());
        }

        return file;
    }

    public int getMzBin(double mz) {
        return (int) FastMath.floor((mz - mzRange.getLowerBounds()) / mzBinSize);
    }

    public int getTimeBin(double time) {
        return (int) FastMath.floor((time - timeRange.getLowerBounds()) / timeBinSize);
    }

    public double getAccurateTimeBin(double time) {
        return (time - timeRange.getLowerBounds()) / timeBinSize;
    }

    public int getNTimeBins() {
        return nTimeBins;
    }
}
