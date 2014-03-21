/*
 * Copyright (C) 2014 EMBL - European Bioinformatics Institute
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
package uk.ac.ebi.masscascade;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.junit.Test;
import uk.ac.ebi.masscascade.alignment.featurebins.FeatureBinGenerator;
import uk.ac.ebi.masscascade.commons.FileLoader;
import uk.ac.ebi.masscascade.interfaces.container.Container;
import uk.ac.ebi.masscascade.interfaces.container.FeatureSetContainer;

public class FeatureBinTest {

    @Test
    public void testTasks() {

        FeatureSetContainer c1 = FileLoader.getSpectrumContainer(FileLoader.TESTFILE.QC1);
        FeatureSetContainer c2 = FileLoader.getSpectrumContainer(FileLoader.TESTFILE.QC2);

        System.out.println(c1.size());
        System.out.println(c2.size());

        Multimap<Integer, Container> containerMap = HashMultimap.create();
        containerMap.put(1, c1);
        containerMap.put(1, c2);

        HashMultimap<Integer, Integer> cToF = FeatureBinGenerator.createContainerToFeatureMap(containerMap, 10, 5, 0);

        System.out.println(cToF.size());
    }
}
