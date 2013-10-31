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

package uk.ac.ebi.masscascade.server;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import uk.ac.ebi.masscascade.alignment.FeatureBinTableModel;
import uk.ac.ebi.masscascade.alignment.featurebins.FeatureBin;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.container.Container;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.TextUtils;
import uk.ac.ebi.masscascade.utilities.comparator.FeatureBinTimeComparator;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * The task runner simplifies running MassCascade on command line. It takes a set of task classes and parameter maps
 * and executes them in order. The first task must be a reader task.
 */
public class TaskRunner {

    private static Logger LOGGER = Logger.getLogger(TaskRunner.class);

    private final LinkedHashMap<Class<? extends CallableTask>, ParameterMap> tasks;

    private File inDirectory;
    private File outDirectory;
    private File tmpDirectory;
    private int nThreads;

    private Multimap<Integer, Container> results;

    /**
     * Constructs a task runner that keeps all files in memory.
     *
     * @param inDirectory  the input directory containing the mass spectrometry files
     * @param outDirectory the output directory for the result
     */
    public TaskRunner(File inDirectory, File outDirectory) {
        this(inDirectory, outDirectory, null);
    }

    /**
     * Constructs a task runner that keeps all files in memory.
     *
     * @param inDirectory  the input directory containing the mass spectrometry files
     * @param outDirectory the output directory for the result
     * @param nThreads     the number of threads
     */
    public TaskRunner(File inDirectory, File outDirectory, int nThreads) {
        this(inDirectory, outDirectory, null, nThreads);
    }

    /**
     * Constructs a task runner that uses a temporary directory for files and sets the number of threads to the number
     * of available processors.
     *
     * @param inDirectory  the input directory containing the mass spectrometry files
     * @param outDirectory the output directory for the result
     * @param tmpDirectory the working directory for temporary files
     */
    public TaskRunner(File inDirectory, File outDirectory, File tmpDirectory) {
        this(inDirectory, outDirectory, tmpDirectory, Runtime.getRuntime().availableProcessors());
    }

    /**
     * Constructs a task runner that uses a temporary directory for files and sets the number of threads.
     *
     * @param inDirectory  the input directory containing the mass spectrometry files
     * @param outDirectory the output directory for the result
     * @param tmpDirectory the working directory for temporary files
     * @param nThreads     the number of threads
     */
    public TaskRunner(File inDirectory, File outDirectory, File tmpDirectory, int nThreads) {

        if (!outDirectory.exists() || !inDirectory.exists())
            throw new MassCascadeException("Directory does not exist.");
        else if (!outDirectory.isDirectory() || !inDirectory.isDirectory())
            throw new MassCascadeException("File is not a directory.");

        if (tmpDirectory != null && (!tmpDirectory.exists() || !tmpDirectory.isDirectory()))
            throw new MassCascadeException("Tmp file is not a directory.");

        this.inDirectory = inDirectory;
        this.outDirectory = outDirectory;
        this.tmpDirectory = tmpDirectory;
        this.nThreads = nThreads;

        results = HashMultimap.create();
        tasks = new LinkedHashMap<>();
    }

    /**
     * Adds a new task class to the set. The parameter map defines the task class.
     *
     * @param taskClass    a task class
     * @param parameterMap a parameter map
     */
    public void add(Class<? extends CallableTask> taskClass, ParameterMap parameterMap) {
        tasks.put(taskClass, parameterMap);
    }

    /**
     * Executes all tasks in order and writes the results in the output directory.
     */
    public Multimap<Integer, Container> run() {

        ExecutorService threadPool = Executors.newFixedThreadPool(nThreads);
        List<Future<Container>> futures = new ArrayList<>();

        File[] files = inDirectory.listFiles(new Filter(Constants.FILE_FORMATS.MZML));
        Arrays.sort(files);

        try {

            for (File file : files) {
                TaskChain executionChain = new TaskChain(file, new LinkedHashMap<>(tasks), tmpDirectory);
                futures.add(threadPool.submit(executionChain));
            }

            threadPool.shutdown();

            for (Future<Container> future : futures) {
                results.put(1, future.get());
            }

        } catch (Exception exception) {
            LOGGER.log(Level.ERROR, "File could not be read: " + exception.getMessage());
        } finally {
            threadPool.shutdownNow();
        }

        return results;
    }

    public void write(ParameterMap outputParams) {

        FileWriter writer = null;

        try {
            double ppm = outputParams.get(Parameter.MZ_WINDOW_PPM, Double.class);
            double sec = outputParams.get(Parameter.TIME_WINDOW, Double.class);
            double missing = outputParams.get(Parameter.MISSINGNESS, Double.class);
            double defaultValue = outputParams.get(Parameter.DEFAULT, Double.class);

            FeatureBinTableModel model = new FeatureBinTableModel(results, ppm, sec, missing);

            List<FeatureBin> rows = model.getRows();
            Collections.sort(rows, new FeatureBinTimeComparator());

            writer = new FileWriter(outDirectory + File.separator + "masscascade_taskRunner.csv");

            for (FeatureBin row : rows) {

                writer.write(row.getMz() + ",");
                writer.write(row.getRt() + ",");
                writer.write(row.getArea() + ",");
                writer.write(row.getLabel() + ",");
                writer.write(row.getMzDev() + ",");

                for (int i = 6; i < model.getColumnCount(); i++) {
                    double intensity = row.isPresent(i - FeatureBin.COLUMNS);
                    if (intensity > 0) {
                        writer.write(intensity + ",");
                    } else {
                        writer.write(defaultValue + ",");
                    }
                }
                writer.write("\n");
            }
        } catch (Exception exception) {
            LOGGER.log(Level.ERROR, "Output could not be written.", exception);
        } finally {
            TextUtils.close(writer);
        }
    }

    class Filter implements FilenameFilter {

        private Constants.FILE_FORMATS format;

        public Filter(Constants.FILE_FORMATS format) {
            this.format = format;
        }

        @Override
        public boolean accept(final File file, final String name) {
            return name.toUpperCase().endsWith(format.name());
        }
    }
}
