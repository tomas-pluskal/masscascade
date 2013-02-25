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
import uk.ac.ebi.masscascade.parameters.ParameterMap;

/**
 * An abstract implementation of a callable task class. The stub instantiates the logging facility and retrieves the
 * task identifier.
 */
public abstract class CallableTask implements Task {

    protected final Logger LOGGER;
    protected final String IDENTIFIER;

    /**
     * Constructs a callable task class with an assigned identifier. The task class to be constructed must be registered
     * via its corresponding {@link Index}.
     *
     * @param taskClass the class of the task to be constructed
     */
    public CallableTask(Class<? extends CallableTask> taskClass) {

        LOGGER = Logger.getLogger(taskClass);
        IDENTIFIER = CoreTasks.getEnumFor(taskClass).getIdentifier();
    }

    /**
     * Sets the task class variables using the parameter map.
     *
     * @param params the parameter map containing the <code> Parameter </code> to <code> Object </code> relations.
     */
    @Override
    public abstract void setParameters(ParameterMap params);

    /**
     * Executes the task. The <code> Callable </code> returns a {@link Container} with the processed data.
     *
     * @return the container with the processed data
     */
    @Override
    public abstract Container call();
}
