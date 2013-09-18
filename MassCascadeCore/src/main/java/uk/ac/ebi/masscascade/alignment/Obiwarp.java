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
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.interfaces.container.Container;
import uk.ac.ebi.masscascade.interfaces.container.ProfileContainer;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.TextUtils;
import uk.ac.ebi.masscascade.utilities.math.LinearEquation;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;
import uk.ac.ebi.masscascade.utilities.xyz.XYZPoint;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Class for profile aligning using Obiwarp. The class executes the Obiwarp binary with the given parameters and
 * converts its output back into a list of aligned profiles. Input is provided in "lmata" format, which is generated on
 * the fly from the profile container. The reference file, the converted reference profile container, is provided via
 * the parameter map. The <link> ObiwarpHelper </link> class can be used to create the file and generate the mz bins.
 * <p/>
 * <ul>
 * <li>Parameter <code> REFERENCE_FILE </code>- The lmata reference file.</li>
 * <li>Parameter <code> PROFILE CONTAINER </code>- The input profile container.</li>
 * <li>Parameter <code> GAP_INIT </code>- The gap penalty for initiating a gap.</li>
 * <li>Parameter <code> GAP_EXTEND </code>- The gap penaly for extending a gap.</li>
 * <li>Parameter <code> RESPONSE </code>- The responsiveness of warping [0 - 100].</li>
 * <li>Parameter <code> EXECUTABLE </code>- The path to the Obiwarp executable.</li>
 * <li>Parameter <code> BIN_WIDTH_MZ </code>- The width of a m/z bin in amu.</li>
 * <li>Parameter <code> MZ_RANGE </code>- The global m/z range in amu (ll-ul).</li>
 * <li>Parameter <code> BIN_WIDTH_RT </code>- The width of a time bin in seconds.</li>
 * <li>Parameter <code> TIME_RANGE </code>- The global time range in seconds (ll-ul).</li>
 * </ul>
 */
public class Obiwarp extends CallableTask {

    private File referenceFile;
    private ProfileContainer profileContainer;

    private ObiwarpHelper obiwarpHelper;

    private double gapInit;
    private double gapExt;
    private double response;
    private String executable;

    private Map<Integer, Double[]> idToTimeDiff;

    /**
     * Constructs an Obiwarp alignment task.
     *
     * @param params the parameter map holding all required task parameters
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the task fails
     */
    public Obiwarp(ParameterMap params) {

        super(Obiwarp.class);
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
    public void setParameters(ParameterMap params) {

        referenceFile = params.get(Parameter.REFERENCE_FILE, File.class);
        profileContainer = params.get(Parameter.PROFILE_CONTAINER, ProfileContainer.class);

        gapInit = params.get(Parameter.GAP_INIT, Double.class);
        gapExt = params.get(Parameter.GAP_EXTEND, Double.class);
        response = params.get(Parameter.RESPONSE, Double.class);
        executable = params.get(Parameter.EXECUTABLE, String.class);

        double mzBinSize = params.get(Parameter.BIN_WIDTH_MZ, Double.class);
        Range mzRange = params.get(Parameter.MZ_RANGE, ExtendableRange.class);
        double timeBinSize = params.get(Parameter.BIN_WIDTH_RT, Double.class);
        Range timeRange = params.get(Parameter.TIME_RANGE, ExtendableRange.class);

        obiwarpHelper = new ObiwarpHelper(mzBinSize, mzRange, timeBinSize, timeRange);
        idToTimeDiff = new HashMap<>();
    }

    /**
     * Executes the task. The <code> Callable </code> returns a {@link uk.ac.ebi.masscascade.interfaces.container
     * .RawContainer} with the processed data.
     *
     * @return the profile container with the processed data
     */
    @Override
    public Container call() {

        String id = profileContainer.getId() + IDENTIFIER;
        ProfileContainer outProfileContainer = profileContainer.getBuilder().newInstance(ProfileContainer.class, id,
                profileContainer.getWorkingDirectory());

        File profFile = obiwarpHelper.buildLmataFile(profileContainer);
        double[] alignedTimes = align(profFile);

        for (Profile profile : profileContainer) {

            Iterator<XYZPoint> iterator = profile.getData().iterator();
            XYZPoint dp = iterator.next();
            Profile alignedProfile = profile.copy(getInterpolatedTimeValue(dp.x, alignedTimes));

            while (iterator.hasNext()) {
                dp = iterator.next();
                double time = getInterpolatedTimeValue(dp.x, alignedTimes);
                alignedProfile.addProfilePoint(new XYZPoint(time, dp.y, dp.z));
            }
            alignedProfile.closeProfile();
            idToTimeDiff.put(profile.getId(),
                    new Double[]{alignedProfile.getRetentionTime() - profile.getRetentionTime(),
                                 alignedProfile.getRetentionTime()});
            outProfileContainer.addProfile(alignedProfile);
        }

        outProfileContainer.finaliseFile();

        return outProfileContainer;
    }

    /**
     * Executes the Obiwarp binary using the parameters defined by the parameter map.
     *
     * @param file the Obiwarp binary
     * @return the old to aligned times data map
     */
    private double[] align(File file) {

        BufferedInputStream bufStream = null;
        double[] alignedTimes = new double[obiwarpHelper.getNTimeBins()];

        try {
            List<String> commands = new ArrayList<>();
            commands.add(executable);
            commands.add("-r");
            commands.add(response + "");
            commands.add("-g");
            commands.add(gapInit + "," + gapExt);
            commands.add(referenceFile.getAbsolutePath());
            commands.add(file.getAbsolutePath());
            ProcessBuilder pb = new ProcessBuilder(commands);
            Process process = pb.start();

            InputStream inputStream = process.getInputStream();
            bufStream = new BufferedInputStream(inputStream);

            TextUtils tx = new TextUtils();
            Float number;
            int index = 0;
            while ((number = tx.readNumberFromStream(bufStream)) != null) {
                alignedTimes[index++] = number;
            }

            try {
                process.waitFor();
                process.destroy();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException exception) {
            LOGGER.log(Level.ERROR, "Obiwarp process error: " + exception.getMessage());
        } finally {
            TextUtils.close(bufStream);
            file.delete();
        }

        return alignedTimes;
    }

    /**
     * Performs linear interpolation in between the ceiling and flooring bin that corresponds to the time value.
     *
     * @param time         the aligned time value
     * @param alignedTimes the array of aligned times
     * @return the interpolates time value
     */
    private double getInterpolatedTimeValue(double time, double[] alignedTimes) {

        double accTimeBin = obiwarpHelper.getAccurateTimeBin(time);
        double floorTimeBin = FastMath.floor(accTimeBin);
        double ceilTimeBin = FastMath.ceil(accTimeBin);

        LinearEquation lq = new LinearEquation(new XYPoint(floorTimeBin, alignedTimes[(int) floorTimeBin]),
                new XYPoint(ceilTimeBin, alignedTimes[(int) ceilTimeBin]));

        double interpolatedTime = lq.getY(accTimeBin);
        return Double.isNaN(interpolatedTime) ? time : interpolatedTime;
    }

    /**
     * Returns the profile id to time difference map to allow mapping between the raw container and aligned profiles.
     *
     * @return the profile id to time difference map
     */
    public Map<Integer, Double[]> getTimeDiffMap() {
        return idToTimeDiff;
    }
}
