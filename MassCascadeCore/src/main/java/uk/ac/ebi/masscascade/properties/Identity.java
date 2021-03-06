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

import uk.ac.ebi.masscascade.core.PropertyType;
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
    private String source;
    private String evidence;
    private String comments;

    /**
     * Constructor for serialization.
     */
    public Identity() {

        id = "";
        name = "";
        notation = "";
        score = 0;
        source = "";
        comments = "";
        evidence = "";
    }

    /**
     * Constructs an identity property.
     *
     * @param id       the identifier
     * @param name     the name
     * @param notation the notation
     * @param score    the score
     * @param source   the source
     * @param evidence the evidence
     * @param comments the comments
     */
    public Identity(String id, String name, String notation, double score, String source, String evidence,
            String comments) {

        this.id = id;
        this.name = name;
        this.notation = notation;
        this.score = score;
        this.source = source;
        this.comments = comments;
        this.evidence = evidence;
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
     * Returns the score.
     *
     * @return the score
     */
    public double getScore() {
        return score;
    }

    /**
     * Sets the notation.
     *
     * @param notation the notation
     */
    public void setNotation(String notation) {
        this.notation = notation;
    }

    /**
     * Returns the source.
     *
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * Returns the comment string.
     *
     * @return the comment string
     */
    public String getComments() {
        return comments;
    }

    /**
     * Returns the evidence string.
     *
     * @return the evidence string
     */
    public String getEvidence() {
        return evidence;
    }

    /**
     * Returns the property type.
     *
     * @return the property type
     */
    public PropertyType getType() {
        return PropertyType.Identity;
    }
}
