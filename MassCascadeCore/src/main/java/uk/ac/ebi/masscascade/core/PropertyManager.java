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

package uk.ac.ebi.masscascade.core;

import uk.ac.ebi.masscascade.interfaces.Property;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class for property handling.
 */
public class PropertyManager {

    private final Map<PropertyType, LinkedHashSet<Property>> properties;

    /**
     * Default constructor.
     */
    public PropertyManager() {
        properties = new HashMap<>();
    }

    /**
     * Adds a property.
     *
     * @param property the property
     */
    public void addProperty(Property property) {

        if (hasProperty(property.getType())) properties.get(property.getType()).add(property);
        else setProperty(property);
    }

    /**
     * Sets the property.
     *
     * @param property the property
     */
    public void setProperty(Property property) {

        LinkedHashSet<Property> props = new LinkedHashSet<>();
        props.add(property);
        properties.put(property.getType(), props);
    }

    /**
     * Returns a particular property list.
     *
     * @param type the property type
     * @return the property list
     */
    public <T> Set<T> getProperty(PropertyType type, Class<T> typeClass) {
        return properties.containsKey(type) ? (Set<T>) properties.get(type) : (Set<T>) new LinkedHashSet<Property>();
    }

    /**
     * Whether a particular property is set.
     *
     * @param type the property type
     * @return whether property is set
     */
    public boolean hasProperty(PropertyType type) {
        return properties.containsKey(type);
    }
}
