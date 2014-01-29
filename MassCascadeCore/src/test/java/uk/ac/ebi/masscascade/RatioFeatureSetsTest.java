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
package uk.ac.ebi.masscascade;

import org.junit.Test;
import uk.ac.ebi.masscascade.alignment.ratiosets.RatioFeatureSets;
import uk.ac.ebi.masscascade.commons.FileLoader;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.container.FeatureSetContainer;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;

import java.util.ArrayList;

public class RatioFeatureSetsTest {

    @Test
    public void testRatioFeatureSets() throws Exception {

        ParameterMap params = new ParameterMap();
        ArrayList<FeatureSetContainer> featureSetContainers = new ArrayList<>();
        featureSetContainers.add(FileLoader.getSpectrumContainer(FileLoader.TESTFILE.QC1));
        featureSetContainers.add(FileLoader.getSpectrumContainer(FileLoader.TESTFILE.QC2));
        params.put(Parameter.FEATURE_SET_LIST, featureSetContainers);

        CallableTask task = new RatioFeatureSets(params);
        FeatureSetContainer container = (FeatureSetContainer) task.call();
    }
}
