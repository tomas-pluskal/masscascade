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

package uk.ac.ebi.masscascade.reference;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.container.Container;
import uk.ac.ebi.masscascade.interfaces.container.ContainerBuilder;
import uk.ac.ebi.masscascade.parameters.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class ReferenceContainer implements Container, Iterable<Map.Entry<Double, List<ReferenceSpectrum>>> {

    private final Logger LOGGER = Logger.getLogger(ReferenceContainer.class);

    public static double NO_PRECURSOR = 0;

    private String id;
    private String source;
    private Constants.MSN msn;

    private Set<String> spectraIds;
    private TreeMap<Double, List<ReferenceSpectrum>> spectra;

    public ReferenceContainer(String id, String source, Constants.MSN msn) {

        this.id = id;
        this.msn = msn;
        this.source = source;

        spectraIds = new HashSet<>();
        spectra = new TreeMap<>();
    }

    @Override
    public String getId() {
        return id;
    }

    public String getSource() {
        return source;
    }

    public Constants.MSN getMsn() {
        return msn;
    }

    public void addSpectrum(ReferenceSpectrum spectrum) {

        if (spectraIds.contains(spectrum.getId())) {
            LOGGER.log(Level.WARN, "Duplicate identifier, cannot add spectrum: " + spectrum.getId());
            return;
        }

        spectraIds.add(spectrum.getId());
        double precursorMass = spectrum.getPrecursorMass();
        if (spectra.containsKey(precursorMass)) {
            spectra.get(precursorMass).add(spectrum);
        } else {
            List<ReferenceSpectrum> spectrumList = new ArrayList<>();
            spectrumList.add(spectrum);
            spectra.put(precursorMass, spectrumList);
        }
    }

    public List<ReferenceSpectrum> getSpectra(double precursorMass, double ppm) {

        double delta = precursorMass * ppm / Constants.PPM;
        SortedMap<Double, List<ReferenceSpectrum>> subSpectra =
                spectra.subMap(delta - precursorMass, true, precursorMass + delta, true);

        List<ReferenceSpectrum> matchingSpectra = new ArrayList<>();
        for (List<ReferenceSpectrum> referenceSpectra : subSpectra.values()) matchingSpectra.addAll(referenceSpectra);

        return matchingSpectra;
    }

    /**
     * Returns an iterator over a set of elements of type ReferenceSpectrum.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<Map.Entry<Double, List<ReferenceSpectrum>>> iterator() {
        return spectra.entrySet().iterator();
    }

    /**
     * Deletes all data from the container.
     *
     * @return if successful
     */
    @Override
    public boolean removeAll() {

        spectra.clear();
        spectraIds.clear();

        return true;
    }

    /**
     * Returns the size of the container
     *
     * @return the container's size
     */
    @Override
    public int size() {
        return spectraIds.size();
    }

    /**
     * Returns the actual data file.
     *
     * @return the dta file
     */
    @Override
    public File getDataFile() {
        throw new MassCascadeException("Method not implemented");
    }

    /**
     * Returns the current working directory.
     *
     * @return the working directory
     */
    @Override
    public String getWorkingDirectory() {
        throw new MassCascadeException("Method not implemented");
    }

    /**
     * Returns a {@link uk.ac.ebi.masscascade.interfaces.container.ContainerBuilder} for the data classes that extend
     * this class.
     *
     * @return The {@link uk.ac.ebi.masscascade.interfaces.container.ContainerBuilder} matching this {@link
     *         uk.ac.ebi.masscascade.interfaces.container.Container}
     */
    @Override
    public ContainerBuilder getBuilder() {
        throw new MassCascadeException("Method not implemented");
    }

    /**
     * Returns a profile iterator.
     *
     * @return the profile iterator
     */
    @Override
    public Iterable<Profile> profileIterator() {
        throw new MassCascadeException("Method not implemented");
    }
}
