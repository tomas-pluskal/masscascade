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

package uk.ac.ebi.masscascade.library;

import uk.ac.ebi.masscascade.interfaces.Option;
import uk.ac.ebi.masscascade.reference.ReferenceContainer;

public enum LibraryParameter implements Option {

    REFERENCE_LIBRARY("Reference library", null, ReferenceContainer.class);

    private String description;
    private Object defaultValue;
    private Class<?> type;

    private LibraryParameter(String description, Object defaultValue) {

        this.description = description;
        this.defaultValue = defaultValue;
        this.type = defaultValue.getClass();
    }

    private LibraryParameter(String description, Object defaultValue, Class<?> type) {

        this.description = description;
        this.defaultValue = defaultValue;
        this.type = type;
    }

    /**
     * Gets the description of the option.
     *
     * @return the description
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Gets the default value of the option.
     *
     * @return the default value
     */
    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * Gets the type of the option's value.
     *
     * @return the option's type
     */
    @Override
    public Class<?> getType() {
        return type;
    }
}
