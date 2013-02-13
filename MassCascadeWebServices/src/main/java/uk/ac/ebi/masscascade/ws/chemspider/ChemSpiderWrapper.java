/*
 * Copyright (c) 2013, Stephan Beisken. All rights reserved.
 *
 * This file is part of MassCascade.
 * It was taken from the ChemSpiderBlog.
 * Copyright (c) 2011 Aileen Day
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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import uk.ac.ebi.masscascade.ws.chemspider.InChIStub.InChIToCSIDResponse;
import uk.ac.ebi.masscascade.ws.chemspider.MassSpecAPIStub.ArrayOfInt;
import uk.ac.ebi.masscascade.ws.chemspider.MassSpecAPIStub.ArrayOfString;
import uk.ac.ebi.masscascade.ws.chemspider.MassSpecAPIStub.ExtendedCompoundInfo;
import uk.ac.ebi.masscascade.ws.chemspider.MassSpecAPIStub.GetDatabasesResponse;
import uk.ac.ebi.masscascade.ws.chemspider.MassSpecAPIStub.GetExtendedCompoundInfoArrayResponse;
import uk.ac.ebi.masscascade.ws.chemspider.MassSpecAPIStub.SearchByMassAsyncResponse;
import uk.ac.ebi.masscascade.ws.chemspider.SearchStub.GetAsyncSearchResultResponse;
import uk.ac.ebi.masscascade.ws.chemspider.SearchStub.GetAsyncSearchStatusResponse;
import uk.ac.ebi.masscascade.ws.chemspider.SearchStub.SimpleSearchResponse;

import java.util.HashMap;
import java.util.Map;

public class ChemSpiderWrapper {

    private static final Logger LOG = Logger.getLogger(ChemSpiderWrapper.class.getName());
    private static MassSpecAPIStub thisMassSpecAPIStub;
    private static SearchStub thisSearchStub;

    public ChemSpiderWrapper() {

        try {
            thisMassSpecAPIStub = new MassSpecAPIStub();
            thisSearchStub = new SearchStub();
        } catch (Exception exception) {
            LOG.log(Level.ERROR, "Problem retrieving ChemSpider webservices", exception);
        }
    }

    /**
     * Function to call the InChIToCSID operation of ChemSpider's InChI SOAP 1.2 webservice
     * (http://www.chemspider.com/InChI.asmx?op=InChIToCSID)
     * Convert InChI to ChemSpider ID.
     *
     * @param inchi: string representing inchi to search ChemSpider for
     * @return: string representing CSID returned
     */
    public String getInChIInChIToCSIDResults(String inchi) {

        String Output = null;
        try {
            final InChIStub thisInChIstub = new InChIStub();
            InChIStub.InChIToCSID InChIToCSIDInput = new InChIStub.InChIToCSID();
            InChIToCSIDInput.setInchi(inchi);
            final InChIToCSIDResponse thisInChIToCSIDResponse = thisInChIstub.inChIToCSID(InChIToCSIDInput);
            Output = thisInChIToCSIDResponse.getInChIToCSIDResult();
        } catch (Exception e) {
            LOG.log(Level.ERROR, "Problem retrieving ChemSpider webservices", e);
        }
        return Output;
    }

    /**
     * Function to call the SimpleSearch operation of ChemSpider's Search SOAP 1.2 webservice
     * (http://www.chemspider.com/search.asmx?op=SimpleSearch)
     * Search by Name, SMILES, InChI, InChIKey, etc. Returns a list of found CSIDs (first 100 - please use
     * AsyncSimpleSearch instead if you like to get the full list). Security token is required.
     *
     * @param query: String representing search term (can be Name, SMILES, InChI, InChIKey)
     * @param token: string containing your user token (listed at your http://www.chemspider.com/UserProfile.aspx page)
     * @return: int[] array containing the ChemSpider IDs. If more than 100 are found then only the first 100 are
     * returned.
     */
    public int[] getSearchSimpleSearchResults(String query, String token) {

        int[] Output = null;
        try {
            SearchStub.SimpleSearch SimpleSearchInput = new SearchStub.SimpleSearch();
            SimpleSearchInput.setQuery(query);
            SimpleSearchInput.setToken(token);
            final SimpleSearchResponse thisSimpleSearchResponse = thisSearchStub.simpleSearch(SimpleSearchInput);
            Output = thisSimpleSearchResponse.getSimpleSearchResult().get_int();
        } catch (Exception e) {
            LOG.log(Level.ERROR, "Problem retrieving ChemSpider webservices", e);
        }
        return Output;
    }

    /**
     * Function to call the GetDatabases operation of ChemSpider's MassSpecAPI SOAP 1.2 webservice
     * (http://www.chemspider.com/massspecapi.asmx?op=GetDatabases)
     * Get the list of datasources in ChemSpider.
     *
     * @return: the list of datasources in ChemSpider as a String Array
     */
    public String[] getMassSpecAPIGetDatabasesResults() {

        String[] Output = null;
        try {
            MassSpecAPIStub.GetDatabases getDatabaseInput = new MassSpecAPIStub.GetDatabases();
            final GetDatabasesResponse thisGetDatabasesResponse = thisMassSpecAPIStub.getDatabases(getDatabaseInput);
            Output = thisGetDatabasesResponse.getGetDatabasesResult().getString();
        } catch (Exception e) {
            LOG.log(Level.ERROR, "Problem retrieving ChemSpider webservices", e);
        }
        return Output;
    }

    /**
     * Function to call the GetExtendedCompoundInfoArray operation of ChemSpider's MassSpecAPI SOAP 1.2 webservice
     * (http://www.chemspider.com/massspecapi.asmx?op=GetExtendedCompoundInfoArray)
     * Get array of extended record details by an array of CSIDs. Security token is required.
     *
     * @param CSIDs: integer array containing the CSIDs of compounds for which information will be returned
     * @param token: string containing your user token (listed at your http://www.chemspider.com/UserProfile.aspx page)
     * @return: a Map<Integer CSID,Map<String Property,String Value>> containing the results array for each CSID (with
     * PropertyNames CSID, MF, SMILES, InChIKey, AverageMass, MolecularWeight, MonoisotopicMass, NominalMass, ALogP,
     * XLogP, CommonName)
     */
    public Map<Integer, Map<String, String>> getMassSpecAPIGetExtendedCompoundInfoArrayResults(int[] CSIDs,
            String token) {

        Map<Integer, Map<String, String>> Output = new HashMap<Integer, Map<String, String>>();
        try {
            ArrayOfInt inputCSIDsArrayofInt = new ArrayOfInt();
            inputCSIDsArrayofInt.set_int(CSIDs);
            MassSpecAPIStub.GetExtendedCompoundInfoArray getGetExtendedCompoundInfoArrayInput =
                    new MassSpecAPIStub.GetExtendedCompoundInfoArray();
            getGetExtendedCompoundInfoArrayInput.setCSIDs(inputCSIDsArrayofInt);
            getGetExtendedCompoundInfoArrayInput.setToken(token);
            final GetExtendedCompoundInfoArrayResponse thisGetExtendedCompoundInfoArrayResponse =
                    thisMassSpecAPIStub.getExtendedCompoundInfoArray(getGetExtendedCompoundInfoArrayInput);
            ExtendedCompoundInfo[] thisExtendedCompoundInfo =
                    thisGetExtendedCompoundInfoArrayResponse.getGetExtendedCompoundInfoArrayResult()
                            .getExtendedCompoundInfo();
            for (int i = 0; i < thisExtendedCompoundInfo.length; i++) {
                Map<String, String> thisCompoundExtendedCompoundInfoArrayOutput = new HashMap<String, String>();
                thisCompoundExtendedCompoundInfoArrayOutput.put("CSID",
                        Integer.toString(thisExtendedCompoundInfo[i].getCSID()));
                thisCompoundExtendedCompoundInfoArrayOutput.put("InChI", thisExtendedCompoundInfo[i].getInChI());
                thisCompoundExtendedCompoundInfoArrayOutput.put("MonoisotopicMass",
                        Double.toString(thisExtendedCompoundInfo[i].getMonoisotopicMass()));
                thisCompoundExtendedCompoundInfoArrayOutput.put("CommonName",
                        thisExtendedCompoundInfo[i].getCommonName());
                Output.put(thisExtendedCompoundInfo[i].getCSID(), thisCompoundExtendedCompoundInfoArrayOutput);
            }
        } catch (Exception e) {
            LOG.log(Level.ERROR, "Problem retrieving ChemSpider webservices", e);
        }
        return Output;
    }

    /**
     * Function to call the SearchByMass2 operation of ChemSpider's MassSpecAPI SOAP 1.2 webservice
     * (http://www.chemspider.com/massspecapi.asmx?op=SearchByMass2)
     * Search ChemSpider by mass +/- range.
     *
     * @param mass  The compounds returned have a mass (Double) within the range Mass +/- Range
     * @param range The compounds returned have a mass (Double) within the range Mass +/- Range
     * @return: the ChemSpider IDs of compounds returned (as a String Array)
     */
    public String getMassSpecAPISearchByMassAsyncResults(Double mass, Double range, String[] dbs, String token) {

        String Output = null;
        try {
            MassSpecAPIStub.SearchByMassAsync getSearchByMassAsyncInput = new MassSpecAPIStub.SearchByMassAsync();
            getSearchByMassAsyncInput.setMass(mass);
            getSearchByMassAsyncInput.setRange(range);
            ArrayOfString inputDBsArrayofString = new ArrayOfString();
            inputDBsArrayofString.setString(dbs);
            getSearchByMassAsyncInput.setDbs(inputDBsArrayofString);
            getSearchByMassAsyncInput.setToken(token);
            final SearchByMassAsyncResponse thisSearchByMassAsyncResponse =
                    thisMassSpecAPIStub.searchByMassAsync(getSearchByMassAsyncInput);
            Output = thisSearchByMassAsyncResponse.getSearchByMassAsyncResult();
        } catch (Exception e) {
            LOG.log(Level.ERROR, "Problem retrieving ChemSpider webservices", e);
        }
        return Output;
    }

    /**
     * Function to call the GetAsyncSearchStatus operation of ChemSpider's Search SOAP 1.2 webservice
     * (http://www.chemspider.com/search.asmx?op=GetAsyncSearchStatus)
     * Query asynchronous operation status. Requires transaction ID returned by AsynchSearch operation. Security token
     * is required.
     *
     * @param rid:   String representing transaction ID returned from a previous search
     * @param token: string containing your user token (listed at your http://www.chemspider.com/UserProfile.aspx page)
     * @return: String describing status of this search - can have values Unknown or Created or Scheduled or Processing
     * or Suspended or PartialResultReady or ResultReady or Failed or TooManyRecords
     */
    public String getSearchGetAsyncSearchStatusResults(String rid, String token) {

        String Output = null;
        try {
            SearchStub.GetAsyncSearchStatus GetAsyncSearchStatusInput = new SearchStub.GetAsyncSearchStatus();
            GetAsyncSearchStatusInput.setRid(rid);
            GetAsyncSearchStatusInput.setToken(token);
            final GetAsyncSearchStatusResponse thisGetAsyncSearchStatusResponse =
                    thisSearchStub.getAsyncSearchStatus(GetAsyncSearchStatusInput);
            Output = thisGetAsyncSearchStatusResponse.getGetAsyncSearchStatusResult().toString();
        } catch (Exception e) {
            LOG.log(Level.ERROR, "Problem retrieving ChemSpider webservices", e);
        }
        return Output;
    }

    /**
     * Function to call the GetAsyncSearchResult operation of ChemSpider's Search SOAP 1.2 webservice
     * (http://www.chemspider.com/search.asmx?op=GetAsyncSearchResult)
     * Returns the list of CSIDs found by AsynchSearch operation. Security token is required.
     *
     * @param rid:   String representing transaction ID returned from a previous search
     * @param token: string containing your user token (listed at your http://www.chemspider.com/UserProfile.aspx page)
     * @return: int[] array containing the ChemSpider IDs.
     */
    public int[] getSearchGetAsyncSearchResultResults(String rid, String token) {

        int[] Output = null;
        try {
            SearchStub.GetAsyncSearchResult GetAsyncSearchResultInput = new SearchStub.GetAsyncSearchResult();
            GetAsyncSearchResultInput.setRid(rid);
            GetAsyncSearchResultInput.setToken(token);
            final GetAsyncSearchResultResponse thisGetAsyncSearchResultResponse =
                    thisSearchStub.getAsyncSearchResult(GetAsyncSearchResultInput);
            Output = thisGetAsyncSearchResultResponse.getGetAsyncSearchResultResult().get_int();
        } catch (Exception e) {
            LOG.log(Level.ERROR, "Problem retrieving ChemSpider webservices", e);
        }
        return Output;
    }
}