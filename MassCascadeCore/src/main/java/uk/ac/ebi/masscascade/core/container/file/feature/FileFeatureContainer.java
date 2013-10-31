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

package uk.ac.ebi.masscascade.core.container.file.feature;

import com.google.common.collect.TreeMultimap;
import org.apache.log4j.Logger;
import uk.ac.ebi.masscascade.core.container.file.FileContainer;
import uk.ac.ebi.masscascade.core.container.file.FileManager;
import uk.ac.ebi.masscascade.core.feature.FeatureIterator;
import uk.ac.ebi.masscascade.core.feature.FeatureImpl;
import uk.ac.ebi.masscascade.interfaces.Feature;
import uk.ac.ebi.masscascade.interfaces.container.FeatureContainer;
import uk.ac.ebi.masscascade.parameters.Constants;

import java.io.File;
import java.util.*;

/**
 * Class containing a collection of features.
 */
public class FileFeatureContainer extends FileContainer implements FeatureContainer {

    private static final Logger LOGGER = Logger.getLogger(FileFeatureContainer.class);

    private final String id;
    private final Constants.ION_MODE ionMode;

    private final TreeMultimap<Double, Integer> featureTimes;
    private final LinkedHashMap<Integer, Long> featureNumber;

    private final FileManager fileManager;

    /**
     * Constructs an empty feature file using the OS tmp directory as working directory.
     *
     * @param id      the file identifier
     * @param ionMode the ion mode
     */
    public FileFeatureContainer(String id, Constants.ION_MODE ionMode) {
        this(id, ionMode, System.getProperty("java.io.tmpdir"));
    }

    /**
     * Constructs an empty feature file.
     *
     * @param id               the file identifier
     * @param ionMode          the ion mode
     * @param workingDirectory the working directory
     */
    public FileFeatureContainer(String id, Constants.ION_MODE ionMode, String workingDirectory) {

        this.id = id;
        this.ionMode = ionMode;

        featureTimes = TreeMultimap.create();
        featureNumber = new LinkedHashMap<Integer, Long>();

        fileManager = new FileManager(workingDirectory);
        fileManager.openFile();
    }

    /**
     * Constructs a populated feature file.
     *
     * @param id            the file identifier
     * @param ionMode       the ion mode
     * @param dataFile      the tmp data file
     * @param featureTimes  the map of retention time - feature id associations
     * @param featureNumber the map of feature id - file pointer associations
     */
    public FileFeatureContainer(String id, Constants.ION_MODE ionMode, String dataFile, TreeMultimap<Double, Integer> featureTimes,
                                LinkedHashMap<Integer, Long> featureNumber) {

        this.id = id;
        this.ionMode = ionMode;

        this.featureTimes = featureTimes;
        this.featureNumber = featureNumber;

        fileManager = new FileManager(new File(dataFile));
    }

    /**
     * Adds a feature to the collection.
     *
     * @param feature the feature
     */
    @Override
    public void addFeature(Feature feature) {

        long fileIndex = fileManager.write(feature);

        featureNumber.put(feature.getId(), fileIndex);
        featureTimes.put(feature.getRetentionTime(), feature.getId());
    }

    /**
     * Adds a list of features to the collection.
     *
     * @param featureList the feature list
     */
    @Override
    public void addFeatureList(List<Feature> featureList) {

        fileManager.openFile();
        for (Feature feature : featureList) addFeature(feature);
        fileManager.closeFile();
    }

    /**
     * Closes the file.
     */
    @Override
    public void finaliseFile() {

        fileManager.setTmp(false);
        fileManager.closeFile();
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
     * Returns the ion mode.
     *
     * @return the ion mode
     */
    @Override
    public Constants.ION_MODE getIonMode() {
        return ionMode;
    }

    /**
     * Returns the retention times of the features.
     *
     * @return the feature retention times.
     */
    @Override
    public TreeMultimap<Double, Integer> getTimes() {
        return featureTimes;
    }

    /**
     * Returns the feature indices.
     *
     * @return the feature indices
     */
    public Map<Integer, Long> getFeatureNumbers() {
        return featureNumber;
    }

    /**
     * Returns the file manager.
     *
     * @return the file manager
     */
    @Override
    public FileManager getFileManager() {
        return fileManager;
    }

    /**
     * Returns a feature by its identifier.
     *
     * @param i the feature identifier
     * @return the feature
     */
    @Override
    public synchronized Feature getFeature(int i) {

        long fileIndex = -1;
        if (featureNumber.containsKey(i)) fileIndex = featureNumber.get(i);
        if (fileIndex == -1) return null;
        Feature feature = fileManager.read(fileIndex, FeatureImpl.class);

        return feature;
    }

    /**
     * Returns the complete feature list.
     *
     * @return the feature list
     */
    @Override
    public synchronized List<Feature> getFeatureList() {

        List<Feature> featureList = new ArrayList<>();
        for (Long l : featureNumber.values()) featureList.add(fileManager.read(l, FeatureImpl.class));

        return featureList;
    }

    /**
     * Returns the current working directory.
     *
     * @return the working directory
     */
    @Override
    public String getWorkingDirectory() {
        return fileManager.getWorkingDirectory();
    }

    /**
     * Returns the actual data file.
     *
     * @return the dta file
     */
    @Override
    public File getDataFile() {
        return fileManager.getDataFile();
    }

    /**
     * Returns the size of the container
     *
     * @return the container's size
     */
    @Override
    public int size() {
        return featureNumber.size();
    }

    /**
     * Deletes all data from the container.
     *
     * @return if successful
     */
    @Override
    public boolean removeAll() {
        return fileManager.removeFile();
    }

    /**
     * Gets the size of the container.
     *
     * @return the size
     */
    public int getContainerSize() {
        return featureNumber.size();
    }

    /**
     * Returns an iterator over a set of elements of type T.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<Feature> iterator() {
        return new FeatureIterator(new ArrayList<>(featureNumber.values()), fileManager);
    }

    /**
     * Returns a feature iterator.
     *
     * @return the feature iterator
     */
    @Override
    public Iterable<Feature> featureIterator() {

        return new Iterable<Feature>() {
            public Iterator<Feature> iterator() {
                return new FeatureIterator(new ArrayList<>(featureNumber.values()), fileManager);
            }
        };
    }
}
