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

package uk.ac.ebi.masscascade.interfaces.container;

import com.google.common.collect.TreeMultimap;
import uk.ac.ebi.masscascade.core.container.file.FileManager;
import uk.ac.ebi.masscascade.interfaces.Profile;

import java.util.Iterator;
import java.util.List;

/**
 * This is a profile container holding profile data.
 */
public interface ProfileContainer extends Container, Iterable<Profile> {

    /**
     * Adds a profile to the collection.
     *
     * @param profile the profile
     */
    void addProfile(Profile profile);

    /**
     * Adds a list of profiles to the collection.
     *
     * @param profileList the profile list
     */
    void addProfileList(List<Profile> profileList);

    /**
     * Closes the file.
     */
    void finaliseFile();

    /**
     * Returns the retention times of the profiles.
     *
     * @return the profile retention times.
     */
    TreeMultimap<Double, Integer> getTimes();

    /**
     * Returns the file manager.
     *
     * @return the file manager
     */
    FileManager getFileManager();

    /**
     * Returns a profile by its identifier.
     *
     * @param i the profile identifier
     * @return the profile
     */
    Profile getProfile(int i);

    /**
     * Returns the complete profile list.
     *
     * @return the profile list
     */
    List<Profile> getProfileList();

    /**
     * Returns an iterator over a set of elements of type T.
     *
     * @return an Iterator.
     */
    @Override
    Iterator<Profile> iterator();
}
