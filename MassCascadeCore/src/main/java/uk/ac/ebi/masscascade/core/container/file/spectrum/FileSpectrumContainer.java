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

package uk.ac.ebi.masscascade.core.container.file.spectrum;

import uk.ac.ebi.masscascade.core.container.file.FileContainer;
import uk.ac.ebi.masscascade.core.container.file.FileManager;
import uk.ac.ebi.masscascade.core.profile.ProfileIterator;
import uk.ac.ebi.masscascade.core.spectrum.PseudoSpectrum;
import uk.ac.ebi.masscascade.core.spectrum.SpectrumIterator;
import uk.ac.ebi.masscascade.core.spectrum.SpectrumProfileIterator;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.Spectrum;
import uk.ac.ebi.masscascade.interfaces.container.SpectrumContainer;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Class containing a collection of spectra.
 */
public class FileSpectrumContainer extends FileContainer implements SpectrumContainer {

    private final String id;
    private final List<XYPoint> basePeaks;
    private final LinkedHashMap<Integer, Long> spectraMap;
    private final FileManager fileManager;

    /**
     * Constructs an empty spectra file.
     *
     * @param id the file identifier
     */
    public FileSpectrumContainer(String id) {
        this(id, System.getProperty("java.io.tmpdir"));
    }

    /**
     * Constructs an empty spectra file.
     *
     * @param id               the file identifier
     * @param workingDirectory the working directory
     */
    public FileSpectrumContainer(String id, String workingDirectory) {

        this.id = id;
        spectraMap = new LinkedHashMap<>();
        basePeaks = new ArrayList<>();

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
    public FileSpectrumContainer(String id, String dataFile, Collection<Spectrum> spectrumSet) {

        this.id = id;
        spectraMap = new LinkedHashMap<>();
        basePeaks = new ArrayList<>();

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
    public FileSpectrumContainer(String id, String dataFile, LinkedHashMap<Integer, Long> spectraMap,
            List<XYPoint> basePeaks) {

        this.id = id;

        this.spectraMap = spectraMap;
        this.basePeaks = basePeaks;
        fileManager = new FileManager(new File(dataFile));
    }

    /**
     * Returns the spectra indices.
     *
     * @return the spectra indices
     */
    public Map<Integer, Long> getSpectraNumbers() {
        return spectraMap;
    }

    /**
     * Adds a spectrum to the collection.
     *
     * @param spectrum the profile
     */
    @Override
    public void addSpectrum(Spectrum spectrum) {

        long spectrumIndex = fileManager.write(spectrum);
        spectraMap.put(spectrum.getIndex(), spectrumIndex);
        basePeaks.add(new XYPoint(spectrum.getRetentionTime(), spectrum.getBasePeak().get(0).x));
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
     * Returns a spectrum by its identifier.
     *
     * @param spectrumId the profile identifier
     * @return the spectrum
     */
    @Override
    public synchronized Spectrum getSpectrum(int spectrumId) {

        long spectrumIndex = -1;
        if (spectraMap.containsKey(spectrumId)) spectrumIndex = spectraMap.get(spectrumId);
        if (spectrumIndex == -1) return null;
        Spectrum spectrum = fileManager.read(spectrumIndex, PseudoSpectrum.class);

        return spectrum;
    }

    /**
     * Returns the size of the container.
     *
     * @return the container size
     */
    @Override
    public int size() {
        return spectraMap.size();
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
    public Iterator<Spectrum> iterator() {
        return new SpectrumIterator(new ArrayList<Long>(spectraMap.values()), fileManager);
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
     * Returns a profile iterator.
     *
     * @return the profile iterator
     */
    @Override
    public Iterable<Profile> profileIterator() {

        return new Iterable<Profile>() {

            public Iterator<Profile> iterator() {
                return new SpectrumProfileIterator(new ArrayList<>(spectraMap.values()), fileManager);
            }
        };
    }
}
