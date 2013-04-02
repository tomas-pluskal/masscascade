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

package uk.ac.ebi.masscascade.ws.chemspider;

import org.junit.Test;
import uk.ac.ebi.masscascade.ws.massbank.MassBankAPIStub;

/**
 * Tests the MassBank web service.
 */
public class MassBankTest {

    /**
     * Tests the synchronous MassBank web service. The MassBank database is queried for the instrument types available.
     *
     * @throws Exception if the web service cannot retrieve the result
     */
    @Test
    public void testAvailableInstruments() throws Exception {

        MassBankAPIStub mbStub = new MassBankAPIStub();
        MassBankAPIStub.GetInstrumentTypesResponse instrumentTypesResponse = mbStub.getInstrumentTypes();
        String[] insts = instrumentTypesResponse.get_return();
        for (String inst : insts) System.out.println(inst);
    }
}
