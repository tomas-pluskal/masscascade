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
import uk.ac.ebi.masscascade.core.container.memory.MemoryContainerBuilder;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.container.ScanContainer;
import uk.ac.ebi.masscascade.io.NetCDFReader;
import uk.ac.ebi.masscascade.io.PsiMzmlReader;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;

import java.io.File;
import java.net.URL;

public class netCDFReaderTest {

    @Test
    public void testPsiReader() {

        URL url = FileLoader.class.getResource(FileLoader.TESTFILE.CDF.getPath());
        File file = new File(url.getFile());

        ScanContainer container = MemoryContainerBuilder.getInstance().newInstance(ScanContainer.class, file.getName());

        ParameterMap params = new ParameterMap();
        params.put(Parameter.DATA_FILE, file);
        params.put(Parameter.SCAN_CONTAINER, container);

        CallableTask task = new NetCDFReader(params);
        task.call();
    }
}
