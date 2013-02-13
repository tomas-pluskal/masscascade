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

package uk.ac.ebi.masscascade.core.profile;

import com.google.common.collect.TreeMultimap;
import org.apache.log4j.Logger;
import uk.ac.ebi.masscascade.core.FileManager;
import uk.ac.ebi.masscascade.core.PropertyManager;
import uk.ac.ebi.masscascade.interfaces.Container;
import uk.ac.ebi.masscascade.interfaces.Profile;

import java.io.File;
import java.util.*;

/**
 * Class containing a collection of profiles.
 */
public class ProfileContainer implements Container, Iterable<Profile> {

    private static final Logger LOGGER = Logger.getLogger(ProfileContainer.class);

    private final String id;

    private final TreeMultimap<Double, Integer> profileTimes;
    private final LinkedHashMap<Integer, Long> profileNumber;

    private final FileManager fileManager;

    /**
     * Constructs an empty profile file.
     *
     * @param id               the file identifier
     * @param workingDirectory the working directory
     */
    public ProfileContainer(String id, String workingDirectory) {

        this.id = id;

        profileTimes = TreeMultimap.create();
        profileNumber = new LinkedHashMap<Integer, Long>();

        fileManager = new FileManager(workingDirectory);
        fileManager.openFile();
    }

    /**
     * Constructs a populated profile file.
     *
     * @param id            the file identifier
     * @param dataFile      the tmp data file
     * @param profileTimes  the map of retention time - profile id associations
     * @param profileNumber the map of profile id - file pointer associations
     */
    public ProfileContainer(String id, String dataFile, TreeMultimap<Double, Integer> profileTimes,
            LinkedHashMap<Integer, Long> profileNumber) {

        this.id = id;

        this.profileTimes = profileTimes;
        this.profileNumber = profileNumber;

        fileManager = new FileManager(new File(dataFile));
    }

    /**
     * Adds a profile to the collection.
     *
     * @param profile the profile
     */
    public void addProfile(Profile profile) {

        long fileIndex = fileManager.write(profile);

        profileNumber.put(profile.getId(), fileIndex);
        profileTimes.put(profile.getRetentionTime(), profile.getId());
    }

    /**
     * Adds a list of profiles to the collection.
     *
     * @param profileList the profile list
     */
    public void addProfileList(List<Profile> profileList) {

        fileManager.openFile();
        for (Profile profile : profileList) addProfile(profile);
        fileManager.closeFile();
    }

    /**
     * Closes the file.
     */
    public void finaliseFile() {

        fileManager.closeFile();
    }

    /**
     * Returns the identifier of the collection.
     *
     * @return the identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the retention times of the profiles.
     *
     * @return the profile retention times.
     */
    public TreeMultimap<Double, Integer> getProfileTimes() {
        return profileTimes;
    }

    /**
     * Returns the profile indices.
     *
     * @return the profile indices
     */
    public Map<Integer, Long> getProfileNumbers() {
        return profileNumber;
    }

    /**
     * Returns the file manager.
     *
     * @return the file manager
     */
    public FileManager getFileManager() {
        return fileManager;
    }

    /**
     * Returns a profile by its identifier.
     *
     * @param i the profile identifier
     * @return the profile
     */
    public synchronized Profile getProfile(int i) {

        long fileIndex = -1;
        if (profileNumber.containsKey(i)) fileIndex = profileNumber.get(i);
        if (fileIndex == -1) return null;
        Profile profile = fileManager.read(fileIndex, ProfileImpl.class);

        return profile;
    }

    /**
     * Returns the complete profile list.
     *
     * @return the profile list
     */
    public synchronized List<Profile> getProfileList() {

        List<Profile> profileList = new ArrayList<Profile>();

        for (Long l : profileNumber.values()) profileList.add(fileManager.read(l, ProfileImpl.class));

        return profileList;
    }

    /**
     * Returns the current working directory.
     *
     * @return the working directory
     */
    public String getWorkingDirectory() {
        return fileManager.getWorkingDirectory();
    }

    /**
     * Returns the actual data file.
     *
     * @return the dta file
     */
    public File getDataFile() {
        return fileManager.getDataFile();
    }

    /**
     * Deletes all data from the container.
     *
     * @return if successful
     */
    public boolean removeAll() {
        return fileManager.removeFile();
    }

    /**
     * Gets the size of the container.
     *
     * @return the size
     */
    public int getContainerSize() {
        return profileNumber.size();
    }

    /**
     * Returns an iterator over a set of elements of type T.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<Profile> iterator() {
        return new ProfileIterator(new ArrayList<Long>(profileNumber.values()), fileManager);
    }
}
