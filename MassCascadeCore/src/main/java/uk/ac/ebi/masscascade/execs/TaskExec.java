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

package uk.ac.ebi.masscascade.execs;

import uk.ac.ebi.masscascade.background.NoiseReduction;
import uk.ac.ebi.masscascade.io.PsiMzmlReader;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.tracebuilder.ProfileBuilder;

import java.io.File;

/**
 * Runs MassCascade via its API.
 */
public class TaskExec {

    public static void main(String[] args) {

        /**
         * Task runner meta information.
         */
        File inDir = new File("C:/Users/stephan/Desktop/MassCascade/In");
        File outDir = new File("C:/Users/stephan/Desktop/MassCascade/Out");
        File tmpDir = new File("C:/Users/stephan/Desktop/MassCascade/Tmp");

        int nThreads = 3;

        /**
         * Creates a task runner that stores temporary files on disk.
         */
//        TaskRunner taskRunner = new TaskRunner(inDir, outDir, tmpDir, nThreads);

        /**
         * Creates a task runner that stores temporary files in memory.
         */
        TaskRunner taskRunner = new TaskRunner(inDir, outDir, nThreads);

        /**
         * Reads mzML files using the class "PsiMzmlReader".
         *
         * The method doesn't require any input parameters if the task runner is used.
         */
        ParameterMap params = new ParameterMap();
        taskRunner.add(PsiMzmlReader.class, params);

        /**
         * Reduces random noise using the class "NoiseReduction".
         */
        params = new ParameterMap();
        params.put(Parameter.SCAN_WINDOW, 5);
        params.put(Parameter.MZ_WINDOW_PPM, 10d);
        taskRunner.add(NoiseReduction.class, params);

        /**
         * Extracts mass traces and builds profiles using the class "ProfileBuilder".
         */
        params = new ParameterMap();
        params.put(Parameter.MZ_WINDOW_PPM, 10d);
        params.put(Parameter.MIN_PROFILE_INTENSITY, 1000d);
        params.put(Parameter.MIN_PROFILE_WIDTH, 10);
        taskRunner.add(ProfileBuilder.class, params);

        /**
         * Run all tasks.
         */
        taskRunner.run();
    }
}