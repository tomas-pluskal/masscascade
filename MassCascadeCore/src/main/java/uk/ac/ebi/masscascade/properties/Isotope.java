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

package uk.ac.ebi.masscascade.properties;

import uk.ac.ebi.masscascade.core.PropertyManager;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.Property;

/**
 * Class implementing the isotope property.
 */
public class Isotope implements Property {

    private final String name;
    private final Integer mzDifference;
    private final Integer parentId;
    private final Integer childId;

    /**
     * Constructor for serialization.
     */
    public Isotope() {

        name = "";
        parentId = -1;
        childId = -1;
        mzDifference = 0;
    }

    /**
     * Constructor for an isotope property.
     *
     * @param name         the isotope name
     * @param mzDifference the nominal isotopic difference
     * @param parentId     id of the most abundant profile in the isotope envelope
     * @param childId      id of the annotated profile
     */
    public Isotope(String name, int mzDifference, int parentId, int childId) {

        this.name = name;
        this.parentId = parentId;
        this.childId = childId;
        this.mzDifference = mzDifference;
    }

    /**
     * Returns the name of the isotopic signal.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the mz difference.
     */
    @Override
    public <T> T getValue(Class<T> type) {

        if (Integer.class != type) throw new MassCascadeException("Property value is not of type " + type);
        return type.cast(mzDifference);
    }

    /**
     * Returns the id of the annotated profile.
     *
     * @return the profile id
     */
    public Integer getChildId() {
        return childId;
    }

    /**
     * Returns the isotope label.
     *
     * @return the label
     */
    public String toString() {
        return parentId + ": " + name;
    }

    /**
     * Returns the id of the most abundant profile in the isotope envelope.
     *
     * @return the profile id
     */
    public Integer getParentId() {
        return parentId;
    }

    /**
     * Returns the property type.
     *
     * @return the property type
     */
    public PropertyManager.TYPE getType() {
        return PropertyManager.TYPE.Isotope;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof Isotope)) return false;

        Isotope isotope = (Isotope) obj;
        return ((isotope.getParentId().intValue() == this.parentId.intValue()) &&
                (isotope.getChildId().intValue() == this.childId.intValue()) &&
                (isotope.getValue(Integer.class).intValue() == this.mzDifference.intValue()));
    }

    @Override
    public int hashCode() {

        int hash = 13;

        hash = (hash * 7) + name.hashCode();
        hash = (hash * 5) + mzDifference;
        hash = (hash * 7) + parentId;
        hash = (hash * 7) + childId;

        return hash;
    }
}
