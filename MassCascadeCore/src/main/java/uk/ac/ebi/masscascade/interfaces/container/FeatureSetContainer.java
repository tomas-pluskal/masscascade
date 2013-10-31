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

import uk.ac.ebi.masscascade.interfaces.FeatureSet;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import java.util.Iterator;
import java.util.List;

/**
 * This is a featureset container holding featureset data.
 */
public interface FeatureSetContainer extends Container, Iterable<FeatureSet> {

    /**
     * Returns a featureset by its identifier.
     *
     * @param featureSetId the feature identifier
     * @return the featureset
     */
    FeatureSet getFeatureSet(int featureSetId);

    /**
     * Adds a featureset to the collection.
     *
     * @param featureSet the feature
     */
    void addFeatureSet(FeatureSet featureSet);

    /**
     * Closes the file.
     */
    void finaliseFile();

    /**
     * Returns an iterator over a set of elements of type T.
     *
     * @return an Iterator.
     */
    Iterator<FeatureSet> iterator();

    /**
     * Returns a list of rt-m/z value pairs.
     *
     * @return the rt-m/z value pairs
     */
    List<XYPoint> getBasePeaks();

    /**
     * Returns the ion mode.
     *
     * @return the ion mode
     */
    Constants.ION_MODE getIonMode();
}
