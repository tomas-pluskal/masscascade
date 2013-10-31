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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import uk.ac.ebi.masscascade.core.container.file.FileContainerBuilder;
import uk.ac.ebi.masscascade.core.container.memory.MemoryContainerBuilder;
import uk.ac.ebi.masscascade.deconvolution.BiehmanDeconvolution;
import uk.ac.ebi.masscascade.deconvolution.SavitzkyGolayDeconvolution;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.container.Container;
import uk.ac.ebi.masscascade.interfaces.container.FeatureContainer;
import uk.ac.ebi.masscascade.interfaces.container.ScanContainer;
import uk.ac.ebi.masscascade.interfaces.container.FeatureSetContainer;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class TaskChain implements Callable<Container> {

    private static Logger LOGGER = Logger.getLogger(TaskChain.class);

    private final LinkedHashMap<Class<? extends CallableTask>, ParameterMap> tasks;
    private final List<File> tmpFiles;
    private final File tmpDir;
    private final File file;

    public TaskChain(File file, LinkedHashMap<Class<? extends CallableTask>, ParameterMap> tasks, File tmpDir) {

        this.file = file;
        this.tasks = tasks;
        this.tmpDir = tmpDir;
        this.tmpFiles = new ArrayList<>();
    }

    public Container call() {

        boolean success = false;
        Container container = null;
        Container lastRawContainer = null;

        try {
            Iterator<Map.Entry<Class<? extends CallableTask>, ParameterMap>> taskIter = tasks.entrySet().iterator();

            container = getIOContainer(taskIter.next().getKey());

            while (taskIter.hasNext()) {

                if (tmpDir != null) tmpFiles.add(container.getDataFile());

                Map.Entry<Class<? extends CallableTask>, ParameterMap> taskEntry = taskIter.next();

                Class<? extends CallableTask> taskClass = taskEntry.getKey();
                ParameterMap params = taskEntry.getValue();
                if (container instanceof ScanContainer) {
                    lastRawContainer = container;
                    params.put(Parameter.SCAN_CONTAINER, container);
                } else if (container instanceof FeatureContainer) {
                    params.put(Parameter.FEATURE_CONTAINER, container);
                } else if (container instanceof FeatureSetContainer) {
                    params.put(Parameter.FEATURE_SET_CONTAINER, container);
                }

                if (taskClass == BiehmanDeconvolution.class || taskClass == SavitzkyGolayDeconvolution.class) {
                    params.put(Parameter.SCAN_CONTAINER, lastRawContainer);
                }

                Constructor<?> cstr = taskEntry.getKey().getConstructor(ParameterMap.class);
                CallableTask task = (CallableTask) cstr.newInstance(params);

                container = task.call();
            }

            success = true;
        } catch (Exception exception) {
            LOGGER.log(Level.ERROR, "Task chain execution error.", exception);
        } finally {
            if (success) {
                for (int i = 0; i < tmpFiles.size() - 1; i++) {
                    tmpFiles.get(i).delete();
                }
            } else {
                for (File tmpFile : tmpFiles) {
                    tmpFile.delete();
                }
            }
        }

        return container;
    }

    private Container getIOContainer(Class<? extends CallableTask> ioClass) throws Exception {

        String name = file.getName().substring(0, file.getName().lastIndexOf("."));

        ParameterMap params = new ParameterMap();
        params.put(Parameter.DATA_FILE, file);
        if (tmpDir != null) {
            params.put(Parameter.SCAN_CONTAINER,
                    FileContainerBuilder.getInstance().newInstance(ScanContainer.class, name + Constants.DELIMITER,
                            tmpDir.getAbsolutePath()));
        } else {
            params.put(Parameter.SCAN_CONTAINER,
                    MemoryContainerBuilder.getInstance().newInstance(ScanContainer.class, name + Constants.DELIMITER));
        }

        Constructor<?> cstr = ioClass.getConstructor(ParameterMap.class);
        CallableTask task = (CallableTask) cstr.newInstance(params);

        return task.call();
    }
}
