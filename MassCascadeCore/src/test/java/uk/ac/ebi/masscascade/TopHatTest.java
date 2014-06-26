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

import junit.framework.Assert;
import org.junit.Test;
import uk.ac.ebi.masscascade.background.BaselineSubtraction;
import uk.ac.ebi.masscascade.commons.FileLoader;
import uk.ac.ebi.masscascade.interfaces.Feature;
import uk.ac.ebi.masscascade.interfaces.container.FeatureContainer;
import uk.ac.ebi.masscascade.interfaces.container.FeatureSetContainer;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;
import uk.ac.ebi.masscascade.utilities.xyz.XYZPoint;

public class TopHatTest {

    @Test
    public void testTasks() {

        FeatureContainer container = FileLoader.getProfileContainer(FileLoader.TESTFILE.SAMPLE);

        ParameterMap params = new ParameterMap();
        params.put(Parameter.SCAN_WINDOW, 16);
        params.put(Parameter.FEATURE_CONTAINER, container);

//        for (Feature f : container.featureIterator()) {
//            System.out.println(f.getId() + " " + f.getMzData().size());
//        }

//        for (int i = 0; i < 95; i++) {
//            String s = "";
//            boolean tooShort = false;
//            for (Feature f : container.featureIterator()) {
//                if (f.getData().size() != 95) {
//                    continue;
//                }
//                s += f.getData().get(i).z + ",";
//            }
//            System.out.println(s);
//        }

//        for (XYZPoint p : container.getFeature(72).getData()) {
//            System.out.println(p.z);
//        }

        BaselineSubtraction bs = new BaselineSubtraction(params);
        FeatureContainer bsContainer = bs.call();

//        for (XYZPoint p : bsContainer.getFeature(72).getData()) {
//            System.out.println(p.x);
//        }
    }
}
