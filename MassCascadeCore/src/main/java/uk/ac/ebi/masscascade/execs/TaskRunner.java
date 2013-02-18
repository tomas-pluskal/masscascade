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

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import uk.ac.ebi.masscascade.core.container.file.FileContainerBuilder;
import uk.ac.ebi.masscascade.core.container.file.raw.FileRawContainer;
import uk.ac.ebi.masscascade.core.container.memory.MemoryContainerBuilder;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.container.Container;
import uk.ac.ebi.masscascade.interfaces.container.ProfileContainer;
import uk.ac.ebi.masscascade.interfaces.container.RawContainer;
import uk.ac.ebi.masscascade.interfaces.container.SpectrumContainer;
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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

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

        tasks = new LinkedHashMap<Class<? extends CallableTask>, ParameterMap>();
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
    public void run() {

        ListeningExecutorService threadPool = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(nThreads));

        List<ListenableFuture<Container>> resultList = new ArrayList<ListenableFuture<Container>>();

        File[] files = new File[0];
        Class<? extends CallableTask> taskClass = tasks.keySet().iterator().next();
        if (taskClass.equals(XCaliburReader.class))
            files = inDirectory.listFiles(new Filter(Constants.FILE_FORMATS.RAW));
        else if (taskClass.equals(PsiMzmlReader.class))
            files = inDirectory.listFiles(new Filter(Constants.FILE_FORMATS.MZML));
        else if (taskClass.equals(CmlReader.class))
            files = inDirectory.listFiles(new Filter(Constants.FILE_FORMATS.CML));

        long start = System.currentTimeMillis();

        try {

            for (File file : files) {
                String name = file.getName().substring(0, file.getName().lastIndexOf("."));

                ParameterMap params = new ParameterMap();
                params.put(Parameter.DATA_FILE, file);
                if (tmpDirectory == null) params.put(Parameter.RAW_CONTAINER,
                        MemoryContainerBuilder.getInstance().newInstance(RawContainer.class, name));
                else params.put(Parameter.RAW_CONTAINER,
                        FileContainerBuilder.getInstance().newInstance(RawContainer.class, name,
                                tmpDirectory.getAbsolutePath()));

                Constructor<?> cstr = taskClass.getConstructor(ParameterMap.class);
                CallableTask task = (CallableTask) cstr.newInstance(params);

                ListenableFuture<Container> future = threadPool.submit(task);
                Futures.addCallback(future, new FutureCallback<Container>() {

                    private Iterator<Map.Entry<Class<? extends CallableTask>, ParameterMap>> iter;
                    private List<File> tmpFiles;

                    @Override
                    public void onSuccess(Container container) {

                        if (iter == null) {
                            iter = tasks.entrySet().iterator();
                            iter.next();

                            tmpFiles = new ArrayList<File>();
                        }

                        if (tmpDirectory != null) tmpFiles.add(container.getDataFile());

                        if (!iter.hasNext()) {
                            if (tmpDirectory != null) for (File tmpFile : tmpFiles) tmpFile.delete();
                            return;
                        }

                        try {
                            Map.Entry<Class<? extends CallableTask>, ParameterMap> entry = iter.next();
                            ParameterMap params = entry.getValue();
                            if (container instanceof RawContainer) params.put(Parameter.RAW_CONTAINER, container);
                            else if (container instanceof ProfileContainer)
                                params.put(Parameter.PROFILE_CONTAINER, container);
                            else if (container instanceof SpectrumContainer)
                                params.put(Parameter.SPECTRUM_CONTAINER, container);

                            Constructor<?> cstr = entry.getKey().getConstructor(ParameterMap.class);
                            CallableTask task = (CallableTask) cstr.newInstance(params);

                            onSuccess(task.call());
                        } catch (Exception exception) {
                            LOGGER.log(Level.ERROR, "File could not be processed: " + exception.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        LOGGER.log(Level.ERROR, "File could not be read: " + throwable.getMessage());
                    }
                });
                resultList.add(future);
            }

            threadPool.shutdown();
            List<Container> con = Futures.allAsList(resultList).get();
        } catch (Exception exception) {
            LOGGER.log(Level.ERROR, "File could not be read: " + exception.getMessage());
        }

        long stop = System.currentTimeMillis();
        int time = (int) Math.round((stop - start) / 1000d);
        LOGGER.log(Level.INFO, "Finished " + tasks.size() + " tasks on " + files.length +
                " files in " + time + " s using " + nThreads + " threads.");
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
