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

package uk.ac.ebi.masscascade.core.profile;

import uk.ac.ebi.masscascade.core.FileManager;
import uk.ac.ebi.masscascade.interfaces.Profile;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Class implementing an iterator for profiles, directly reading from the stored data file.
 */
public class ProfileIterator implements Iterator<Profile> {

    private final List<Long> profilePointers;
    private final FileManager fileManager;
    private int currentPosition;

    /**
     * Constructor for a profile iterator.
     *
     * @param profilePointers the profile pointers for the data file
     * @param fileManager     the file manager handling the data file
     */
    public ProfileIterator(List<Long> profilePointers, FileManager fileManager) {

        this.profilePointers = profilePointers;
        this.fileManager = fileManager;

        currentPosition = 0;
    }

    /**
     * Returns <tt>true</tt> if the iteration has more elements. (In other
     * words, returns <tt>true</tt> if <tt>next</tt> would return an element
     * rather than throwing an exception.)
     *
     * @return <tt>true</tt> if the iterator has more elements.
     */
    @Override
    public boolean hasNext() {

        return currentPosition < profilePointers.size();
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration.
     * @throws java.util.NoSuchElementException
     *          iteration has no more elements.
     */
    @Override
    public Profile next() {

        Profile profile = fileManager.read(profilePointers.get(currentPosition), ProfileImpl.class);

        currentPosition++;
        return profile;
    }

    /**
     * Removes from the underlying collection the last element returned by the
     * iterator (optional operation).  This method can be called only once per
     * call to <tt>next</tt>.  The behavior of an iterator is unspecified if
     * the underlying collection is modified while the iteration is in
     * progress in any way other than by calling this method.
     *
     * @throws UnsupportedOperationException if the <tt>remove</tt>
     *                                       operation is not supported by this Iterator.
     * @throws IllegalStateException         if the <tt>next</tt> method has not
     *                                       yet been called, or the <tt>remove</tt> method has already
     *                                       been called after the last call to the <tt>next</tt>
     *                                       method.
     */
    @Override
    public void remove() {

        // do nothing
    }
}
