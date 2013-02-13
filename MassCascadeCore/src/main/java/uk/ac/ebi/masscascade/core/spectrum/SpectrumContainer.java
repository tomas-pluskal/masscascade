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

package uk.ac.ebi.masscascade.core.spectrum;

import uk.ac.ebi.masscascade.core.FileManager;
import uk.ac.ebi.masscascade.interfaces.Container;
import uk.ac.ebi.masscascade.interfaces.Spectrum;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class containing a collection of spectra.
 */
public class SpectrumContainer implements Container, Iterable<PseudoSpectrum> {

    private final String id;
    private final LinkedHashMap<Integer, Long> spectraMap;

    private final FileManager fileManager;

    /**
     * Constructs an empty spectra file.
     *
     * @param id               the file identifier
     * @param workingDirectory the working directory
     */
    public SpectrumContainer(String id, String workingDirectory) {

        this.id = id;
        spectraMap = new LinkedHashMap<Integer, Long>();

        fileManager = new FileManager(workingDirectory);
        fileManager.openFile();
    }

    /**
     * Constructs a populated spectra file.
     *
     * @param id          the file identifier
     * @param dataFile    the tmp data file
     * @param spectrumSet the collection of spectra
     */
    public SpectrumContainer(String id, String dataFile, Collection<Spectrum> spectrumSet) {

        this.id = id;
        spectraMap = new LinkedHashMap<Integer, Long>();

        fileManager = new FileManager(new File(dataFile));
        fileManager.openFile();

        for (Spectrum spectrum : spectrumSet)
            addSpectrum(spectrum);

        fileManager.closeFile();
    }

    /**
     * Constructs a populated spectra file.
     *
     * @param id         the file identifier
     * @param dataFile   the data file
     * @param spectraMap the map of profile id - file pointer associations
     */
    public SpectrumContainer(String id, String dataFile, LinkedHashMap<Integer, Long> spectraMap) {

        this.id = id;

        this.spectraMap = spectraMap;
        fileManager = new FileManager(new File(dataFile));
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
     * Returns a spectrum by its identifier.
     *
     * @param spectrumId the profile identifier
     * @return the spectrum
     */
    public synchronized Spectrum getSpectrum(int spectrumId) {

        long spectrumIndex = -1;
        if (spectraMap.containsKey(spectrumId)) {
            spectrumIndex = spectraMap.get(spectrumId);
        }
        if (spectrumIndex == -1) return null;
        Spectrum spectrum = fileManager.read(spectrumIndex, PseudoSpectrum.class);

        return spectrum;
    }

    /**
     * Adds a spectrum to the collection.
     *
     * @param spectrum the profile
     */
    public void addSpectrum(Spectrum spectrum) {

        long spectrumIndex = fileManager.write(spectrum);
        spectraMap.put(spectrum.getIndex(), spectrumIndex);
    }

    /**
     * Returns the spectra indices.
     *
     * @return the spectra indices
     */
    public Map<Integer, Long> getSpectraNumbers() {
        return spectraMap;
    }

    public int size() {
        return spectraMap.size();
    }

    /**
     * Returns the currenct working directory.
     *
     * @return the working directory
     */
    public String getWorkingDirectory() {
        return fileManager.getWorkingDirectory();
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
     * Returns the actual data file.
     *
     * @return the dta file
     */
    public File getDataFile() {
        return fileManager.getDataFile();
    }

    /**
     * Closes the file.
     */
    public void finaliseFile() {
        fileManager.closeFile();
    }

    /**
     * Returns an iterator over a set of elements of type T.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<PseudoSpectrum> iterator() {
        return new SpectrumIterator(new ArrayList<Long>(spectraMap.values()), fileManager);
    }
}
