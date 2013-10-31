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
import uk.ac.ebi.masscascade.interfaces.container.ScanContainer;
import uk.ac.ebi.masscascade.parameters.Constants;

public class PsiMzmlReaderTest {

    @Test
    public void testPsiReader() {

        ScanContainer container = FileLoader.getRawContainer(FileLoader.TESTFILE.SAMPLE);

        Assert.assertEquals(2, container.size());
        Assert.assertEquals(2, container.getScanLevels().size());
        Assert.assertEquals(102, (int) container.getScan(1).getBasePeak().get(0).x);
        Assert.assertEquals(Constants.ION_MODE.NEGATIVE, container.getScan(5).getIonMode());
    }
}
