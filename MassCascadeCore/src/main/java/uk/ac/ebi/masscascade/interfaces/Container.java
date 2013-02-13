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

import java.io.File;

/**
 * Interface for file-based container.
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
}
