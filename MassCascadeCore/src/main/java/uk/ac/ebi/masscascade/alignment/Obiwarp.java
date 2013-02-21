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

import org.apache.log4j.Level;
import uk.ac.ebi.masscascade.core.profile.ProfileImpl;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.container.Container;
import uk.ac.ebi.masscascade.interfaces.container.ProfileContainer;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.TextUtils;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;
import uk.ac.ebi.masscascade.utilities.xyz.XYZPoint;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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

    /**
     * Constructs an Obiwarp task.
     *
     * @param params a parameter map
     */
    public Obiwarp(ParameterMap params) {

        super(Obiwarp.class);
        setParameters(params);
    }

    /**
     * Sets the parameters for the Obiwarp task.
     *
     * @param params the parameter values
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    private void setParameters(ParameterMap params) {

        referenceFile = params.get(Parameter.REFERENCE_FILE, File.class);
        profileContainer = params.get(Parameter.PROFILE_CONTAINER, ProfileContainer.class);

        gapInit = params.get(Parameter.GAP_INIT, Double.class);
        gapExt = params.get(Parameter.GAP_EXTEND, Double.class);
        response = params.get(Parameter.RESPONSE, Double.class);
        executable = params.get(Parameter.EXECUTABLE, String.class);

        TreeMap<Double, Integer> mzBins = params.get(Parameter.MZ_BINS, TreeMap.class);
        obiwarpHelper = new ObiwarpHelper(mzBins);
    }

    /**
     * Executes the task and processes the data.
     *
     * @return the processed profiles
     */
    @Override
    public Container call() {

        String id = profileContainer.getId() + IDENTIFIER;
        ProfileContainer outProfileContainer = profileContainer.getBuilder().newInstance(ProfileContainer.class, id,
                profileContainer.getWorkingDirectory());

        File profFile = obiwarpHelper.buildLmataFile(profileContainer);
        Map<Double, Double> times = execute(profFile, obiwarpHelper.getTimes());

        for (Profile profile : profileContainer) {
            Iterator<XYZPoint> dpIter = profile.getData().iterator();
            XYZPoint dp = dpIter.next();
            Profile alignedProfile = new ProfileImpl(profile.getId(), new XYZPoint(times.get(dp.x), dp.y, dp.z),
                    new ExtendableRange(dp.y, dp.y));
            while (dpIter.hasNext()) {
                dp = dpIter.next();
                alignedProfile.addProfilePoint(new XYZPoint(times.get(dp.x), dp.y, dp.z));
            }
            alignedProfile.closeProfile();
            outProfileContainer.addProfile(alignedProfile);
        }

        profFile.delete();
        outProfileContainer.finaliseFile();
        return outProfileContainer;
    }

    /**
     * Executes the Obiwarp binary using the parameters defined by the parameter map.
     *
     * @param file     the Obiwarp binary
     * @param oldTimes the time data points of the profiles to be aligned
     * @return the old to aligned times data map
     */
    private Map<Double, Double> execute(File file, Set<Double> oldTimes) {

        Map<Double, Double> times = new LinkedHashMap<Double, Double>();
        BufferedInputStream bufStream = null;

        try {
            List<String> commands = new ArrayList<String>();
            commands.add(executable);
            commands.add("-r " + response);
            commands.add("-g " + gapInit + "," + gapExt);
            commands.add(referenceFile.getAbsolutePath());
            commands.add(file.getAbsolutePath());
            ProcessBuilder pb = new ProcessBuilder(commands);
            Process process = pb.start();

            InputStream inputStream = process.getInputStream();
            bufStream = new BufferedInputStream(inputStream);

            Double number;
            Iterator<Double> oldTimesIter = oldTimes.iterator();
            TextUtils tx = new TextUtils();
            while ((number = tx.readNumberFromStream(bufStream)) != null) {
                times.put(oldTimesIter.next(), number);
            }
        } catch (IOException exception) {
            LOGGER.log(Level.ERROR, "Obiwarp process error: " + exception.getMessage());
        } finally {
            TextUtils.close(bufStream);
            file.delete();
        }

        return times;
    }
}
