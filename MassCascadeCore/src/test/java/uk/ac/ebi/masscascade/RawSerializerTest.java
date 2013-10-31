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
import org.xmlcml.cml.element.CMLCml;
import uk.ac.ebi.masscascade.core.container.file.FileContainerBuilder;
import uk.ac.ebi.masscascade.core.container.file.scan.FileScanContainer;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.container.Container;
import uk.ac.ebi.masscascade.interfaces.container.ScanContainer;
import uk.ac.ebi.masscascade.io.PsiMzmlReader;
import uk.ac.ebi.masscascade.io.cml.ScanDeserializer;
import uk.ac.ebi.masscascade.io.cml.ScanSerializer;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class RawSerializerTest {

    @Test
    public void testSerialisation() throws IOException, XMLStreamException {

        // get mzML resource
        URL url = this.getClass().getResource("/uk/ac/ebi/masscascade/data/Sample.mzML");
        File file = new File(url.getFile());

        // build the file-based output data container
        Container container = FileContainerBuilder.getInstance().newInstance(ScanContainer.class, file.getName(),
                System.getProperty(Constants.JAVA_TMP));

        // build the parameter map for the reader task
        ParameterMap params = new ParameterMap();
        params.put(Parameter.DATA_FILE, file);
        params.put(Parameter.SCAN_CONTAINER, container);

        // create and run the task to read the file
        CallableTask readerTask = new PsiMzmlReader(params);
        ScanContainer outContainer = (ScanContainer) readerTask.call();

        // serialise the container to CML
        ScanSerializer cmlSerializer = new ScanSerializer((FileScanContainer) outContainer);
        CMLCml cml = cmlSerializer.getCml();

        // deserialise the CML and create a new data container
        ScanDeserializer cmlDeSerializer = new ScanDeserializer(cml.toXML(), System.getProperty(Constants.JAVA_TMP));
        outContainer = cmlDeSerializer.getFile();

        // assert that no values have changed
        Assert.assertEquals(2, outContainer.getScanLevels().size());
        Assert.assertEquals(Constants.ION_MODE.NEGATIVE, outContainer.getScanLevels().get(0).getIonMode());
        Assert.assertEquals(Constants.ION_MODE.NEGATIVE, outContainer.getScanLevels().get(1).getIonMode());
        Assert.assertEquals(100.1023941040039, outContainer.getScanLevels().get(1).getMzRange().getLowerBounds());
        Assert.assertEquals(236.0926055908203, outContainer.getScanLevels().get(1).getMzRange().getUpperBounds());
        Assert.assertEquals(502, outContainer.getTicChromatogram(Constants.MSN.MS1).getData().size());
        Assert.assertEquals(99961.515625, outContainer.getBasePeakChromatogram().getData().getFirst().y);

        // delete the data file of the container
        Assert.assertTrue(outContainer.getDataFile().delete());
    }
}
