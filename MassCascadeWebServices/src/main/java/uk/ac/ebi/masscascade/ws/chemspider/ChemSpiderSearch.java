/*
 * Copyright (c) 2013, Stephan Beisken. All rights reserved.
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
 * Class to execute a simple mz query against ChemSpider.
 */
public class ChemSpiderSearch extends CallableTask {

    private String token;
    private Constants.ION_MODE ionMode;
    private double massTolerance;
    private String[] databases;
    private ChemSpiderWrapper wrapper;
    private FileProfileContainer profileContainer;

    /**
     * Constructor for a ChemSpider search task.
     *
     * @param params the parameter map
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public ChemSpiderSearch(ParameterMap params) throws MassCascadeException {

        super(ChemSpiderSearch.class);
        setParameters(params);
    }

    /**
     * Sets the parameters for the ChemSpider search task.
     *
     * @param params the parameter objects
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public void setParameters(ParameterMap params) throws MassCascadeException {

        massTolerance = params.get(Parameter.MZ_WINDOW_PPM, Double.class);
        ionMode = params.get(Parameter.ION_MODE, Constants.ION_MODE.class);
        token = params.get(Parameter.SECURITY_TOKEN, String.class);
        profileContainer = params.get(Parameter.PROFILE_CONTAINER, FileProfileContainer.class);
    }

    /**
     * Executes the task.
     *
     * @return the annotated profile data
     */
    public ProfileContainer call() {

        String id = profileContainer.getId() + IDENTIFIER;
        ProfileContainer outContainer = new FileProfileContainer(id, profileContainer.getWorkingDirectory());

        wrapper = new ChemSpiderWrapper();
        databases = wrapper.getMassSpecAPIGetDatabasesResults();

        ExecutorService executor = Executors.newFixedThreadPool(Constants.NTHREADS);
        List<Future<Profile>> futureList = new ArrayList<Future<Profile>>();

        for (int profileId : profileContainer.getProfileNumbers().keySet()) {

            Callable<Profile> css = new CsSearch(profileContainer.getProfile(profileId));
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

    class CsSearch implements Callable<Profile> {

        private Profile csProfile;

        public CsSearch(Profile csProfile) {

            this.csProfile = csProfile;
        }

        /**
         * Computes a result, or throws an exception if unable to do so.
         *
         * @return computed result
         * @throws Exception if unable to compute a result
         */
        @Override
        public Profile call() throws Exception {

            double mass = csProfile.getMz();
            if (ionMode == Constants.ION_MODE.POSITIVE) {
                mass = mass - Constants.PARTICLES.PROTON.getMass();
            } else if (ionMode == Constants.ION_MODE.NEGATIVE) {
                mass = mass + Constants.PARTICLES.PROTON.getMass();
            }
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

                csProfile.setProperty(identity);
                inchis.add(notation);
            }

            return csProfile;
        }
    }
}
