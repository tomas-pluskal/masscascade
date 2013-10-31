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

package uk.ac.ebi.masscascade.core.container.memory;

import uk.ac.ebi.masscascade.core.ContainerFactory;
import uk.ac.ebi.masscascade.core.container.memory.feature.MemoryFeatureContainer;
import uk.ac.ebi.masscascade.core.container.memory.featureset.MemoryFeatureSetContainer;
import uk.ac.ebi.masscascade.core.container.memory.scan.MemoryScanContainer;
import uk.ac.ebi.masscascade.interfaces.container.*;
import uk.ac.ebi.masscascade.interfaces.container.FeatureContainer;

import java.util.Arrays;

/**
 * A factory class to provide implementation independent <code> MemoryContainer </code>.
 */
public class MemoryContainerBuilder implements ContainerBuilder {

    private static ContainerBuilder instance = null;
    private final ContainerFactory factory = new ContainerFactory(10);

    private MemoryContainerBuilder() {

        factory.register(ScanContainer.class, MemoryScanContainer.class);
        factory.register(FeatureContainer.class, MemoryFeatureContainer.class);
        factory.register(FeatureSetContainer.class, MemoryFeatureSetContainer.class);
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

        // hack to remove the working directory from the parameter array
        if (params.length == 3 && params[2] instanceof String) {
            params = Arrays.copyOf(params, params.length - 1);
        }
        return factory.ofClass(containerClass, params);
    }
}