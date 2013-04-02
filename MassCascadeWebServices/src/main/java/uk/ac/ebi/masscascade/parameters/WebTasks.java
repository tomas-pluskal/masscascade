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

import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Index;
import uk.ac.ebi.masscascade.ws.chemspider.ChemSpiderSearch;
import uk.ac.ebi.masscascade.ws.massbank.MassBankSearch;

/**
 * An index for web tasks. Callable tasks that implement web services are registered here and assigned an identifier.
 */
public enum WebTasks implements Index {

    CHEMSPIDER(ChemSpiderSearch.class, "DCS"),
    MASSBANK(MassBankSearch.class, "DMB");

    private final Class<? extends CallableTask> className;
    private final String identifier;

    /**
     * Constructs an index for a web task. The class for the web task is assigned an identifier unique to the task.
     *
     * @param className  the task class
     * @param identifier the abbreviated identifier
     */
    private WebTasks(Class<? extends CallableTask> className, String identifier) {

        this.className = className;
        this.identifier = "-" + identifier;
    }

    /**
     * Returns the callable task class assigned to this index.
     *
     * @return the callable task class
     */
    public synchronized Class<? extends CallableTask> getCallableClass() {
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
}
