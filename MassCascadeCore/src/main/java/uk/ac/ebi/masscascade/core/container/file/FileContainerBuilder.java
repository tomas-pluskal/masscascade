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

package uk.ac.ebi.masscascade.core.container.file;

import uk.ac.ebi.masscascade.core.ContainerFactory;
import uk.ac.ebi.masscascade.core.container.file.feature.FileFeatureContainer;
import uk.ac.ebi.masscascade.core.container.file.scan.FileScanContainer;
import uk.ac.ebi.masscascade.core.container.file.featureset.FileFeatureSetContainer;
import uk.ac.ebi.masscascade.interfaces.container.*;
import uk.ac.ebi.masscascade.interfaces.container.FeatureContainer;

/**
 * A factory class to provide implementation independent <code> FileContainer </code>.
 */
public class FileContainerBuilder implements ContainerBuilder {

    private static ContainerBuilder instance = null;
    private final ContainerFactory factory = new ContainerFactory(10);

    private FileContainerBuilder() {

        factory.register(ScanContainer.class, FileScanContainer.class);
        factory.register(FeatureContainer.class, FileFeatureContainer.class);
        factory.register(FeatureSetContainer.class, FileFeatureSetContainer.class);
    }

    /**
     * Access the singleton instance of this FileContainerBuilder.
     *
     * @return a FileContainerBuilder instance
     */
    public static ContainerBuilder getInstance() {
        return (instance == null) ? instance = new FileContainerBuilder() : instance;
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
