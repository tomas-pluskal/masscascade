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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import uk.ac.ebi.masscascade.core.profile.ProfileContainer;
import uk.ac.ebi.masscascade.core.raw.RawContainer;
import uk.ac.ebi.masscascade.core.spectrum.SpectrumContainer;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.ACallableTask;
import uk.ac.ebi.masscascade.interfaces.Container;
import uk.ac.ebi.masscascade.io.CmlReader;
import uk.ac.ebi.masscascade.io.PsiMzmlReader;
import uk.ac.ebi.masscascade.io.XCaliburReader;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TaskRunner {

    private static Logger LOGGER = Logger.getLogger(TaskRunner.class);

    private LinkedHashMap<Class<? extends ACallableTask>, ParameterMap> tasks;

    private File inDirectory;
    private File outDirectory;
    private File tmpDirectory;
    private int taskCount;
    private int nThreads;

    public TaskRunner(File inDirectory, File outDirectory, File tmpDirectory) {
        this(inDirectory, outDirectory, tmpDirectory, Runtime.getRuntime().availableProcessors());
    }

    public TaskRunner(File inDirectory, File outDirectory, File tmpDirectory, int nThreads) {

        if (!tmpDirectory.exists() || !outDirectory.exists() || !inDirectory.exists())
            throw new MassCascadeException("Directory does not exist.");
        else if (!tmpDirectory.isDirectory() || !outDirectory.isDirectory() || !inDirectory.isDirectory())
            throw new MassCascadeException("File is not a directory.");

        this.inDirectory = inDirectory;
        this.outDirectory = outDirectory;
        this.tmpDirectory = tmpDirectory;
        this.nThreads = nThreads;
        this.taskCount = 1;

        tasks = new LinkedHashMap<Class<? extends ACallableTask>, ParameterMap>();
    }

    public void add(Class<? extends ACallableTask> taskClass, ParameterMap parameterMap) {
        tasks.put(taskClass, parameterMap);
    }

    public void run() {

        List<Container> containerList = new ArrayList<Container>();

//        LOGGER.log(Level.INFO, "Starting MassCascade with " + tasks.size() + " tasks and " + nThreads + " threads");
        long start = System.currentTimeMillis();

        try {
            for (Map.Entry<Class<? extends ACallableTask>, ParameterMap> entry : tasks.entrySet()) {

                if (taskCount == 1) {
                    Class<? extends ACallableTask> taskClass = entry.getKey();
                    if (!(taskClass.equals(CmlReader.class) || taskClass.equals(PsiMzmlReader.class) ||
                            taskClass.equals(XCaliburReader.class)))
                        throw new MassCascadeException("No file reader found");
                    containerList = readFromDirectory(entry);
                } else {
                    containerList = executeTask(entry, containerList);
                }

                taskCount++;
            }
            long stop = System.currentTimeMillis();
            int time = (int) Math.round((stop - start) / 1000d);

            for (Container container : containerList) container.getDataFile().delete();

//            LOGGER.log(Level.INFO, "Finished " + (taskCount - 1) + " of " + tasks.size() + " tasks in " + time + " s");
            LOGGER.log(Level.INFO, nThreads + "," + time);

        } catch (MassCascadeException exception) {
            LOGGER.log(Level.ERROR, "MassCascade error: " + exception.getMessage());
        }
    }

    private List<Container> readFromDirectory(
            Map.Entry<Class<? extends ACallableTask>, ParameterMap> entry) throws MassCascadeException {

        ExecutorService threadPool = Executors.newFixedThreadPool(nThreads);

        List<Container> resultList = new ArrayList<Container>();

        File[] files = new File[0];
        if (entry.getKey().equals(XCaliburReader.class))
            files = inDirectory.listFiles(new Filter(Constants.FILE_FORMATS.RAW));
        else if (entry.getKey().equals(PsiMzmlReader.class))
            files = inDirectory.listFiles(new Filter(Constants.FILE_FORMATS.MZML));
        else if (entry.getKey().equals(CmlReader.class))
            files = inDirectory.listFiles(new Filter(Constants.FILE_FORMATS.CML));

        List<Future<Container>> callableList = new ArrayList<Future<Container>>();

//        LOGGER.log(Level.INFO, "Found " + files.length + " files in \"" + inDirectory.getName() + "\"");
//        LOGGER.log(Level.INFO, "Running task " + taskCount + ": " + entry.getKey().getSimpleName());

        try {
            for (File file : files) {

                String name = file.getName().substring(0, file.getName().lastIndexOf("."));

                ParameterMap params = new ParameterMap();
                params.put(Parameter.DATA_FILE, file);
                params.put(Parameter.RAW_CONTAINER, new RawContainer(name, tmpDirectory.getAbsolutePath()));
                params.put(Parameter.WORKING_DIRECTORY, tmpDirectory.getAbsolutePath());

                Constructor<?> cstr = entry.getKey().getConstructor(ParameterMap.class);
                ACallableTask task = (ACallableTask) cstr.newInstance(params);

                callableList.add(threadPool.submit(task));
            }

            threadPool.shutdown();

            for (Future<Container> future : callableList) {
                RawContainer container = (RawContainer) future.get();
                if (container != null) resultList.add(container);
            }
        } catch (Exception e) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
            throw new MassCascadeException("Could not read the file: " + e.getMessage());
        }
//        LOGGER.log(Level.INFO, "Finished task " + taskCount + "");

        return resultList;
    }

    private List<Container> executeTask(Map.Entry<Class<? extends ACallableTask>, ParameterMap> entry,
            List<Container> containerList) throws MassCascadeException {

//        LOGGER.log(Level.INFO, "Running task " + taskCount + ": " + entry.getKey().getSimpleName());

        ExecutorService threadPool = Executors.newFixedThreadPool(nThreads);

        List<Container> resultList = new ArrayList<Container>();
        List<Future<Container>> callableList = new ArrayList<Future<Container>>();

        try {
            for (Container container : containerList) {
                ParameterMap params = entry.getValue();
                if (container instanceof RawContainer) params.put(Parameter.RAW_CONTAINER, container);
                else if (container instanceof ProfileContainer) params.put(Parameter.PROFILE_CONTAINER, container);
                else if (container instanceof SpectrumContainer) params.put(Parameter.SPECTRUM_CONTAINER, container);

                Constructor<?> cstr = entry.getKey().getConstructor(ParameterMap.class);
                ACallableTask task = (ACallableTask) cstr.newInstance(params);

                callableList.add(threadPool.submit(task));
            }

            threadPool.shutdown();

            for (Future<Container> future : callableList) {
                Container container = future.get();
                if (container != null) resultList.add(container);
            }
        } catch (Exception e) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
            throw new MassCascadeException("Could not execute the task: " + e.getMessage());
        } finally {
//            LOGGER.log(Level.INFO, "Finished task " + taskCount + ": Deleting tmp files");
            for (Container container : containerList) container.getDataFile().delete();
        }

        return resultList;
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
