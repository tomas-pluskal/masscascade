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

import uk.ac.ebi.masscascade.core.scan.ScanInfo;
import uk.ac.ebi.masscascade.core.scan.ScanLevel;
import uk.ac.ebi.masscascade.featurebuilder.FeatureMsnHelper;
import uk.ac.ebi.masscascade.interfaces.Chromatogram;
import uk.ac.ebi.masscascade.interfaces.Scan;
import uk.ac.ebi.masscascade.parameters.Constants;

import java.util.Iterator;
import java.util.List;

/**
 * This is a scan container holding scan data.
 */
public interface ScanContainer extends Container, Iterable<Scan> {

    /**
     * Adds a scan to the scan file and serializes the scan information.
     *
     * @param scan the scan to be added
     */
    void addScan(Scan scan);

    /**
     * Adds a list of scans and serializes the information.
     *
     * @param scans the list of scans to be added
     */
    void addScanList(List<Scan> scans);

    /**
     * Wraps up loose ends and closes the scan file construction process.
     *
     * @param date the creation date
     */
    void finaliseFile(String date);

    /**
     * Returns the list of MSn information.
     *
     * @return the MSn information
     */
    List<ScanLevel> getScanLevels();

    /**
     * Returns the file meta info.
     *
     * @return the meta info
     */
    ScanInfo getScanInfo();

    /**
     * Returns the number of scans of a particular level.
     * @param msn a MSN level
     * @return the number of scans
     */
    int size(Constants.MSN msn);

    /**
     * Returns the base feature chromatogram.
     *
     * @return the base feature chromatogram
     */
    Chromatogram getBasePeakChromatogram();

    /**
     * Returns the total ion chromatogram of level MSn.
     *
     * @param msn the MSn level
     * @return the total ion chromatogram
     */
    Chromatogram getTicChromatogram(Constants.MSN msn);

    /**
     * Returns the scan with the given scan index.
     *
     * @param i the scan index
     * @return the scan
     */
    Scan getScan(int i);

    /**
     * Returns the parent scan -> daughter scan -> parent mass association map.
     *
     * @return the map
     */
    FeatureMsnHelper getMsnHelper();

    /**
     * Returns the scan with the given array index.
     *
     * @param i the array index
     * @return the scan at the index
     */
    Scan getScanByIndex(int i);

    /**
     * Returns an iterator over a set of elements of type Scan.
     *
     * @return an Iterator.
     */
    @Override
    Iterator<Scan> iterator();

    /**
     * Returns an iterator over a set of elements of type Scan for a given MSn level.
     *
     * @return an Iterator.
     */
    Iterable<Scan> iterator(Constants.MSN msn);
}
