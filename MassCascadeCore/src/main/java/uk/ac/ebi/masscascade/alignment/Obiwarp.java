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

public class Obiwarp extends CallableTask {

    private File referenceFile;
    private ProfileContainer profileContainer;

    private ObiwarpHelper obiwarpHelper;

    private double gapInit;
    private double gapExt;
    private double response;
    private String executable;

    public Obiwarp(ParameterMap params) {

        super(Obiwarp.class);
        setParameters(params);
    }

    private void setParameters(ParameterMap params) {

        referenceFile = params.get(Parameter.REFERENCE_FILE, File.class);
        profileContainer = params.get(Parameter.PROFILE_CONTAINER, ProfileContainer.class);

        gapInit = params.get(Parameter.GAP_INIT, Double.class);
        gapExt = params.get(Parameter.GAP_INIT, Double.class);
        response = params.get(Parameter.RESPONSE, Double.class);
        executable = params.get(Parameter.EXECUTABLE, String.class);

        TreeMap<Double, Integer> mzBins = params.get(Parameter.MZ_BINS, TreeMap.class);
        obiwarpHelper = new ObiwarpHelper(mzBins);
    }

    /**
     * Executes the task and processes the data.
     *
     * @return the processed mass spectrometry run
     */
    @Override
    public Container call() {

        String id = profileContainer.getId() + IDENTIFIER;
        ProfileContainer outProfileContainer = profileContainer.getBuilder().newInstance(ProfileContainer.class, id,
                profileContainer.getWorkingDirectory());

        File profFile = obiwarpHelper.buildLmataFile(profileContainer);
        Map<Double, Double> times = execute(profFile, profileContainer.getTimes().keySet());

        for (Profile profile : profileContainer) {
            Iterator<XYZPoint> dpIter = profile.getData().iterator();
            XYZPoint dp = dpIter.next();
            Profile alignedProfile = new ProfileImpl(profile.getId(), dp, new ExtendableRange(dp.y, dp.y));
            while (dpIter.hasNext()) alignedProfile.addProfilePoint(dpIter.next());
            outProfileContainer.addProfile(alignedProfile);
        }

        outProfileContainer.finaliseFile();
        return outProfileContainer;
    }

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
