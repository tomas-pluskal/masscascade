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
 * Class implementing the adduct property.
 */
public class Adduct implements Property {

    private String name;
    private Integer parentId;
    private Integer childId;
    private Double mzDifference;

    /**
     * Constructor for serialization.
     */
    public Adduct() {

        name = "";
        parentId = -1;
        mzDifference = 0d;
    }

    /**
     * Constructor for an adduct property.
     *
     * @param mzDifference the mz difference
     * @param name         the adduct name
     * @param parentId     id of the parent profile
     * @param childId      id of the annotated profile
     */
    public Adduct(double mzDifference, String name, int parentId, int childId) {

        this.parentId = parentId;
        this.childId = childId;
        this.name = name;
        this.mzDifference = mzDifference;
    }

    /**
     * Returns the adduct name.
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

        if (Double.class != type) throw new MassCascadeException("Property value is not of type " + type);
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
     * Returns the adduct label.
     *
     * @return the adduct label
     */
    public String toString() {
        return parentId + ": " + name;
    }

    /**
     * Returns the id of the parent profile.
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
        return PropertyManager.TYPE.Adduct;
    }

    @Override
    public int hashCode() {

        int hash = 13;

        hash = (hash * 7) + name.hashCode();
        hash = (hash * 7) + Long.valueOf(Double.doubleToLongBits(mzDifference)).hashCode();
        hash = (hash * 7) + parentId;
        hash = (hash * 7) + childId;

        return hash;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof Isotope)) return false;

        Adduct adduct = (Adduct) obj;
        return ((adduct.getParentId().intValue() == this.parentId.intValue()) &&
                (adduct.getChildId().intValue() == this.childId.intValue()) &&
                (adduct.getValue(Double.class).doubleValue() == this.mzDifference));
    }
}
