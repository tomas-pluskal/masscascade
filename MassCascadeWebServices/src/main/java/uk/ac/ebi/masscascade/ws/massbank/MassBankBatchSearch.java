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

package uk.ac.ebi.masscascade.ws.massbank;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Doubles;
import org.apache.commons.math3.util.FastMath;
import org.apache.log4j.Level;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableWebservice;
import uk.ac.ebi.masscascade.interfaces.Feature;
import uk.ac.ebi.masscascade.interfaces.FeatureSet;
import uk.ac.ebi.masscascade.interfaces.container.FeatureSetContainer;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.properties.Identity;
import uk.ac.ebi.masscascade.utilities.DataUtils;
import uk.ac.ebi.masscascade.utilities.range.ToleranceRange;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Web task to run a featureset search against MassBank using the MassBank's batch web service.
 * <ul>
 * <li>Parameter <code> MZ WINDOW PPM </code>- The m/z tolerance value for the precursor ions in ppm.</li>
 * <li>Parameter <code> SCORE </code>- The minimum score per hit (min-0, max=1).</li>
 * <li>Parameter <code> ION MODE </code>- The ion mode.</li>
 * <li>Parameter <code> RESULTS </code>- The max. number of retrieved results.</li>
 * <li>Parameter <code> INSTRUMENTS </code>- The instruments to be included in the query.</li>
 * <li>Parameter <code> RESULTS </code>- The max. number of results per featureset.</li>
 * <li>Parameter <code> SPECTRUM CONTAINER </code>- The input featureset container.</li>
 * </ul>
 */
public class MassBankBatchSearch extends CallableWebservice {

    private int minNumOfProfiles;
    private double score;
    private int maxNumOfResults;
    private List<String> instruments;
    private Constants.ION_MODE ionMode;
    private Constants.MSN msn;
    private double ppm;

    private MassBankAPIStub stub;

    private static final String EMAIL = "";
    private static final int QUERY_SLEEP = 3000;
    private static final String IUPAC = "CH$IUPAC: ";
    private static final String SEARCHSPECTRUM = "searchSpectrum";

    private FeatureSetContainer featureSetContainer;

    /**
     * Constructs a web task for the MassBank web service.
     *
     * @param params the parameter map holding all required task parameters
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the web task fails
     */
    public MassBankBatchSearch(ParameterMap params) throws MassCascadeException {

        super(MassBankBatchSearch.class);
        setParameters(params);
    }

    /**
     * Sets the task class variables using the parameter map.
     *
     * @param params the parameter map containing the <code> Parameter </code> to <code> Object </code> relations.
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the parameter map does not contain all variables required by this class
     */
    @Override
    public void setParameters(ParameterMap params) throws MassCascadeException {

        ppm = params.get(Parameter.MZ_WINDOW_PPM, Double.class);
        ionMode = params.get(Parameter.ION_MODE, Constants.ION_MODE.class);
        instruments = params.get(Parameter.INSTRUMENTS, (new ArrayList<String>()).getClass());
        minNumOfProfiles = params.get(Parameter.MIN_FEATURES, Integer.class);
        score = params.get(Parameter.SCORE, Double.class);
        maxNumOfResults = params.get(Parameter.RESULTS, Integer.class);
        msn = params.get(Parameter.MS_LEVEL, Constants.MSN.class);
        featureSetContainer = params.get(Parameter.FEATURE_SET_CONTAINER, FeatureSetContainer.class);
    }

    /**
     * Executes the task. The <code> Callable </code> returns a {@link uk.ac.ebi.masscascade.interfaces.container
     * .FeatureSetContainer} with the processed data.
     *
     * @return the featureset container with the processed data
     */
    public FeatureSetContainer call() {

        String id = featureSetContainer.getId() + IDENTIFIER;
        FeatureSetContainer outContainer = featureSetContainer.getBuilder().newInstance(FeatureSetContainer.class, id,
                featureSetContainer.getIonMode(), featureSetContainer.getWorkingDirectory());

        try {
            List<String> queries = buildQuery();

            stub = new MassBankAPIStub();
            MassBankAPIStub.ExecBatchJob batchJob = new MassBankAPIStub.ExecBatchJob();
            batchJob.setInstrumentTypes(instruments.toArray(new String[]{}));
            batchJob.setIonMode(ionMode.name().toLowerCase());
            batchJob.setQueryStrings(queries.toArray(new String[]{}));
            batchJob.setType(SEARCHSPECTRUM);
            batchJob.setMailAddress(EMAIL);

            MassBankAPIStub.ExecBatchJobResponse batchJobResponse = stub.execBatchJob(batchJob);
            MassBankAPIStub.GetJobStatus batchJobStatus = new MassBankAPIStub.GetJobStatus();
            batchJobStatus.setJobId(batchJobResponse.get_return());

            while (!(stub.getJobStatus(batchJobStatus).get_return().getStatus().equals("Completed")))
                Thread.sleep(QUERY_SLEEP);

            MassBankAPIStub.GetJobResult batchJobResult = new MassBankAPIStub.GetJobResult();
            batchJobResult.setJobId(batchJobResponse.get_return());
            MassBankAPIStub.GetJobResultResponse finalResponse = stub.getJobResult(batchJobResult);

            MassBankAPIStub.ResultSet[] resultSets = finalResponse.get_return();
            Multimap<String, Identity> recordIdsToIdentities = HashMultimap.create();

            if (msn == Constants.MSN.MS1) {

                Map<Integer, Multimap<Double, Identity>> sIdToMzProfiId = new HashMap<>();

                for (MassBankAPIStub.ResultSet resultSet : resultSets) {

                    int querySpectrumId = Integer.parseInt(resultSet.getQueryName());
                    FeatureSet queryFeatureSet = featureSetContainer.getFeatureSet(querySpectrumId);
                    TreeSet<Double> mzTree = new TreeSet<>(Doubles.asList(queryFeatureSet.getData().getXs()));

                    MassBankAPIStub.Result[] results = resultSet.getResults();
                    if (results == null) continue;
                    Arrays.sort(results, new MassBankComparator());
                    int resultCounter = 0;
                    for (MassBankAPIStub.Result result : results) {
                        if (Double.parseDouble(result.getScore()) < score) continue;

                        String resultId = result.getId();
                        String title = result.getTitle();
                        double mass = Double.parseDouble(result.getExactMass());
                        double score = Double.parseDouble(result.getScore());

                        if (ionMode == Constants.ION_MODE.POSITIVE) mass = mass + Constants.PARTICLES.PROTON.getMass();
                        else if (ionMode.equals(Constants.ION_MODE.NEGATIVE))
                            mass = mass - Constants.PARTICLES.PROTON.getMass();

                        Double closestValue = DataUtils.getClosestValue(mass, mzTree);
                        if (closestValue != null) {
                            Identity identity =
                                    new Identity(id, title.split(";")[0], "", score, "MassBank", msn.name(), title);
                            recordIdsToIdentities.put(resultId, identity);
                            if (sIdToMzProfiId.containsKey(querySpectrumId)) {
                                sIdToMzProfiId.get(querySpectrumId).put(closestValue, identity);
                            } else {
                                Multimap<Double, Identity> mzToProfId = HashMultimap.create();
                                mzToProfId.put(closestValue, identity);
                                sIdToMzProfiId.put(querySpectrumId, mzToProfId);
                            }

                            if (resultCounter++ > maxNumOfResults) break;
                        }
                    }
                }

                Map<Identity, String> identityToInChI = getIUPACNotation(recordIdsToIdentities);
                for (FeatureSet featureSet : featureSetContainer) {
                    if (sIdToMzProfiId.containsKey(featureSet.getIndex())) {
                        Multimap<Double, Identity> tmpMm = sIdToMzProfiId.get(featureSet.getIndex());
                        for (double mz : tmpMm.keySet()) {
                            for (Feature feature : featureSet) {
                                if (feature.getMz() == mz) {
                                    for (Identity identity : tmpMm.get(mz)) {
                                        identity.setNotation(identityToInChI.get(identity));
                                        feature.setProperty(identity);
                                    }
                                }
                            }
                        }
                    }
                    outContainer.addFeatureSet(featureSet);
                }
            } else {

                Map<Integer, Multimap<Integer[], Identity>> sIdToMzProfiId = new HashMap<>();
                Map<Identity, Double> identityToMass = new HashMap<>();

                for (MassBankAPIStub.ResultSet resultSet : resultSets) {
                    String[] ids = resultSet.getQueryName().split("-");
                    int specIdMs1 = Integer.parseInt(ids[0]);
                    int profileIdMs1 = Integer.parseInt(ids[1]);
                    int profileIdMsn = Integer.parseInt(ids[2]);

                    MassBankAPIStub.Result[] results = resultSet.getResults();
                    if (results == null) continue;
                    Arrays.sort(results, new MassBankComparator());
                    int resultCounter = 0;
                    for (MassBankAPIStub.Result result : results) {
                        if (Double.parseDouble(result.getScore()) < score) continue;

                        String resultId = result.getId();
                        String title = result.getTitle();
                        double mass = Double.parseDouble(result.getExactMass());
                        double score = Double.parseDouble(result.getScore());
                        score = FastMath.round(score * 1000);

                        if (ionMode == Constants.ION_MODE.POSITIVE) mass = mass + Constants.PARTICLES.PROTON.getMass();
                        else if (ionMode.equals(Constants.ION_MODE.NEGATIVE))
                            mass = mass - Constants.PARTICLES.PROTON.getMass();

                        String name = title.split(";")[0];
                        Identity identity = new Identity(resultId, name, "", score, "MassBank", msn.name(), title);
                        recordIdsToIdentities.put(resultId, identity);
                        identityToMass.put(identity, mass);

                        if (sIdToMzProfiId.containsKey(specIdMs1)) {
                            sIdToMzProfiId.get(specIdMs1).put(new Integer[]{profileIdMs1, profileIdMsn}, identity);
                        } else {
                            Multimap<Integer[], Identity> mzToProfId = HashMultimap.create();
                            mzToProfId.put(new Integer[]{profileIdMs1, profileIdMsn}, identity);
                            sIdToMzProfiId.put(specIdMs1, mzToProfId);
                        }

                        if (resultCounter++ > maxNumOfResults) break;
                    }
                }

                Map<Identity, String> identityToInChI = getIUPACNotation(recordIdsToIdentities);
                for (FeatureSet featureSet : featureSetContainer) {
                    if (sIdToMzProfiId.containsKey(featureSet.getIndex())) {
                        Multimap<Integer[], Identity> msnSpectrum = sIdToMzProfiId.get(featureSet.getIndex());
                        for (Integer[] profIds : msnSpectrum.keySet()) {
                            Feature tmpProf = featureSet.getFeature(profIds[0]);
                            if (msn.up() == Constants.MSN.MS1) {
                                for (Identity identity : msnSpectrum.get(profIds)) {
                                    if (new ToleranceRange(identityToMass.get(identity), ppm).contains(
                                            tmpProf.getMz())) {
                                        identity.setNotation(identityToInChI.get(identity));
                                        tmpProf.setProperty(identity);
                                    }
                                }
                            } else {
                                for (FeatureSet parentFeatureSet : tmpProf.getMsnSpectra(msn.up())) {
                                    Feature parentFeature = parentFeatureSet.getFeature(profIds[1]);
                                    if (parentFeature != null) {
                                        for (Identity identity : msnSpectrum.get(profIds)) {
                                            if (new ToleranceRange(identityToMass.get(identity), ppm).contains(
                                                    parentFeature.getMz())) {
                                                identity.setNotation(identityToInChI.get(identity));
                                                parentFeature.setProperty(identity);
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    outContainer.addFeatureSet(featureSet);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            LOGGER.log(Level.ERROR, "MassBank batch error: " + exception);
        }

        outContainer.finaliseFile();
        return outContainer;
    }

    private List<String> buildQuery() {

        List<String> queries = new ArrayList<>();
        for (FeatureSet ps : featureSetContainer) {

            if (msn == Constants.MSN.MS1) {
                if (ps.size() < minNumOfProfiles) continue;

                double maxY = Constants.MIN_ABUNDANCE;
                for (XYPoint dp : ps.getData()) if (dp.y > maxY) maxY = dp.y;

                StringBuilder sb = new StringBuilder();
                sb.append("Name: " + ps.getIndex() + ";");
                for (XYPoint dp : ps.getData()) {
                    sb.append(dp.x);
                    sb.append(",");
                    sb.append(dp.y * 1000 / maxY);
                    sb.append(";");
                }
                queries.add(sb.toString());
            } else {
                for (Feature feature : ps) {
                    if (feature.hasMsnSpectra(msn)) {
                        for (FeatureSet singleMsnPs : feature.getMsnSpectra(msn)) {
                            if (singleMsnPs.size() < minNumOfProfiles) continue;

                            double maxY = Constants.MIN_ABUNDANCE;
                            for (XYPoint dp : singleMsnPs.getData()) if (dp.y > maxY) maxY = dp.y;

                            StringBuilder sb = new StringBuilder();
                            sb.append(
                                    "Name: " + ps.getIndex() + "-" + feature.getId() + "-" + singleMsnPs
                                            .getParentScan() + ";");
                            for (XYPoint dp : singleMsnPs.getData()) {
                                sb.append(dp.x);
                                sb.append(",");
                                sb.append(dp.y * 1000 / maxY);
                                sb.append(";");
                            }
                            queries.add(sb.toString());
                        }
                    }
                }
            }
        }
        return queries;
    }

    /**
     * Retrieves the full record info and extracts the InChI.
     *
     * @param recIdToIdentities the record id to identities map
     * @return the identity to InChI map
     * @throws Exception if SOAP retrieval fails
     */
    private Map<Identity, String> getIUPACNotation(Multimap<String, Identity> recIdToIdentities) throws Exception {

        Map<Identity, String> identityToInchI = new HashMap<>();

        MassBankAPIStub.GetRecordInfo rci = new MassBankAPIStub.GetRecordInfo();
        for (String recID : recIdToIdentities.keySet()) rci.addIds(recID);
        MassBankAPIStub.GetRecordInfoResponse rciR = stub.getRecordInfo(rci);
        for (MassBankAPIStub.RecordInfo recInfo : rciR.get_return()) {
            String recInfoDetail = recInfo.getInfo();
            String iupacString = "";
            int iupacIndex = recInfoDetail.indexOf(IUPAC);
            if (iupacIndex != -1) {
                iupacIndex = iupacIndex + IUPAC.length();
                int iupacStop = recInfoDetail.indexOf('\n', iupacIndex);
                if (iupacStop == -1) iupacStop = recInfoDetail.indexOf('\r', iupacIndex);
                if (iupacStop != -1) iupacString = recInfoDetail.substring(iupacIndex, iupacStop);
            }

            for (Identity identity : recIdToIdentities.get(recInfo.getId()))
                identityToInchI.put(identity, iupacString);
        }

        return identityToInchI;
    }
}
