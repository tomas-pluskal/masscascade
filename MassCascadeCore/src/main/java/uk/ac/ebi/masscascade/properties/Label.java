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

package uk.ac.ebi.masscascade.properties;

import uk.ac.ebi.masscascade.core.PropertyManager;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.Property;

/**
 * Class implementing a generic label property.
 */
public class Label implements Property {

    private String name;
    private String label;

    /**
     * Constructor for serialization purposes.
     */
    public Label() {

        name = "";
        label = "";
    }

    /**
     * Constructor for a single label property.
     *
     * @param label the label string
     */
    public Label(String name, String label) {

        this.name = name;
        this.label = label;
    }

    /**
     * Returns the name of the label.
     *
     * @return the name
     */
    public String getName() {

        return name;
    }

    /**
     * Returns the label.
     *
     * @return the label
     */
    public String toString() {

        return label;
    }

    /**
     * Returns the label.
     */
    @Override
    public <T> T getValue(Class<T> type) {

        if (String.class != type) throw new MassCascadeException("Property value is not of type " + type);
        return type.cast(label);
    }

    /**
     * Returns the property type.
     *
     * @return the property type
     */
    public PropertyManager.TYPE getType() {

        return PropertyManager.TYPE.Label;
    }
}
