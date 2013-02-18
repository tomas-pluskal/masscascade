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

package uk.ac.ebi.masscascade.core.memory;

import uk.ac.ebi.masscascade.core.ContainerFactory;
import uk.ac.ebi.masscascade.core.memory.profile.MemoryProfileContainer;
import uk.ac.ebi.masscascade.core.memory.raw.MemoryRawContainer;
import uk.ac.ebi.masscascade.core.memory.spectrum.MemorySpectrumContainer;
import uk.ac.ebi.masscascade.interfaces.container.Container;
import uk.ac.ebi.masscascade.interfaces.container.ContainerBuilder;
import uk.ac.ebi.masscascade.interfaces.container.ProfileContainer;
import uk.ac.ebi.masscascade.interfaces.container.RawContainer;
import uk.ac.ebi.masscascade.interfaces.container.SpectrumContainer;

public class MemoryContainerBuilder implements ContainerBuilder {

    private static ContainerBuilder instance = null;
    private final ContainerFactory factory = new ContainerFactory(10);

    private MemoryContainerBuilder() {

        factory.register(RawContainer.class, MemoryRawContainer.class);
        factory.register(ProfileContainer.class, MemoryProfileContainer.class);
        factory.register(SpectrumContainer.class, MemorySpectrumContainer.class);
    }

    /**
     * Access the singleton instance of this FileContainerBuilder.
     *
     * @return a FileContainerBuilder instance
     */
    public static ContainerBuilder getInstance() {
        return (instance == null) ? instance = new MemoryContainerBuilder() : instance;
    }

    /**
     * @inheritDoc
     */
    @Override
    public <T extends Container> T newInstance(Class<T> containerClass,
            Object... params) throws IllegalArgumentException {
        return factory.ofClass(containerClass, params);
    }
}