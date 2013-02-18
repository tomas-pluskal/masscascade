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

package uk.ac.ebi.masscascade.interfaces.container;

import uk.ac.ebi.masscascade.interfaces.Spectrum;

import java.util.Iterator;

/**
 * This is a spectrum container holding spectrum data.
 */
public interface SpectrumContainer extends Container, Iterable<Spectrum> {

    /**
     * Returns a spectrum by its identifier.
     *
     * @param spectrumId the profile identifier
     * @return the spectrum
     */
    Spectrum getSpectrum(int spectrumId);

    /**
     * Adds a spectrum to the collection.
     *
     * @param spectrum the profile
     */
    void addSpectrum(Spectrum spectrum);

    /**
     * Closes the file.
     */
    void finaliseFile();

    /**
     * Returns an iterator over a set of elements of type T.
     *
     * @return an Iterator.
     */
    Iterator<Spectrum> iterator();
}
