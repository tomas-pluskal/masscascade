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
import uk.ac.ebi.masscascade.alignment.Obiwarp;
import uk.ac.ebi.masscascade.alignment.ObiwarpHelper;
import uk.ac.ebi.masscascade.background.NoiseReduction;
import uk.ac.ebi.masscascade.core.container.memory.MemoryContainerBuilder;
import uk.ac.ebi.masscascade.featurebuilder.SequentialFeatureBuilder;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.container.FeatureContainer;
import uk.ac.ebi.masscascade.interfaces.container.ScanContainer;
import uk.ac.ebi.masscascade.io.PsiMzmlReader;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;

import java.io.File;
import java.net.URL;

public class ObiwarpTest {

    @Test
    public void testObiwarp() {

        // get mzML resource
        URL url1 = this.getClass().getResource("/uk/ac/ebi/masscascade/data/Qc1.mzML");
        File file1 = new File(url1.getFile());
        URL url2 = this.getClass().getResource("/uk/ac/ebi/masscascade/data/Qc2.mzML");
        File file2 = new File(url2.getFile());

        // build the output data container
        ScanContainer container1 = MemoryContainerBuilder.getInstance().newInstance(ScanContainer.class, file1.getName());
        ScanContainer container2 = MemoryContainerBuilder.getInstance().newInstance(ScanContainer.class, file2.getName());

        // build the parameter map for the reader task
        ParameterMap params = new ParameterMap();
        params.put(Parameter.DATA_FILE, file1);
        params.put(Parameter.SCAN_CONTAINER, container1);

        // create and run the task to read the file
        CallableTask readerTask = new PsiMzmlReader(params);
        ScanContainer outContainer11 = (ScanContainer) readerTask.call();

        params = new ParameterMap();
        params.put(Parameter.DATA_FILE, file2);
        params.put(Parameter.SCAN_CONTAINER, container2);

        readerTask = new PsiMzmlReader(params);
        ScanContainer outContainer12 = (ScanContainer) readerTask.call();

        // build the parameter map for the noise reduction task
        params = new ParameterMap();
        params.put(Parameter.SCAN_WINDOW, 10);
        params.put(Parameter.MZ_WINDOW_PPM, 10d);
        params.put(Parameter.SCAN_CONTAINER, outContainer11);

        // create and run the task to reduce random noise
        CallableTask noiseTask = new NoiseReduction(params);
        ScanContainer outContainer21 = (ScanContainer) noiseTask.call();

        params = new ParameterMap();
        params.put(Parameter.SCAN_WINDOW, 10);
        params.put(Parameter.MZ_WINDOW_PPM, 10d);
        params.put(Parameter.SCAN_CONTAINER, outContainer12);

        noiseTask = new NoiseReduction(params);
        ScanContainer outContainer22 = (ScanContainer) noiseTask.call();

        // build the parameter map for the mass chromatogram task
        params = new ParameterMap();
        params.put(Parameter.MZ_WINDOW_PPM, 10d);
        params.put(Parameter.MIN_FEATURE_INTENSITY, 10000d);
        params.put(Parameter.MIN_FEATURE_WIDTH, 1);
        params.put(Parameter.SCAN_CONTAINER, outContainer21);

        // create and run the task to extract mass chromatograms
        CallableTask chromatogramTask = new SequentialFeatureBuilder(params);
        FeatureContainer outContainer31 = (FeatureContainer) chromatogramTask.call();

        params = new ParameterMap();
        params.put(Parameter.MZ_WINDOW_PPM, 10d);
        params.put(Parameter.MIN_FEATURE_INTENSITY, 10000d);
        params.put(Parameter.MIN_FEATURE_WIDTH, 1);
        params.put(Parameter.SCAN_CONTAINER, outContainer22);

        chromatogramTask = new SequentialFeatureBuilder(params);
        FeatureContainer outContainer32 = (FeatureContainer) chromatogramTask.call();

        ObiwarpHelper obiHelper =
                new ObiwarpHelper(0.25, new ExtendableRange(0, 1000), 1.5, new ExtendableRange(0, 1000));
        File refFile = obiHelper.buildLmataFile(outContainer31);

        params = new ParameterMap();
        params.put(Parameter.REFERENCE_FILE, refFile);
        params.put(Parameter.TIME_WINDOW, 1d);
        params.put(Parameter.FEATURE_CONTAINER, outContainer32);
        params.put(Parameter.EXECUTABLE, "C:/Users/stephan/Mass Spectrometry/Obiwarp/obiwarp.exe");
        params.put(Parameter.GAP_INIT, 0.1);
        params.put(Parameter.GAP_EXTEND, 0.5);
        params.put(Parameter.RESPONSE, 100d);
        params.put(Parameter.BIN_WIDTH_MZ, 0.25);
        params.put(Parameter.MZ_RANGE, new ExtendableRange(0, 1000));
        params.put(Parameter.BIN_WIDTH_RT, 1.5);
        params.put(Parameter.TIME_RANGE, new ExtendableRange(0, 1000));

        CallableTask obiwarpTask = new Obiwarp(params);
        FeatureContainer outContainer42 = (FeatureContainer) obiwarpTask.call();

        refFile.delete();
    }
}
