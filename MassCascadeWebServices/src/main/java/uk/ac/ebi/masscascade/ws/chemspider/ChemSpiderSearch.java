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

import org.apache.log4j.Level;
import uk.ac.ebi.masscascade.core.container.file.profile.FileProfileContainer;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.container.ProfileContainer;
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
 * <li>Parameter <code> MZ WINDOW PPM </code>- The m/z tolerance value in ppm.</li>
 * <li>Parameter <code> ION MODE </code>- The ion mode.</li>
 * <li>Parameter <code> SECURITY TOKEN </code>- The ChemSpider security token.</li>
 * <li>Parameter <code> PROFILE CONTAINER </code>- The input profile container.</li>
 * </ul>
 */
public class ChemSpiderSearch extends CallableTask {

    private String token;
    private Constants.ION_MODE ionMode;
    private double massTolerance;
    private String[] databases;
    private ChemSpiderWrapper wrapper;
    private FileProfileContainer profileContainer;

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
        ionMode = params.get(Parameter.ION_MODE, Constants.ION_MODE.class);
        token = params.get(Parameter.SECURITY_TOKEN, String.class);
        profileContainer = params.get(Parameter.PROFILE_CONTAINER, FileProfileContainer.class);
    }

    /**
     * Executes the task. The <code> Callable </code> returns a {@link uk.ac.ebi.masscascade.interfaces.container
     * .ProfileContainer} with the processed data.
     *
     * @return the profile container with the processed data
     */
    public ProfileContainer call() {

        String id = profileContainer.getId() + IDENTIFIER;
        ProfileContainer outContainer = profileContainer.getBuilder().newInstance(ProfileContainer.class, id,
                profileContainer.getWorkingDirectory());

        wrapper = new ChemSpiderWrapper();
        databases = wrapper.getMassSpecAPIGetDatabasesResults();

        ExecutorService executor = Executors.newFixedThreadPool(Constants.NTHREADS);
        List<Future<Profile>> futureList = new ArrayList<Future<Profile>>();

        for (int profileId : profileContainer.getProfileNumbers().keySet()) {

            Callable<Profile> css = new Searcher(profileContainer.getProfile(profileId));
            futureList.add(executor.submit(css));
        }

        for (Future<Profile> css : futureList) {

            try {
                outContainer.addProfile(css.get());
            } catch (InterruptedException e) {
                LOGGER.log(Level.ERROR, e.getMessage());
                e.printStackTrace();
            } catch (ExecutionException e) {
                LOGGER.log(Level.ERROR, e.getMessage());
                e.printStackTrace();
            }
        }

        executor.shutdown();

        outContainer.finaliseFile();
        return outContainer;
    }

    /**
     * Runs ChemSpider's <code> SearchByMass </code> web service on the query profile. The returned profile is
     * annotated with the retrieved results.
     */
    class Searcher implements Callable<Profile> {

        private Profile profile;

        /**
         * Constructs a ChemSpider search helper.
         *
         * @param profile the profile for the query.
         */
        public Searcher(Profile profile) {
            this.profile = profile;
        }

        /**
         * Converts the profile into a ChemSpider compatible format and queries ChemSpider for compounds matching the
         * mass.
         *
         * @return the annotated profile
         * @throws Exception if unable to run the web service
         */
        @Override
        public Profile call() throws Exception {

            double mass = profile.getMz();
            if (ionMode == Constants.ION_MODE.POSITIVE) mass = mass - Constants.PARTICLES.PROTON.getMass();
            else if (ionMode == Constants.ION_MODE.NEGATIVE) mass = mass + Constants.PARTICLES.PROTON.getMass();

            double tolerance = mass * massTolerance / Constants.PPM;
            String result = wrapper.getMassSpecAPISearchByMassAsyncResults(mass, tolerance, databases, token);
            int[] csids = wrapper.getSearchGetAsyncSearchResultResults(result, token);
            Map<Integer, Map<String, String>> csidMap =
                    wrapper.getMassSpecAPIGetExtendedCompoundInfoArrayResults(csids, token);

            Set<String> inchis = new HashSet<String>();
            Map<String, String> propMap;
            for (int csid : csidMap.keySet()) {
                propMap = csidMap.get(csid);

                String ident = propMap.get("CSID");
                String name = propMap.get("CommonName");
                String notation = propMap.get("InChI");
                double identMass = Double.parseDouble(propMap.get("MonoisotopicMass"));
                // mass difference to query mass in ppm
                double score = Math.abs((mass - identMass) * Constants.PPM / mass);
                Identity identity = new Identity(ident, name, notation, score);

                if (inchis.contains(notation)) continue;

                profile.setProperty(identity);
                inchis.add(notation);
            }

            return profile;
        }
    }
}
