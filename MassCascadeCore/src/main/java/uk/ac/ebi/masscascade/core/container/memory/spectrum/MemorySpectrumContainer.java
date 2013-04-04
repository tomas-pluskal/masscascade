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

package uk.ac.ebi.masscascade.core.container.memory.spectrum;

import uk.ac.ebi.masscascade.core.container.file.FileManager;
import uk.ac.ebi.masscascade.core.container.memory.MemoryContainer;
import uk.ac.ebi.masscascade.core.spectrum.PseudoSpectrum;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.Spectrum;
import uk.ac.ebi.masscascade.interfaces.container.SpectrumContainer;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Class containing a collection of spectra.
 */
public class MemorySpectrumContainer extends MemoryContainer implements SpectrumContainer {

    private final String id;
    private final List<XYPoint> basePeaks;
    private final LinkedHashMap<Integer, Spectrum> spectraMap;

    /**
     * Constructs an empty spectra file.
     *
     * @param id the file identifier
     */
    public MemorySpectrumContainer(String id) {

        this.id = id;
        basePeaks = new ArrayList<XYPoint>();
        spectraMap = new LinkedHashMap<Integer, Spectrum>();
    }

    /**
     * Constructs a populated spectra file.
     *
     * @param id          the file identifier
     * @param spectrumSet the collection of spectra
     */
    public MemorySpectrumContainer(String id, Collection<Spectrum> spectrumSet) {

        this.id = id;
        basePeaks = new ArrayList<XYPoint>();
        spectraMap = new LinkedHashMap<Integer, Spectrum>();

        for (Spectrum spectrum : spectrumSet)
            addSpectrum(spectrum);
    }

    /**
     * Constructs a populated spectra file.
     *
     * @param id         the file identifier
     * @param spectraMap the map of profile id - file pointer associations
     */
    public MemorySpectrumContainer(String id, LinkedHashMap<Integer, Spectrum> spectraMap) {

        this.id = id;
        this.spectraMap = spectraMap;
        basePeaks = new ArrayList<XYPoint>();
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
    public Spectrum getSpectrum(int spectrumId) {
        return (spectraMap.containsKey(spectrumId)) ? spectraMap.get(spectrumId) : null;
    }

    /**
     * Adds a spectrum to the collection.
     *
     * @param spectrum the profile
     */
    public void addSpectrum(Spectrum spectrum) {
        spectraMap.put(spectrum.getIndex(), spectrum);
        basePeaks.add(new XYPoint(spectrum.getRetentionTime(), spectrum.getBasePeak().get(0).x));
    }

    public int size() {
        return spectraMap.size();
    }

    /**
     * Deletes all data from the container.
     *
     * @return if successful
     */
    public boolean removeAll() {
        spectraMap.clear();
        basePeaks.clear();
        ;
        return (spectraMap.size() == 0);
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
    public Iterator<Spectrum> iterator() {
        return spectraMap.values().iterator();
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
                return new SpectrumProfileIterator(spectraMap.values().iterator());
            }
        };
    }
}

class SpectrumProfileIterator implements Iterator<Profile> {

    private Iterator<Spectrum> spectrumIterator;
    private Iterator<Profile> profileIterator;
    private Spectrum cachedSpectrum;

    public SpectrumProfileIterator(Iterator<Spectrum> spectrumIterator) {
        this.spectrumIterator = spectrumIterator;

        cachedSpectrum = spectrumIterator.next();
        profileIterator = cachedSpectrum.iterator();
    }

    @Override
    public boolean hasNext() {
        return profileIterator.hasNext() || spectrumIterator.hasNext();
    }

    @Override
    public Profile next() {

        if (!profileIterator.hasNext()) {
            cachedSpectrum = spectrumIterator.next();
            profileIterator = cachedSpectrum.iterator();
        }

        return profileIterator.next();
    }

    @Override
    public void remove() {
        // do nothing
    }
}
