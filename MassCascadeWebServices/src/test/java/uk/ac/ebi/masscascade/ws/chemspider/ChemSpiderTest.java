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

import java.util.Map;

/**
 * Tests the ChemSpider web service. ChemSpider requires users to provide a token for certain web services which must
 * be
 * provided for the test to work.
 */
public class ChemSpiderTest {

    /**
     * The ChemSpider token.
     */
    private static final String TOKEN = "";

    /**
     * Tests the asynchronous ChemSpider web service. The <code> SearchByMass </code> service is used to retrieve
     * results for a molecular mass. The methods requires also a set of database names to be used for the query and an
     * absolute mass tolerance.
     */
    @Test
    public void testMassAsync() {

        ChemSpiderWrapper wrapper = new ChemSpiderWrapper();

        String[] databases = new String[]{"ChEMBL", "Human Metabolome Database", "NIST", "NMRShiftDB",
                                          "SDBS Spectral Database for Organic ", "Compounds",
                                          "SMPDB Small Molecule Pathway Database", "WikiPathways", "ZINC", "ChEBI",
                                          "PubChem"};

        String result = wrapper.getMassSpecAPISearchByMassAsyncResults(100.0, 0.001, databases, TOKEN);
        System.out.println(result);
        int[] csids = wrapper.getSearchGetAsyncSearchResultResults(result, TOKEN);

        if (csids == null) {
            System.out.println("Empty");
            return;
        }

        Map<Integer, Map<String, String>> csidMap =
                wrapper.getMassSpecAPIGetExtendedCompoundInfoArrayResults(csids, TOKEN);

        for (int csid : csidMap.keySet()) System.out.println(csid + " " + csidMap.get(csid).get("CommonName"));
    }
}
