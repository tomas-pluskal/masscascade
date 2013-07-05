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

package uk.ac.ebi.masscascade.core.container.memory.profile;

import com.google.common.collect.TreeMultimap;
import org.apache.log4j.Logger;
import uk.ac.ebi.masscascade.core.container.file.FileManager;
import uk.ac.ebi.masscascade.core.container.memory.MemoryContainer;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.container.ProfileContainer;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Class containing a collection of profiles.
 */
public class MemoryProfileContainer extends MemoryContainer implements ProfileContainer {

    private static final Logger LOGGER = Logger.getLogger(MemoryProfileContainer.class);

    private final String id;

    private final TreeMultimap<Double, Integer> times;
    private final LinkedHashMap<Integer, Profile> profileMap;

    /**
     * Constructs an empty profile file.
     *
     * @param id the file identifier
     */
    public MemoryProfileContainer(String id) {

        this.id = id;

        times = TreeMultimap.create();
        profileMap = new LinkedHashMap<Integer, Profile>();
    }

    /**
     * Constructs a populated profile file.
     *
     * @param id         the file identifier
     * @param dataFile   the tmp data file
     * @param times      the map of retention time - profile id associations
     * @param profileMap the map of profile id - file pointer associations
     */
    public MemoryProfileContainer(String id, String dataFile, TreeMultimap<Double, Integer> times,
            LinkedHashMap<Integer, Profile> profileMap) {

        this.id = id;

        this.times = times;
        this.profileMap = profileMap;
    }

    /**
     * Adds a profile to the collection.
     *
     * @param profile the profile
     */
    @Override
    public void addProfile(Profile profile) {

        profileMap.put(profile.getId(), profile);
        times.put(profile.getRetentionTime(), profile.getId());
    }

    /**
     * Adds a list of profiles to the collection.
     *
     * @param profileList the profile list
     */
    @Override
    public void addProfileList(List<Profile> profileList) {
        for (Profile profile : profileList) addProfile(profile);
    }

    /**
     * Closes the file.
     */
    @Override
    public void finaliseFile() {
        // nothing to do
    }

    /**
     * Returns the identifier of the collection.
     *
     * @return the identifier
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * Returns the retention times of the profiles.
     *
     * @return the profile retention times.
     */
    @Override
    public TreeMultimap<Double, Integer> getTimes() {
        return times;
    }

    /**
     * Returns the file manager.
     *
     * @return the file manager
     */
    @Override
    public FileManager getFileManager() {
        throw new MassCascadeException("Memory containers are not file based.");
    }

    /**
     * Returns a profile by its identifier.
     *
     * @param i the profile identifier
     * @return the profile
     */
    @Override
    public Profile getProfile(int i) {
        return profileMap.containsKey(i) ? profileMap.get(i) : null;
    }

    /**
     * Returns the complete profile list.
     *
     * @return the profile list
     */
    @Override
    public List<Profile> getProfileList() {
        return new ArrayList<Profile>(profileMap.values());
    }

    /**
     * Returns the current working directory.
     *
     * @return the working directory
     */
    @Override
    public String getWorkingDirectory() {
        return "";
    }

    /**
     * Returns the actual data file.
     *
     * @return the dta file
     */
    @Override
    public File getDataFile() {
        throw new MassCascadeException("Memory containers are not file based.");
    }

    /**
     * Deletes all data from the container.
     *
     * @return if successful
     */
    @Override
    public boolean removeAll() {
        profileMap.clear();
        times.clear();
        return profileMap.size() == 0;
    }

    /**
     * Gets the size of the container.
     *
     * @return the size
     */
    @Override
    public int size() {
        return profileMap.size();
    }

    /**
     * Returns an iterator over a set of elements of type T.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<Profile> iterator() {
        return profileMap.values().iterator();
    }

    /**
     * Returns a profile iterator.
     *
     * @return the profile iterator
     */
    @Override
    public Iterable<Profile> profileIterator() {
        return new Iterable<Profile>() {
            public Iterator<Profile> iterator() {
                return profileMap.values().iterator();
            }
        };
    }
}
