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

package uk.ac.ebi.masscascade.core.container.memory.feature;

import com.google.common.collect.TreeMultimap;
import org.apache.log4j.Logger;
import uk.ac.ebi.masscascade.core.container.file.FileManager;
import uk.ac.ebi.masscascade.core.container.memory.MemoryContainer;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.Feature;
import uk.ac.ebi.masscascade.interfaces.container.FeatureContainer;
import uk.ac.ebi.masscascade.parameters.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Class containing a collection of features.
 */
public class MemoryFeatureContainer extends MemoryContainer implements FeatureContainer {

    private static final Logger LOGGER = Logger.getLogger(MemoryFeatureContainer.class);

    private final String id;
    private final Constants.ION_MODE ionMode;

    private final TreeMultimap<Double, Integer> times;
    private final LinkedHashMap<Integer, Feature> featuresMap;

    /**
     * Constructs an empty feature file.
     *
     * @param id      the file identifier
     * @param ionMode the ion mode
     */
    public MemoryFeatureContainer(String id, Constants.ION_MODE ionMode) {

        this.id = id;
        this.ionMode = ionMode;

        times = TreeMultimap.create();
        featuresMap = new LinkedHashMap<>();
    }

    /**
     * Constructs a populated feature file.
     *
     * @param id          the file identifier
     * @param ionMode     the ion mode
     * @param times       the map of retention time - feature id associations
     * @param featuresMap the map of feature id - file pointer associations
     */
    public MemoryFeatureContainer(String id, Constants.ION_MODE ionMode, TreeMultimap<Double, Integer> times,
                                  LinkedHashMap<Integer, Feature> featuresMap) {

        this.id = id;
        this.ionMode = ionMode;

        this.times = times;
        this.featuresMap = featuresMap;
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
     * Adds a feature to the collection.
     *
     * @param feature the feature
     */
    @Override
    public void addFeature(Feature feature) {

        featuresMap.put(feature.getId(), feature);
        times.put(feature.getRetentionTime(), feature.getId());
    }

    /**
     * Adds a list of features to the collection.
     *
     * @param featureList the feature list
     */
    @Override
    public void addFeatureList(List<Feature> featureList) {
        for (Feature feature : featureList) addFeature(feature);
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
     * Returns the retention times of the features.
     *
     * @return the feature retention times.
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
     * Returns a feature by its identifier.
     *
     * @param i the feature identifier
     * @return the feature
     */
    @Override
    public Feature getFeature(int i) {
        return featuresMap.containsKey(i) ? featuresMap.get(i) : null;
    }

    /**
     * Returns the complete feature list.
     *
     * @return the feature list
     */
    @Override
    public List<Feature> getFeatureList() {
        return new ArrayList<Feature>(featuresMap.values());
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
        featuresMap.clear();
        times.clear();
        return featuresMap.size() == 0;
    }

    /**
     * Gets the size of the container.
     *
     * @return the size
     */
    @Override
    public int size() {
        return featuresMap.size();
    }

    /**
     * Returns an iterator over a set of elements of type T.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<Feature> iterator() {
        return featuresMap.values().iterator();
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
                return featuresMap.values().iterator();
            }
        };
    }
}
