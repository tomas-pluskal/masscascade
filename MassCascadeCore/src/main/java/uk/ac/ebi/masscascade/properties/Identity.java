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
 * Class implementing an identity property.
 */
public class Identity implements Property {

    private String id;
    private String name;
    private String notation;
    private double score;

    /**
     * Constructor for serialization.
     */
    public Identity() {

        id = "";
        name = "";
        notation = "";
        score = -1;
    }

    /**
     * Constructs an identity property.
     *
     * @param id       the identifier
     * @param name     the name
     * @param notation the notation
     * @param score    the score
     */
    public Identity(String id, String name, String notation, double score) {

        this.id = id;
        this.name = name;
        this.notation = notation;
        this.score = score;
    }

    /**
     * Returns the property name.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns the score.
     */
    @Override
    public <T> T getValue(Class<T> type) {

        if (Double.class != type) throw new MassCascadeException("Property value is not of type " + type);
        return type.cast(score);
    }

    /**
     * Returns the identity label.
     *
     * @return the label
     */
    public String toString() {
        return name;
    }

    /**
     * Returns the identifier.
     *
     * @return the identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the notation.
     *
     * @return the notation
     */
    public String getNotation() {
        return notation;
    }

    /**
     * Returns the property type.
     *
     * @return the property type
     */
    public PropertyManager.TYPE getType() {
        return PropertyManager.TYPE.Identity;
    }
}
