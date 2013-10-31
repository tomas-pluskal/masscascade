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

import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.Feature;
import uk.ac.ebi.masscascade.interfaces.container.Container;
import uk.ac.ebi.masscascade.interfaces.container.ContainerBuilder;
import uk.ac.ebi.masscascade.parameters.Constants;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Reference container for library spectra.
 */
public class ReferenceContainer implements Container, Iterable<ReferenceSpectrum>, Serializable {

    private static final long serialVersionUID = 7253134509357859535L;
    public static double NO_PRECURSOR = 0;

    private String id;
    private String source;
    private Constants.MSN msn;

    private Set<String> spectraIds;
    private TreeMap<Double, List<ReferenceSpectrum>> spectra;

    /**
     * Constructs a reference container.
     *
     * @param id     the container identifier
     * @param source the source of the reference spectra
     * @param msn    the MSn level of the spectra
     */
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

    /**
     * Returns the source of this container.
     *
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * Returns the MSn level of this container.
     *
     * @return the MSn level
     */
    public Constants.MSN getMsn() {
        return msn;
    }

    /**
     * Adds a reference featureset to the container. The featureset is only added if the featureset id is not already present
     * in the container.
     *
     * @param spectrum the reference featureset
     */
    public void addSpectrum(ReferenceSpectrum spectrum) {

        if (spectraIds.contains(spectrum.getId())) return;

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
    public Iterator<ReferenceSpectrum> iterator() {
        return spectra.get(0d).iterator();
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
     * Returns a feature iterator.
     *
     * @return the feature iterator
     */
    @Override
    public Iterable<Feature> featureIterator() {
        throw new MassCascadeException("Method not implemented");
    }
}
