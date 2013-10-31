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

package uk.ac.ebi.masscascade.core.container.memory.featureset;

import uk.ac.ebi.masscascade.core.container.memory.MemoryContainer;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.Feature;
import uk.ac.ebi.masscascade.interfaces.FeatureSet;
import uk.ac.ebi.masscascade.interfaces.container.FeatureSetContainer;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Class containing a collection of feature sets.
 */
public class MemoryFeatureSetContainer extends MemoryContainer implements FeatureSetContainer {

    private final String id;
    private final Constants.ION_MODE ionMode;
    private final List<XYPoint> basePeaks;
    private final LinkedHashMap<Integer, FeatureSet> featureSetMap;

    /**
     * Constructs an empty feature set file.
     *
     * @param id      the file identifier
     * @param ionMode the ion mode
     */
    public MemoryFeatureSetContainer(String id, Constants.ION_MODE ionMode) {
        this(id, ionMode, new LinkedHashMap<Integer, FeatureSet>());
    }

    /**
     * Constructs a populated feature set file.
     *
     * @param id            the file identifier
     * @param ionMode       the ion mode
     * @param featureSetSet the collection of feature sets
     */
    public MemoryFeatureSetContainer(String id, Constants.ION_MODE ionMode, Collection<FeatureSet> featureSetSet) {

        this.id = id;
        this.ionMode = ionMode;
        basePeaks = new ArrayList<>();
        featureSetMap = new LinkedHashMap<>();

        for (FeatureSet featureSet : featureSetSet) {
            addFeatureSet(featureSet);
        }
    }

    /**
     * Constructs a populated feature set file.
     *
     * @param id         the file identifier
     * @param ionMode    the ion mode
     * @param featureSetMap the map of feature id - file pointer associations
     */
    public MemoryFeatureSetContainer(String id, Constants.ION_MODE ionMode, LinkedHashMap<Integer, FeatureSet> featureSetMap) {

        this.id = id;
        this.ionMode = ionMode;
        this.featureSetMap = featureSetMap;
        basePeaks = new ArrayList<>();
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
     * Returns the ion mode.
     *
     * @return the ion mode
     */
    @Override
    public Constants.ION_MODE getIonMode() {
        return ionMode;
    }

    /**
     * Returns a featureset by its identifier.
     *
     * @param featureSetId the feature identifier
     * @return the featureset
     */
    public FeatureSet getFeatureSet(int featureSetId) {
        return (featureSetMap.containsKey(featureSetId)) ? featureSetMap.get(featureSetId) : null;
    }

    /**
     * Adds a featureset to the collection.
     *
     * @param featureSet the feature
     */
    public void addFeatureSet(FeatureSet featureSet) {
        featureSetMap.put(featureSet.getIndex(), featureSet);
        basePeaks.add(new XYPoint(featureSet.getRetentionTime(), featureSet.getBasePeak().getFirst().x));
    }

    public int size() {
        return featureSetMap.size();
    }

    /**
     * Deletes all data from the container.
     *
     * @return if successful
     */
    public boolean removeAll() {
        featureSetMap.clear();
        basePeaks.clear();
        return (featureSetMap.size() == 0);
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
     * Returns the current working directory.
     *
     * @return the working directory
     */
    @Override
    public String getWorkingDirectory() {
        return "";
    }

    /**
     * Closes the file.
     */
    @Override
    public void finaliseFile() {
        // do nothing
    }

    /**
     * Returns an iterator over a set of elements of type T.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<FeatureSet> iterator() {
        return featureSetMap.values().iterator();
    }

    /**
     * Returns a list of rt-m/z value pairs.
     *
     * @return the rt-m/z value pairs
     */
    @Override
    public List<XYPoint> getBasePeaks() {
        return basePeaks;
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
                return new FeatureSetIterator(featureSetMap.values().iterator());
            }
        };
    }
}

class FeatureSetIterator implements Iterator<Feature> {

    private Iterator<FeatureSet> featureSetIterator;
    private Iterator<Feature> profileIterator;
    private FeatureSet cachedFeatureSet;

    public FeatureSetIterator(Iterator<FeatureSet> featureSetIterator) {
        this.featureSetIterator = featureSetIterator;

        cachedFeatureSet = featureSetIterator.next();
        profileIterator = cachedFeatureSet.iterator();
    }

    @Override
    public boolean hasNext() {
        return profileIterator.hasNext() || featureSetIterator.hasNext();
    }

    @Override
    public Feature next() {

        if (!profileIterator.hasNext()) {
            cachedFeatureSet = featureSetIterator.next();
            profileIterator = cachedFeatureSet.iterator();
        }

        return profileIterator.next();
    }

    @Override
    public void remove() {
        // do nothing
    }
}
