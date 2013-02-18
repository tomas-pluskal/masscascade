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

package uk.ac.ebi.masscascade.parameters;

import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Index;
import uk.ac.ebi.masscascade.ws.chemspider.ChemSpiderSearch;
import uk.ac.ebi.masscascade.ws.massbank.MassBankSearch;

public enum WebTasks implements Index {

    // webservices
    CHEMSPIDER(ChemSpiderSearch.class, "DCS"),
    MASSBANK(MassBankSearch.class, "DMB");

    private final Class<? extends CallableTask> className;
    private final String identifier;

    /**
     * Constructs a processing task.
     *
     * @param className  the task class
     * @param identifier the abbreviated identifier
     */
    private WebTasks(Class<? extends CallableTask> className, String identifier) {

        this.className = className;
        this.identifier = "-" + identifier;
    }

    /**
     * Returns the callable class.
     *
     * @return the callable class
     */
    public synchronized Class<? extends CallableTask> getCallableClass() {

        return className;
    }

    /**
     * Returns the abbreviated identifier.
     *
     * @return the identifier
     */
    public synchronized String getIdentifier() {

        return identifier;
    }

    /**
     * Simple placeholder for 'empty' default values.
     *
     * @return the placeholder
     */
    private static String PLACEHOLDER() {

        return "";
    }

    /**
     * For task classes outside the core module, the static importer returns the class as required by the enum.
     *
     * @param className the full class name
     * @return the class
     */
    @SuppressWarnings("unchecked")
    private static Class<? extends CallableTask> getClassFor(String className) {

        Class<? extends CallableTask> theClass = null;

        try {
            theClass = (Class<? extends CallableTask>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            // should never happen
            e.printStackTrace();
        }

        return theClass;
    }

    /**
     * Returns the corresponding enum for a task class.
     *
     * @param callableClass the task class
     * @return the enum
     */
    public static CoreTasks getEnumFor(Class<? extends CallableTask> callableClass) {

        for (CoreTasks x : CoreTasks.values())
            if (x.getCallableClass() == callableClass) return x;

        return null;
    }
}
