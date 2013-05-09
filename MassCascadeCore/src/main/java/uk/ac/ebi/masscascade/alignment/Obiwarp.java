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
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

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

    private double timeWindow;
    private double gapInit;
    private double gapExt;
    private double response;
    private String executable;

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
        timeWindow = params.get(Parameter.TIME_WINDOW, Double.class);

        TreeMap<Double, Integer> mzBins = params.get(Parameter.MZ_BINS, TreeMap.class);
        obiwarpHelper = new ObiwarpHelper(mzBins);
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

        File profFile = obiwarpHelper.buildLmataFile(profileContainer, timeWindow);
        TreeSet<Float> times = (TreeSet<Float>) obiwarpHelper.getTimes();
        double[] corrections = align(profFile, times);

        for (Profile profile : profileContainer) {
            Iterator<XYZPoint> dpIter = profile.getData().iterator();
            XYZPoint dp = dpIter.next();
            int timeBin = (int) FastMath.floor(((float) dp.x - times.first()) / timeWindow);
            Profile alignedProfile = profile.copy(dp.x + corrections[timeBin]);
            while (dpIter.hasNext()) {
                dp = dpIter.next();
                timeBin = (int) FastMath.floor(((float) dp.x - times.first()) / timeWindow);
                alignedProfile.addProfilePoint(new XYZPoint(dp.x + corrections[timeBin], dp.y, dp.z));
            }
            alignedProfile.closeProfile();
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
    private double[] align(File file, TreeSet<Float> times) {

        BufferedInputStream bufStream = null;
        int nTimeBins = (int) FastMath.ceil((times.last() - times.first()) / timeWindow);

        double[] corrections = new double[nTimeBins];

        try {
            List<String> commands = new ArrayList<>();
            commands.add(executable);
            commands.add("-r " + response);
            commands.add("-g " + gapInit + "," + gapExt);
            commands.add(referenceFile.getAbsolutePath());
            commands.add(file.getAbsolutePath());
            ProcessBuilder pb = new ProcessBuilder(commands);
            Process process = pb.start();

            InputStream inputStream = process.getInputStream();
            bufStream = new BufferedInputStream(inputStream);

            Float number;
            int index = 0;
            Iterator<Integer> popRowsIter = obiwarpHelper.getPopRows().iterator();
            TextUtils tx = new TextUtils();
            while ((number = tx.readNumberFromStream(bufStream)) != null) {
                corrections[index] = number - popRowsIter.next() * timeWindow;
                index++;
            }
        } catch (IOException exception) {
            LOGGER.log(Level.ERROR, "Obiwarp process error: " + exception.getMessage());
        } finally {
            TextUtils.close(bufStream);
            file.delete();
        }

        return corrections;
    }
}
