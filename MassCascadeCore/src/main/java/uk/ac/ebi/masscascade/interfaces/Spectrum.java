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

import uk.ac.ebi.masscascade.core.PropertyManager;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is a spectrum that is comprised of a list of profiles.
 */
public interface Spectrum extends Scan, Iterable<Profile> {

    /**
     * Adds a profile to the spectrum.
     *
     * @param profile a profile
     */
    void addProfile(Profile profile);

    /**
     * Returns the retention time range.
     *
     * @return the retention time range
     */
    Range getRtRange();

    /**
     * Returns a profile by its identifier.
     *
     * @param profileId an identifier
     * @return the profile
     */
    Profile getProfile(int profileId);

    /**
     * Returns a profile map containing profile id to profile object associations.
     *
     * @return the profile map
     */
    Map<Integer, Profile> getProfileMap();

    /**
     * Returns the value of the selected property.
     *
     * @return the property value
     */
    Set<Property> getProperty(PropertyManager.TYPE type);

    /**
     * Whether the property is set.
     *
     * @return boolean if property set
     */
    boolean hasProperty(PropertyManager.TYPE type);

    /**
     * Sets a spectrum property.
     *
     * @param property the property to be set
     */
    void setProperty(Property property);

    /**
     * Returns the size of the spectrum, i.e., the number of profiles.
     *
     * @return the size
     */
    int size();
}
