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

package uk.ac.ebi.masscascade.interfaces;

import uk.ac.ebi.masscascade.core.PropertyManager;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface for all extracted spectra.
 */
public interface Spectrum extends Scan {

    /**
     * Adds a profile to the spectrum.
     *
     * @param peak a profile
     */
    void addProfile(Profile peak);

    /**
     * Returns the rt range.
     *
     * @return the rt range
     */
    Range getRtRange();

    /**
     * Gets a profile by its identifier.
     *
     * @param peakId an identifier
     * @return the profile
     */
    Profile getProfile(int peakId);

    /**
     * Returns a profile map containing profile id - profile associations.
     *
     * @return the profile map
     */
    Map<Integer, Profile> getProfileMap();

    /**
     * Retrieves the value of the selected property.
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
}
