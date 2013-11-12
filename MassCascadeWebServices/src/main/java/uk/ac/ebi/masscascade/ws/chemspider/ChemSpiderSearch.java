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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Ints;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Web task to query ChemSpider for compounds that match the major isotopic mass of a given ion. ChemSpider requires
 * users to provide a token for certain web services which must be provided for the web task to work.
 * <ul>
 * <li>Parameter <code> MZ_WINDOW_PPM </code>- The m/z tolerance value in ppm.</li>
 * <li>Parameter <code> DATABASES </code>- The databases to be queried.</li>
 * <li>Parameter <code> SECURITY_TOKEN </code>- The ChemSpider security token.</li>
 * <li>Parameter <code> FEATURE_SET_CONTAINER </code>- The input featureset container.</li>
 * </ul>
 */
public class ChemSpiderSearch extends CallableWebservice {

    private String token;
    private double massTolerance;
    private String[] databases;
    private ChemSpiderWrapper wrapper;
    private FeatureSetContainer featureSetContainer;

    /**
     * Constructs a web task for the ChemSpider web service.
     *
     * @param params the parameter map holding all required task parameters
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the web task fails
     */
    public ChemSpiderSearch(ParameterMap params) throws MassCascadeException {

        super(ChemSpiderSearch.class);
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

        massTolerance = params.get(Parameter.MZ_WINDOW_PPM, Double.class);
        token = params.get(Parameter.SECURITY_TOKEN, String.class);
        featureSetContainer = params.get(Parameter.FEATURE_SET_CONTAINER, FeatureSetContainer.class);
        databases = params.get(Parameter.DATABASES, (new String[0]).getClass());
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

        Constants.ION_MODE ionMode = featureSetContainer.getIonMode();

        wrapper = new ChemSpiderWrapper();

        ExecutorService executor = Executors.newFixedThreadPool(Constants.NTHREADS);
        List<Future<Multimap<Integer, Integer>>> futureList = new ArrayList<>();

        for (Feature feature : featureSetContainer.featureIterator()) {

            Callable<Multimap<Integer, Integer>> css = new Searcher(feature, ionMode);
            futureList.add(executor.submit(css));
        }

        Set<Integer> allUniqueCsids = new HashSet<>();
        Multimap<Integer, Integer> profileIdToCsids = HashMultimap.create();
        for (Future<Multimap<Integer, Integer>> css : futureList) {

            try {
                profileIdToCsids.putAll(css.get());
                allUniqueCsids.addAll(css.get().values());
            } catch (ExecutionException | InterruptedException exception) {
                LOGGER.log(Level.ERROR, exception);
            }
        }

        executor.shutdown();

        Map<Integer, Map<String, String>> csidMap =
                wrapper.getMassSpecAPIGetExtendedCompoundInfoArrayResults(Ints.toArray(allUniqueCsids), token);

        Map<String, String> propMap;
        for (FeatureSet featureSet : featureSetContainer) {
            for (Feature feature : featureSet) {
                if (profileIdToCsids.containsKey(feature.getId())) {
                    Set<String> inchis = new HashSet<>();
                    for (int csid : profileIdToCsids.get(feature.getId())) {
                        propMap = csidMap.get(csid);
                        if (!propMap.containsKey("CSID") || !propMap.containsKey("InChI")
                                || !propMap.containsKey("CommonName") || !propMap.containsKey("MonoisotopicMass")) {
                            continue;
                        }
                        String ident = propMap.get("CSID");
                        String name = propMap.get("CommonName");
                        String notation = propMap.get("InChI");
                        double identMass = Double.parseDouble(propMap.get("MonoisotopicMass"));
                        // mass difference to query mass in ppm
                        double mass = feature.getMz();
                        if (ionMode == Constants.ION_MODE.POSITIVE) mass -= Constants.PARTICLES.PROTON.getMass();
                        else if (ionMode == Constants.ION_MODE.NEGATIVE) mass += Constants.PARTICLES.PROTON.getMass();
                        double score = FastMath.abs((mass - identMass) * Constants.PPM / mass);
                        score = FastMath.round(-0.001 * score + 1000);
                        Identity identity = new Identity(ident, name, notation, score, "ChemSpider", "m/z", "");

                        if (inchis.contains(notation)) {
                            continue;
                        }

                        feature.setProperty(identity);
                        inchis.add(notation);
                    }
                }
            }
            outContainer.addFeatureSet(featureSet);
        }

        outContainer.finaliseFile();
        return outContainer;
    }

    /**
     * Runs ChemSpider's <code> SearchByMass </code> web service on the query feature.
     */
    class Searcher implements Callable<Multimap<Integer, Integer>> {

        private Feature feature;
        private Constants.ION_MODE ionMode;

        /**
         * Constructs a ChemSpider search helper.
         *
         * @param feature the feature for the query.
         */
        public Searcher(Feature feature, Constants.ION_MODE ionMode) {
            this.feature = feature;
            this.ionMode = ionMode;
        }

        /**
         * Converts the feature into a ChemSpider compatible format and queries ChemSpider for compounds matching the
         * mass.
         *
         * @return the feature id to ChemSpider Ids map
         * @throws Exception if unable to run the web service
         */
        @Override
        public Multimap<Integer, Integer> call() throws Exception {

            double mass = feature.getMz();
            if (ionMode == Constants.ION_MODE.POSITIVE) mass -= Constants.PARTICLES.PROTON.getMass();
            else if (ionMode == Constants.ION_MODE.NEGATIVE) mass += Constants.PARTICLES.PROTON.getMass();

            double tolerance = mass * massTolerance / Constants.PPM;
            String result = wrapper.getMassSpecAPISearchByMassAsyncResults(mass, tolerance, databases, token);

            int[] csids = wrapper.getSearchGetAsyncSearchResultResults(result, token);

            Multimap<Integer, Integer> profileIdToCsids = HashMultimap.create();
            if (csids != null) profileIdToCsids.putAll(feature.getId(), Ints.asList(csids));
            return profileIdToCsids;
        }
    }
}
