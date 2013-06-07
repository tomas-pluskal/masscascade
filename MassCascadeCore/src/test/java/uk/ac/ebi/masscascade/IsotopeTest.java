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
import uk.ac.ebi.masscascade.commons.FileLoader;
import uk.ac.ebi.masscascade.core.PropertyManager;
import uk.ac.ebi.masscascade.identification.IsotopeFinder;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.container.SpectrumContainer;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;

public class IsotopeTest {

    @Test
    public void testFinder() throws Exception {

        ParameterMap params = new ParameterMap();
        params.put(Parameter.MZ_WINDOW_PPM, 20);
        params.put(Parameter.SPECTRUM_CONTAINER, FileLoader.getSpectrumContainer(FileLoader.TESTFILE.QC2));

        CallableTask task = new IsotopeFinder(params);
        SpectrumContainer container = (SpectrumContainer) task.call();

        Assert.assertTrue(container.getSpectrum(3).getProfile(245).hasProperty(PropertyManager.TYPE.Isotope));
        Assert.assertTrue(container.getSpectrum(3).getProfile(246).hasProperty(PropertyManager.TYPE.Isotope));
    }
}