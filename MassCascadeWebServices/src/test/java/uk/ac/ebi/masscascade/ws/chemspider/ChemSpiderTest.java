/*
 * Copyright (c) 2012, Stephan Beisken. All rights reserved.
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
package uk.ac.ebi.masscascade.ws.chemspider;

import org.junit.Test;

import java.util.Map;

public class ChemSpiderTest {

    private static final String token = "myChemSpiderToken";

    @Test
    public void testMassAsync() {

        ChemSpiderWrapper wrapper = new ChemSpiderWrapper();
        String[] databases = wrapper.getMassSpecAPIGetDatabasesResults();

        String result = wrapper.getMassSpecAPISearchByMassAsyncResults(100.0, 0.01, databases, token);
        String status = wrapper.getSearchGetAsyncSearchStatusResults(result, token);
        int[] csids = wrapper.getSearchGetAsyncSearchResultResults(result, token);
        Map<Integer, Map<String, String>> csidMap =
                wrapper.getMassSpecAPIGetExtendedCompoundInfoArrayResults(csids, token);
        for (int csid : csidMap.keySet()) System.out.println(csid + " " + csidMap.get(csid).get("CommonName"));
    }
}
