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
import uk.ac.ebi.masscascade.interfaces.Feature;
import uk.ac.ebi.masscascade.parameters.Constants;

import java.util.Iterator;
import java.util.List;

/**
 * This is a feature container holding feature data.
 */
public interface FeatureContainer extends Container, Iterable<Feature> {

    /**
     * Adds a feature to the collection.
     *
     * @param feature the feature
     */
    void addFeature(Feature feature);

    /**
     * Returns the ion mode.
     *
     * @return the ion mode
     */
    Constants.ION_MODE getIonMode();

    /**
     * Adds a list of features to the collection.
     *
     * @param featureList the feature list
     */
    void addFeatureList(List<Feature> featureList);

    /**
     * Closes the file.
     */
    void finaliseFile();

    /**
     * Returns the retention times of the features.
     *
     * @return the feature retention times.
     */
    TreeMultimap<Double, Integer> getTimes();

    /**
     * Returns the file manager.
     *
     * @return the file manager
     */
    FileManager getFileManager();

    /**
     * Returns a feature by its identifier.
     *
     * @param i the feature identifier
     * @return the feature
     */
    Feature getFeature(int i);

    /**
     * Returns the complete feature list.
     *
     * @return the feature list
     */
    List<Feature> getFeatureList();

    /**
     * Returns an iterator over a set of elements of type T.
     *
     * @return an Iterator.
     */
    @Override
    Iterator<Feature> iterator();
}
