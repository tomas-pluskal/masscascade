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

package uk.ac.ebi.masscascade.ws.metlin;

import org.apache.commons.math3.util.FastMath;
import org.apache.log4j.Level;
import org.codehaus.jackson.map.ObjectMapper;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableWebservice;
import uk.ac.ebi.masscascade.interfaces.Feature;
import uk.ac.ebi.masscascade.interfaces.FeatureSet;
import uk.ac.ebi.masscascade.interfaces.container.FeatureSetContainer;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.properties.Identity;
import uk.ac.ebi.masscascade.utilities.comparator.PointIntensityComparator;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Web task to query Metlin, matching MS2 spectra where possible. Metlin requires
 * users to provide a token for certain web services which must be provided for the web task to work.
 * <ul>
 * <li>Parameter <code> MZ WINDOW PPM </code>- The m/z tolerance value for the precursor ions in ppm (1-100).</li>
 * <li>Parameter <code> MZ WINDOW AMU </code>- The m/z tolerance value for the MS2 signals in dalton (0.001-0.5).</li>
 * <li>Parameter <code> ION MODE </code>- The ion mode.</li>
 * <li>Parameter <code> SCORE METLIN </code>- The minimum Metlin query score.</li>
 * <li>Parameter <code> COLLISION ENERGY </code>- The collision energy.</li>
 * <li>Parameter <code> SECURITY TOKEN </code>- The Metlin security token.</li>
 * <li>Parameter <code> SPECTRUM CONTAINER </code>- The input featureset container.</li>
 * </ul>
 */
public class MetlinSearch extends CallableWebservice {

    private String token;
    private String ionMode;
    private int collisionEnergy;
    private double ppmMS1;
    private double ppmMS2;
    private int minScore;
    private FeatureSetContainer featureSetContainer;

    /**
     * Constructs a web task for the Metlin web service.
     *
     * @param params the parameter map holding all required task parameters
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the web task fails
     */
    public MetlinSearch(ParameterMap params) {

        super(MetlinSearch.class);
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

        ppmMS1 = params.get(Parameter.MZ_WINDOW_PPM, Double.class);
        ppmMS2 = params.get(Parameter.MZ_WINDOW_AMU, Double.class);
        token = params.get(Parameter.SECURITY_TOKEN, String.class);
        minScore = params.get(Parameter.SCORE_METLIN, Integer.class);
        collisionEnergy = params.get(Parameter.COLLISION_ENERGY, Integer.class);
        featureSetContainer = params.get(Parameter.FEATURE_SET_CONTAINER, FeatureSetContainer.class);

        Constants.ION_MODE mode = params.get(Parameter.ION_MODE, Constants.ION_MODE.class);
        ionMode = (mode == Constants.ION_MODE.POSITIVE) ? "pos" : "neg";
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

        ExecutorService executor = Executors.newFixedThreadPool(3);
        List<Future<FeatureSet>> futureList = new ArrayList<>();

        for (FeatureSet featureSet : featureSetContainer) {
            Callable<FeatureSet> searcher = new SpectrumSearcher(featureSet);
            futureList.add(executor.submit(searcher));
        }

        for (Future<FeatureSet> search : futureList) {
            try {
                outContainer.addFeatureSet(search.get());
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.log(Level.ERROR, e);
            }
        }

        executor.shutdown();

        outContainer.finaliseFile();
        return outContainer;
    }

    /**
     * Runs Metlin's <code> SearchSpectrum </code> web service on the query featureset. The returned profiles in the
     * featureset are annotated with the retrieved results.
     */
    class SpectrumSearcher implements Callable<FeatureSet> {

        private FeatureSet featureSet;

        /**
         * Constructs a Metlin search helper.
         *
         * @param featureSet the featureset containing the profiles for the query.
         */
        public SpectrumSearcher(FeatureSet featureSet) {
            this.featureSet = featureSet;
        }

        /**
         * Converts the featureset into a Metlin compatible format and queries Metlin for compounds matching the
         * featureset.
         *
         * @return the annotated featureset
         * @throws Exception if unable to run the web service
         */
        @Override
        public FeatureSet call() throws Exception {

            for (Feature feature : featureSet) {
                if (feature.hasMsnSpectra(Constants.MSN.MS2)) queryMetlin(feature);
            }

            return featureSet;
        }

        private void queryMetlin(Feature feature) {

            String urlPrefix = "http://metlin.scripps.edu/REST/match/index.php?";
            String urlToken = "token=" + token;
            String urlMz = "mass[]=";
            String urlIntensity = "intensity[]=";
            String urlMode = "mode=" + ionMode;
            String urlCollisionEnergy = "ce=" + collisionEnergy;
            String urlPpmMs2 = "tolMS=" + (float) ppmMS2;
            String urlPpmMs1 = "tolPrec=" + (int) ppmMS1;
            String urlParent = "prec=";

            String delimiter = "&";

            List<FeatureSet> msnSpectra = feature.getMsnSpectra(Constants.MSN.MS2);
            for (FeatureSet msnFeatureSet : msnSpectra) {

                StringBuilder urlBuilder = new StringBuilder();
                urlBuilder.append(urlPrefix);
                urlBuilder.append(urlToken);
                urlBuilder.append(delimiter);

                StringBuilder mzBuilder = new StringBuilder();
                StringBuilder intBuilder = new StringBuilder();

                List<XYPoint> dps = msnFeatureSet.getData();
                if (dps.size() > 30) {
                    dps = msnFeatureSet.getData();
                    Collections.sort(dps, new PointIntensityComparator());
                    dps = dps.subList(dps.size() - 30, dps.size());
                }

                DecimalFormat df = new DecimalFormat("##0.####");
                for (XYPoint dp : dps) {
                    mzBuilder.append(urlMz);
                    mzBuilder.append((float) dp.x);
                    mzBuilder.append(delimiter);

                    intBuilder.append(urlIntensity);
                    intBuilder.append(df.format((float) dp.y));
                    intBuilder.append(delimiter);
                }

                urlBuilder.append(mzBuilder.toString());
                urlBuilder.append(intBuilder.toString());
                urlBuilder.append(urlMode);
                urlBuilder.append(delimiter);
                urlBuilder.append(urlCollisionEnergy);
                urlBuilder.append(delimiter);
                urlBuilder.append(urlPpmMs2);
                urlBuilder.append(delimiter);
                urlBuilder.append(urlPpmMs1);
                urlBuilder.append(delimiter);
                urlBuilder.append(urlParent);
                urlBuilder.append((float) msnFeatureSet.getParentMz());

                try {
                    URL url = new URL(urlBuilder.toString());

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(120000);
                    conn.setConnectTimeout(120000);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.close();
                    os.close();

                    conn.connect();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = rd.readLine()) != null) sb.append(line + '\n');
                    line = sb.toString();

                    ObjectMapper mapper = new ObjectMapper();
                    ArrayList<ArrayList<LinkedHashMap<String, Object>>> metlin = mapper.readValue(line,
                            new ArrayList<ArrayList<LinkedHashMap<String, Object>>>().getClass());

                    for (ArrayList<LinkedHashMap<String, Object>> metlinSpectrum : metlin) {
                        Iterator<LinkedHashMap<String, Object>> iter = metlinSpectrum.iterator();
                        int metlinId = (Integer) iter.next().get("value");
                        String metlinName = (String) iter.next().get("value");
                        double metlinScore = Integer.parseInt((String) iter.next().get("value"));
                        metlinScore = FastMath.round(metlinScore * 10);
                        String metlinPrec = (String) iter.next().get("value");
                        int metlinPrecPpm = (Integer) iter.next().get("value");

                        if (metlinScore < minScore) continue;
                        Identity identity = new Identity(metlinId + "", metlinName, "", metlinScore, "Metlin",
                                Constants.MSN.MS2.name(), "");
                        feature.setProperty(identity);
                    }
                } catch (Exception exception) {
                    // if IOException, query most likely not found
                    LOGGER.log(Level.ERROR, "Metlin query failed: " + exception);
                }
            }
        }
    }
}