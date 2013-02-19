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

package uk.ac.ebi.masscascade;

import junit.framework.Assert;
import org.junit.Test;
import uk.ac.ebi.masscascade.background.NoiseReduction;
import uk.ac.ebi.masscascade.core.container.memory.MemoryContainerBuilder;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.container.ProfileContainer;
import uk.ac.ebi.masscascade.interfaces.container.RawContainer;
import uk.ac.ebi.masscascade.io.PsiMzmlReader;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.tracebuilder.ProfileBuilder;

import java.io.File;
import java.net.URL;

public class ProfileBuilderTest {

    @Test
    public void testTasks() {

        // get mzML resource
        URL url = this.getClass().getResource("/uk/ac/ebi/masscascade/data/Sample.mzML");
        File file = new File(url.getFile());

        // build the output data container
        RawContainer container = MemoryContainerBuilder.getInstance().newInstance(RawContainer.class, file.getName());

        // build the parameter map for the reader task
        ParameterMap params = new ParameterMap();
        params.put(Parameter.DATA_FILE, file);
        params.put(Parameter.RAW_CONTAINER, container);

        // create and run the task to read the file
        CallableTask readerTask = new PsiMzmlReader(params);
        RawContainer outContainer1 = (RawContainer) readerTask.call();

        // build the parameter map for the noise reduction task
        params = new ParameterMap();
        params.put(Parameter.SCAN_WINDOW, 10);
        params.put(Parameter.MZ_WINDOW_PPM, 10d);
        params.put(Parameter.RAW_CONTAINER, outContainer1);

        // create and run the task to reduce random noise
        CallableTask noiseTask = new NoiseReduction(params);
        RawContainer outContainer2 = (RawContainer) noiseTask.call();

        // build the parameter map for the mass chromatogram task
        params = new ParameterMap();
        params.put(Parameter.MZ_WINDOW_PPM, 10d);
        params.put(Parameter.MIN_PROFILE_INTENSITY, 1000d);
        params.put(Parameter.MIN_PROFILE_WIDTH, 1);
        params.put(Parameter.RAW_CONTAINER, outContainer2);

        // create and run the task to extract mass chromatograms
        CallableTask chromatogramTask = new ProfileBuilder(params);
        ProfileContainer outContainer3 = (ProfileContainer) chromatogramTask.call();

        Assert.assertEquals(1504, outContainer3.size());
    }
}
