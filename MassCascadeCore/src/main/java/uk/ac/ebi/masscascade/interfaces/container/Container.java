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

package uk.ac.ebi.masscascade.interfaces.container;

import java.io.File;

/**
 * This is a container holding data.
 */
public interface Container {

    /**
     * Returns the file identifier
     *
     * @return the identifier
     */
    String getId();

    /**
     * Returns the current working directory.
     *
     * @return the working directory
     */
    String getWorkingDirectory();

    /**
     * Deletes all data from the container.
     *
     * @return if successful
     */
    boolean removeAll();

    /**
     * Returns the actual data file.
     *
     * @return the dta file
     */
    File getDataFile();

    /**
     * Returns the size of the container
     *
     * @return the container's size
     */
    int size();

    /**
     * Returns a {@link ContainerBuilder} for the data classes that extend this class.
     *
     * @return The {@link ContainerBuilder} matching this {@link Container}
     */
    ContainerBuilder getBuilder();
}
