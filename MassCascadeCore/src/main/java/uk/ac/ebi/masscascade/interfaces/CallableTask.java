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

package uk.ac.ebi.masscascade.interfaces;

import org.apache.log4j.Logger;
import uk.ac.ebi.masscascade.interfaces.container.Container;
import uk.ac.ebi.masscascade.parameters.CoreTasks;

/**
 * This is a callable task stub that instantiates the logging facility and retrieves the task identifier.
 */
public abstract class CallableTask implements Task {

    protected final Logger LOGGER;
    protected final String IDENTIFIER;

    public CallableTask(Class<? extends CallableTask> taskClass) {

        LOGGER = Logger.getLogger(taskClass);
        IDENTIFIER = CoreTasks.getEnumFor(taskClass).getIdentifier();
    }

    /**
     * Executes the task and processes the data.
     *
     * @return the processed mass spectrometry run
     */
    @Override
    public abstract Container call();
}
