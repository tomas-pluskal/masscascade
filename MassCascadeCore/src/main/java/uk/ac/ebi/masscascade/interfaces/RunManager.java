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
import java.util.Collection;
import java.util.List;

/**
 * Interface for the random access file manager implementations.
 */
public interface RunManager {

    /**
     * Opens the random access file.
     *
     * @return success
     */
    void openFile();

    /**
     * Closes the random access file.
     *
     * @return success
     */
    void closeFile();

    /**
     * Writes an object to the file.
     *
     * @param object the object to be written
     * @return the file pointer
     */
    long write(Object object);

    /**
     * Reads an object from the file.
     *
     * @param start       the file pointer
     * @param objectClass the class
     * @return the object
     */
    <T> T read(long start, Class<T> objectClass);

    /**
     * Reads a collection of objects from the file.
     *
     * @param startPositions the list of file pointers
     * @return the list of objects
     */
    List<Object> read(Collection<Long> startPositions, Class objectClass);

    /**
     * Returns the file name.
     *
     * @return the file name
     */
    String getFileName();

    /**
     * Returns the absolute file name.
     *
     * @return the absolute file name
     */
    String getAbsoluteFileName();

    /**
     * Returns the path of the working directory.
     *
     * @return the working directory
     */
    String getWorkingDirectory();

    /**
     * Returns the actual data file.
     *
     * @return the dta file
     */
    File getDataFile();

    /**
     * Deletes the file.
     *
     * @return if successful
     */
    boolean removeFile();
}
