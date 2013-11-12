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

package uk.ac.ebi.masscascade.core.container.file.featureset;

import uk.ac.ebi.masscascade.core.container.file.FileContainer;
import uk.ac.ebi.masscascade.core.container.file.FileManager;
import uk.ac.ebi.masscascade.core.featureset.FeatureSetFeatureIterator;
import uk.ac.ebi.masscascade.core.featureset.FeatureSetImpl;
import uk.ac.ebi.masscascade.core.featureset.FeatureSetIterator;
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
import java.util.Map;

/**
 * Class containing a collection of feature sets.
 */
public class FileFeatureSetContainer extends FileContainer implements FeatureSetContainer {

    private final String id;
    private final Constants.ION_MODE ionMode;
    private final List<XYPoint> basePeaks;
    private final LinkedHashMap<Integer, Long> featureSetMap;
    private final FileManager fileManager;

    /**
     * Constructs an empty feature set file.
     *
     * @param id      the file identifier
     * @param ionMode the ion mode
     */
    public FileFeatureSetContainer(String id, Constants.ION_MODE ionMode) {
        this(id, ionMode, System.getProperty("java.io.tmpdir"));
    }

    /**
     * Constructs an empty feature set file.
     *
     * @param id               the file identifier
     * @param ionMode          the ion mode
     * @param workingDirectory the working directory
     */
    public FileFeatureSetContainer(String id, Constants.ION_MODE ionMode, String workingDirectory) {

        this.id = id;
        this.ionMode = ionMode;
        featureSetMap = new LinkedHashMap<>();
        basePeaks = new ArrayList<>();

        fileManager = new FileManager(workingDirectory);
        fileManager.openFile();
    }

    /**
     * Constructs a populated feature set file.
     *
     * @param id            the file identifier
     * @param ionMode       the ion mode
     * @param dataFile      the tmp data file
     * @param featureSetSet the collection of feature sets
     */
    public FileFeatureSetContainer(String id, Constants.ION_MODE ionMode, String dataFile, Collection<FeatureSet> featureSetSet) {

        this.id = id;
        this.ionMode = ionMode;
        featureSetMap = new LinkedHashMap<>();
        basePeaks = new ArrayList<>();

        fileManager = new FileManager(new File(dataFile));
        fileManager.openFile();

        for (FeatureSet featureSet : featureSetSet)
            addFeatureSet(featureSet);

        fileManager.closeFile();
    }

    /**
     * Constructs a populated feature set file.
     *
     * @param id            the file identifier
     * @param ionMode       the ion mode
     * @param dataFile      the data file
     * @param featureSetMap the map of feature id - file pointer associations
     */
    public FileFeatureSetContainer(String id, Constants.ION_MODE ionMode, String dataFile,
                                   LinkedHashMap<Integer, Long> featureSetMap, List<XYPoint> basePeaks) {

        this.id = id;
        this.ionMode = ionMode;

        this.featureSetMap = featureSetMap;
        this.basePeaks = basePeaks;
        fileManager = new FileManager(new File(dataFile));
    }

    /**
     * Returns the feature set indices.
     *
     * @return the feature set indices
     */
    public Map<Integer, Long> getFeatureNumbers() {
        return featureSetMap;
    }

    /**
     * Adds a feature set to the collection.
     *
     * @param featureSet the feature set
     */
    @Override
    public void addFeatureSet(FeatureSet featureSet) {

        long featureSetIndex = fileManager.write(featureSet);
        featureSetMap.put(featureSet.getIndex(), featureSetIndex);
        basePeaks.add(new XYPoint(featureSet.getRetentionTime(), featureSet.getBasePeak().getFirst().x));
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
     * Returns a feature set by its identifier.
     *
     * @param featureSetId the feature identifier
     * @return the feature set
     */
    @Override
    public synchronized FeatureSet getFeatureSet(int featureSetId) {

        long featureSetIndex = -1;
        if (featureSetMap.containsKey(featureSetId)) featureSetIndex = featureSetMap.get(featureSetId);
        if (featureSetIndex == -1) return null;
        FeatureSet featureSet = fileManager.read(featureSetIndex, FeatureSetImpl.class);

        return featureSet;
    }

    /**
     * Returns the size of the container.
     *
     * @return the container size
     */
    @Override
    public int size() {
        return featureSetMap.size();
    }

    /**
     * Returns the currenct working directory.
     *
     * @return the working directory
     */
    @Override
    public String getWorkingDirectory() {
        return fileManager.getWorkingDirectory();
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
     * Returns the actual data file.
     *
     * @return the dta file
     */
    @Override
    public File getDataFile() {
        return fileManager.getDataFile();
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
     * Returns an iterator over a set of elements of type T.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<FeatureSet> iterator() {
        return new FeatureSetIterator(new ArrayList<Long>(featureSetMap.values()), fileManager);
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
                return new FeatureSetFeatureIterator(new ArrayList<>(featureSetMap.values()), fileManager);
            }
        };
    }
}
