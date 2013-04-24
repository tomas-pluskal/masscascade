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
import uk.ac.ebi.masscascade.parameters.Constants;
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

    @Test
    public void testSpectrumSearch() throws Exception {

        String[] mzs =
                new String[]{"39.022", "41.038", "41.039", "42.034", "43.041", "54.034", "55.042", "56.049", "56.05",
                             "66.032", "66.033", "67.04", "68.048", "68.049", "69.044", "81.044", "82.052", "83.059",
                             "93.044", "95.06", "110.07", "111.054", "112.086", "138.065", "156.074"};
        String[] ints =
                new String[]{"16", "38", "12", "11", "7", "169", "104", "999", "9", "166", "34", "16", "86", "13", "41",
                             "469", "695", "999", "606", "112", "999", "16", "2", "2", "999"};
        String[] instruments =
                new String[]{"LC-ESI-IT", "LC-ESI-ITFT", "LC-ESI-ITTOF", "LC-ESI-Q", "LC-ESI-QIT", "LC-ESI-QQ",
                             "LC-ESI-QTOF", "LC-ESI-TOF"};
        int cutoff = 50;
        String ionMode = "positive";
        double ppm = 10;
        int maxNumOfResults = 10;

        MassBankAPIStub mbStub = new MassBankAPIStub();
        MassBankAPIStub.SearchSpectrum sp = new MassBankAPIStub.SearchSpectrum();
        sp.setCutoff("" + cutoff);
        sp.setInstrumentTypes(instruments);
        sp.setIntensities(ints);
        sp.setMzs(mzs);
        sp.setIonMode(ionMode);
        sp.setTolerance("" + ppm);
        sp.setMaxNumResults(maxNumOfResults);
        sp.setUnit("ppm");

        MassBankAPIStub.SearchSpectrumResponse ssr = mbStub.searchSpectrum(sp);
        MassBankAPIStub.Result[] results = ssr.get_return().getResults();

        if (results != null) {
            for (MassBankAPIStub.Result result : results) {
                String id = result.getId();
                String title = result.getTitle();
                double mass = Double.parseDouble(result.getExactMass());
                double score = Double.parseDouble(result.getScore());

                mass = mass + Constants.PARTICLES.PROTON.getMass();

                System.out.println(id + " " + title + " " + mass + " " + score);
            }
        } else {
            System.out.println("Null");
        }
    }

    @Test
    public void testSpectrumBatchSearch() throws Exception {

        // histidine and tyrosine
        String[] queries = new String[]{
                "Name: Histidine;39.022,16;41.038,38;41.039,12;42.034,11;43.041,7;54.034,169;55.042,104;56.049," +
                        "999;56.05,9;66.032," +
                        "166;66.033,34;67.04,16;68.048,86;68.049,13;69.044,41;81.044,469;82.052,695;83.059," +
                        "999;93.044,606;95.06,112;110.07,999;111.054,16;112.086,2;138.065,2;156.074,999",
                "Name: Tyrosine;43.019,2;51.025,3;55.02,1;65.039,52;67.055,10;77.039,199;78.043,2;79.055,8;81.07," +
                        "8;81.071,4;91.054,999;91.055,36;92.057,16;93.062,6;93.07,12;94.044,9;94.063," +
                        "4;95.05,444;96.054,4;103.056,35;105.046,10;107.049,130;107.05,51;108.07," +
                        "5;108.081,6;109.066,33;117.058,1;117.06,8;118.066,18;118.067,40;119.052," +
                        "693;120.053,10;120.057,5;121.066,12;123.043,385;123.045,633;123.046," +
                        "101;124.048,8;136.075,821;136.076,999;137.073,13;137.076,20;147.043,84;147.045,204;165.055," +
                        "6;165.056,999;165.057,355;166.988,5;182.082,999"};
        String[] instruments =
                new String[]{"LC-ESI-IT", "LC-ESI-ITFT", "LC-ESI-ITTOF", "LC-ESI-Q", "LC-ESI-QIT", "LC-ESI-QQ",
                             "LC-ESI-QTOF", "LC-ESI-TOF"};
        String ionMode = "positive";

        MassBankAPIStub mbStub = new MassBankAPIStub();
        MassBankAPIStub.ExecBatchJob bj = new MassBankAPIStub.ExecBatchJob();
        bj.setInstrumentTypes(instruments);
        bj.setIonMode(ionMode);
        bj.setQueryStrings(queries);
        bj.setType("searchSpectrum");
        bj.setMailAddress("");

        MassBankAPIStub.ExecBatchJobResponse ssr = mbStub.execBatchJob(bj);
        MassBankAPIStub.GetJobStatus status = new MassBankAPIStub.GetJobStatus();
        status.setJobId(ssr.get_return());

        while (!(mbStub.getJobStatus(status).get_return().getStatus().equals("Completed"))) {
            System.out.println(mbStub.getJobStatus(status).get_return().getStatus());
            Thread.sleep(3000);
        }

        MassBankAPIStub.GetJobResult jr = new MassBankAPIStub.GetJobResult();
        jr.setJobId(ssr.get_return());
        MassBankAPIStub.GetJobResultResponse jrr = mbStub.getJobResult(jr);
        System.out.println(jrr.get_return().length);
        System.out.println(jrr.get_return()[0].getNumResults());
        for (MassBankAPIStub.Result result : jrr.get_return()[0].getResults()) {
            System.out.println(
                    result.getId() + " " + result.getTitle() + " " + result.getScore() + " " + result.getExactMass());
        }
    }

    @Test
    public void testRecordRetrievel() throws Exception {

        String ids = "KO002525";

        MassBankAPIStub mbStub = new MassBankAPIStub();
        MassBankAPIStub.GetRecordInfo rci = new MassBankAPIStub.GetRecordInfo();
        rci.addIds(ids);
        MassBankAPIStub.GetRecordInfoResponse rciR = mbStub.getRecordInfo(rci);
        String resString = rciR.get_return()[0].getInfo();
        System.out.println(resString);
    }
}
