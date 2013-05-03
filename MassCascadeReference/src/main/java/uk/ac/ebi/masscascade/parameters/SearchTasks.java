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

package uk.ac.ebi.masscascade.parameters;

import uk.ac.ebi.masscascade.interfaces.CallableSearch;
import uk.ac.ebi.masscascade.interfaces.Index;
import uk.ac.ebi.masscascade.interfaces.Task;
import uk.ac.ebi.masscascade.library.LibraryGenerator;
import uk.ac.ebi.masscascade.library.LibrarySearch;

/**
 * An index for web tasks. Callable tasks that implement web services are registered here and assigned an identifier.
 */
public enum SearchTasks implements Index {

    LIBRARY_GENERATOR(LibraryGenerator.class, ""),
    LIBRARY_SEARCH(LibrarySearch.class, "LS");

    private final Class<? extends CallableSearch> className;
    private final String identifier;

    /**
     * Constructs an index for a web task. The class for the web task is assigned an identifier unique to the task.
     *
     * @param className  the task class
     * @param identifier the abbreviated identifier
     */
    private SearchTasks(Class<? extends CallableSearch> className, String identifier) {

        this.className = className;
        this.identifier = "-" + identifier;
    }

    /**
     * Returns the callable task class assigned to this index.
     *
     * @return the callable task class
     */
    public synchronized Class<? extends CallableSearch> getCallableClass() {
        return className;
    }

    /**
     * Returns the abbreviated identifier for the task class assigned to this index.
     *
     * @return the unique identifier of the task class
     */
    public synchronized String getIdentifier() {
        return identifier;
    }

    /**
     * Returns the corresponding enum for a task class.
     *
     * @param callableClass the task class
     * @return the enum
     */
    public static SearchTasks getEnumFor(Class<? extends Task> callableClass) {

        for (SearchTasks x : SearchTasks.values())
            if (x.getCallableClass() == callableClass) return x;

        return null;
    }
}
