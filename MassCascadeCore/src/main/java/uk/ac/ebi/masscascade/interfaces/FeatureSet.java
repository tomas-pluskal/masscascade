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

package uk.ac.ebi.masscascade.interfaces;

import uk.ac.ebi.masscascade.core.PropertyType;

import java.util.Map;
import java.util.Set;

/**
 * This is a featureset that is comprised of a list of profiles.
 */
public interface FeatureSet extends Scan, Iterable<Feature> {

    /**
     * Adds a feature to the featureset.
     *
     * @param feature a feature
     */
    void addProfile(Feature feature);

    /**
     * Returns the retention time range.
     *
     * @return the retention time range
     */
    Range getRtRange();

    /**
     * Returns a feature by its identifier.
     *
     * @param profileId an identifier
     * @return the feature
     */
    Feature getFeature(int profileId);

    /**
     * Returns a feature map containing feature id to feature object associations.
     *
     * @return the feature map
     */
    Map<Integer, Feature> getFeaturesMap();

    /**
     * Sets the featureset's parent.
     *
     * @param scanId the parent scan id
     * @param mz     the parent m/z
     * @param charge the parent charge
     */
    public void setParent(int scanId, double mz, int charge);

    /**
     * Returns the value of the selected property.
     *
     * @return the property value
     */
    <T> Set<T> getProperty(PropertyType type, Class<T> typeClass);

    /**
     * Whether the property is set.
     *
     * @return boolean if property set
     */
    boolean hasProperty(PropertyType type);

    /**
     * Sets a featureset property.
     *
     * @param property the property to be set
     */
    void setProperty(Property property);

    /**
     * Returns the size of the featureset, i.e., the number of profiles.
     *
     * @return the size
     */
    int size();
}
