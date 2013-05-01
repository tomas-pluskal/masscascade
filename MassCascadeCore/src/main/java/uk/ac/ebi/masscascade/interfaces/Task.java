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

package uk.ac.ebi.masscascade.interfaces;

import uk.ac.ebi.masscascade.interfaces.container.Container;
import uk.ac.ebi.masscascade.parameters.ParameterMap;

import java.io.File;
import java.util.concurrent.Callable;

/**
 * Top-most interface that is implemented by all task classes that are callable. Task classes that implement this
 * interface can run concurrently and accept parameters via a {@link ParameterMap}.
 */
public interface Task extends Callable<Container> {

    /**
     * Sets the task class variables using the parameter map.
     *
     * @param params the parameter map containing the <code> Parameter </code> to <code> Object </code> relations.
     */
    void setParameters(ParameterMap params);

    /**
     * Executes the task. The <code> Callable </code> returns a {@link Container} with the processed data.
     *
     * @return the container with the processed data
     */
    Container call();
}
