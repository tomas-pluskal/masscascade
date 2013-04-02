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

import uk.ac.ebi.masscascade.interfaces.container.Container;

/**
 * A helper class to instantiate a <code> MemoryContainer </code> instance for a specific implementation.
 */
public interface ContainerBuilder {

    /**
     * Creates a new instance of a <code> MemoryContainer </code>, using the constructor defined by the given parameters.
     *
     * @param <T>            Class of an interface extending <code> MemoryContainer </code>, or <code> MemoryContainer </code>,
     *                       itself.
     * @param containerClass Interface class to instantiate an instance for.
     * @param params         Parameters passed to the constructor of the created instance.
     * @return Instance created.
     * @throws IllegalArgumentException Exception thrown when the <code> ContainerBuilder </code> cannot instantiate
     *                                  the <code> containerClass </code> with the given parameters.
     */
    public <T extends Container> T newInstance(Class<T> containerClass,
            Object... params) throws IllegalArgumentException;
}
