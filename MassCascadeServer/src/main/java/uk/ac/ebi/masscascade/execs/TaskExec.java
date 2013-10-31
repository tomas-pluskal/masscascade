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

package uk.ac.ebi.masscascade.execs;

import org.apache.commons.cli.Option;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import uk.ac.ebi.masscascade.alignment.Obiwarp;
import uk.ac.ebi.masscascade.background.DurbinWatsonFilter;
import uk.ac.ebi.masscascade.background.NoiseReduction;
import uk.ac.ebi.masscascade.deconvolution.BiehmanDeconvolution;
import uk.ac.ebi.masscascade.distance.BiehmanSimilarity;
import uk.ac.ebi.masscascade.featurebuilder.SequentialFeatureBuilder;
import uk.ac.ebi.masscascade.filter.FeatureFilter;
import uk.ac.ebi.masscascade.filter.FeatureSetFilter;
import uk.ac.ebi.masscascade.filter.IonFilter;
import uk.ac.ebi.masscascade.identification.IsotopeFinder;
import uk.ac.ebi.masscascade.io.PsiMzmlReader;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.server.TaskRunner;
import uk.ac.ebi.masscascade.smoothing.SavitzkyGolaySmoothing;
import uk.ac.ebi.masscascade.utilities.TextUtils;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;
import uk.ac.ebi.masscascade.utilities.range.SimpleRange;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.TreeSet;

/**
 * Runs MassCascade via its API.
 */
public class TaskExec extends CommandLineMain {

    private static final Logger LOGGER = Logger.getLogger(TaskExec.class);

    public static void main(String[] args) {
        new TaskExec(args).process();
    }

    public TaskExec(String[] args) {
        super(args);
    }

    @Override
    public void setupOptions() {

        add(new Option("i", "input directory", true, "input directory containing mzML or RAW files"));
        add(new Option("o", "output directory", true, "output directory for results"));
        add(new Option("t", "temporary directory", true, "temporary directory for intermediate results"));
        add(new Option("n", "thread number", true, "maximum number of threads to be used"));
    }

    @Override
    public void process() {

        if (!hasOption("i") || !hasOption("o") || !hasOption("n")) {
            printHelp();
            System.exit(0);
        }

        File iDir = new File(getCommandLine().getOptionValue("i"));
        File oDir = new File(getCommandLine().getOptionValue("o"));

        int threads = Integer.parseInt(getCommandLine().getOptionValue("n"));

        TaskRunner runner;
        if (getCommandLine().getOptionValue("t") == null) {
            LOGGER.log(Level.INFO, "Running MassCascade in memory.");
            runner = new TaskRunner(iDir, oDir, threads);
        } else {
            File tDir = new File(getCommandLine().getOptionValue("t"));
            LOGGER.log(Level.INFO, "Running MassCascade with serialization.");
            runner = new TaskRunner(iDir, oDir, tDir, threads);
        }

        setup(runner);

        long start = System.currentTimeMillis();

        runner.run();

        ParameterMap outputParams = new ParameterMap();
        outputParams.put(Parameter.MZ_WINDOW_PPM, 10);
        outputParams.put(Parameter.MISSINGNESS, 10);
        outputParams.put(Parameter.TIME_WINDOW, 10);
        outputParams.put(Parameter.DEFAULT, 100000);

        runner.write(outputParams);

        long stop = System.currentTimeMillis();
        int time = (int) Math.round((stop - start) / 1000d);
        LOGGER.log(Level.INFO, "Finished process in " + time + " s using " + threads + " threads.");
    }

    private void setup(TaskRunner runner) {

        ParameterMap params = new ParameterMap();
        runner.add(PsiMzmlReader.class, params);

        params = new ParameterMap();
        params.put(Parameter.SCAN_WINDOW, 5);
        params.put(Parameter.MZ_WINDOW_PPM, 10);
        runner.add(NoiseReduction.class, params);

        params = new ParameterMap();
        params.put(Parameter.MZ_WINDOW_PPM, 10);
        params.put(Parameter.MIN_FEATURE_INTENSITY, 10000);
        params.put(Parameter.MIN_FEATURE_WIDTH, 6);
        runner.add(SequentialFeatureBuilder.class, params);

        params = new ParameterMap();
        params.put(Parameter.DURBIN, 2.38);
        runner.add(DurbinWatsonFilter.class, params);

        params = new ParameterMap();
        params.put(Parameter.POLYNOMIAL_ORDER, 3);
        params.put(Parameter.DATA_WINDOW, 4);
        runner.add(SavitzkyGolaySmoothing.class, params);

        params = new ParameterMap();
        params.put(Parameter.SCAN_WINDOW, 10);
        params.put(Parameter.NOISE_FACTOR, 1);
        params.put(Parameter.CENTER, false);
        runner.add(BiehmanDeconvolution.class, params);

        params = new ParameterMap();
        params.put(Parameter.TIME_RANGE, new SimpleRange(0, 1000));
        params.put(Parameter.MZ_RANGE, new SimpleRange(0, 1000));
        params.put(Parameter.FEATURE_RANGE, new SimpleRange(0, 100));
        params.put(Parameter.MIN_FEATURE_INTENSITY, 10000);
        params.put(Parameter.KEEP_ISOTOPES, true);
        runner.add(FeatureFilter.class, params);

        params = new ParameterMap();
        params.put(Parameter.GAP_INIT, 0.3);
        params.put(Parameter.GAP_EXTEND, 0.5);
        params.put(Parameter.RESPONSE, 90);
        params.put(Parameter.BIN_WIDTH_MZ, 0.25);
        params.put(Parameter.BIN_WIDTH_RT, 1.5);
        params.put(Parameter.MZ_RANGE, new ExtendableRange(60, 900));
        params.put(Parameter.TIME_RANGE, new ExtendableRange(0, 900));
        params.put(Parameter.REFERENCE_FILE, new File("./20100921_Tomato_Standard_61-0904.lmata"));
        params.put(Parameter.EXECUTABLE, "./obiwarp-x86_64-linux");
        runner.add(Obiwarp.class, params);

        params = new ParameterMap();
        params.put(Parameter.MZ_WINDOW_PPM, 20);
        params.put(Parameter.MZ_FOR_REMOVAL, getInterferents());
        runner.add(IonFilter.class, params);

        params = new ParameterMap();
        params.put(Parameter.TIME_WINDOW, 10);
        params.put(Parameter.BINS, 15);
        runner.add(BiehmanSimilarity.class, params);

        params = new ParameterMap();
        params.put(Parameter.MZ_WINDOW_PPM, 20);
        runner.add(IsotopeFinder.class, params);

        params = new ParameterMap();
        params.put(Parameter.TIME_RANGE, new SimpleRange(80, 750));
        params.put(Parameter.MZ_RANGE, new SimpleRange(0, 1000));
        params.put(Parameter.MIN_FEATURE_INTENSITY, 100000);
        params.put(Parameter.KEEP_ISOTOPES, true);
        runner.add(FeatureSetFilter.class, params);
    }

    private TreeSet<Double> getInterferents() {

        TreeSet<Double> interferents = new TreeSet<>();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("./interferents.csv"));
            String line;
            while ((line = reader.readLine()) != null) {
                interferents.add(Double.parseDouble(line.trim()));
            }
        } catch (Exception exception) {
            LOGGER.log(Level.WARN, "Failed reading interferents file.", exception);
        } finally {
            TextUtils.close(reader);
        }

        return interferents;
    }
}