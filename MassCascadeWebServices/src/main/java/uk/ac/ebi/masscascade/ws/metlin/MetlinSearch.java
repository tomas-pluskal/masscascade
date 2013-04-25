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

import org.apache.log4j.Level;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableWebservice;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.container.SpectrumContainer;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Web task to query Metlin, matching MSn spectra where possible. Metlin requires
 * users to provide a token for certain web services which must be provided for the web task to work.
 * <ul>
 * <li>Parameter <code> MZ WINDOW PPM </code>- The m/z tolerance value for the precursor ions in ppm (1-100).</li>
 * <li>Parameter <code> MZ WINDOW AMU </code>- The m/z tolerance value for the MS2 signals in dalton (0.001-0.5).</li>
 * <li>Parameter <code> ION MODE </code>- The ion mode.</li>
 * <li>Parameter <code> COLLISION ENERGY </code>- The collision energy.</li>
 * <li>Parameter <code> SECURITY TOKEN </code>- The Metlin security token.</li>
 * <li>Parameter <code> SPECTRUM CONTAINER </code>- The input spectrum container.</li>
 * </ul>
 */
public class MetlinSearch extends CallableWebservice {

    // #30 MS2 limit

    private String token;
    private String ionMode;
    private int collisionEnergy;
    private double ppmMS1;
    private double ppmMS2;
    private SpectrumContainer spectrumContainer;

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
        collisionEnergy = params.get(Parameter.COLLISION_ENERGY, Integer.class);
        spectrumContainer = params.get(Parameter.SPECTRUM_CONTAINER, SpectrumContainer.class);

        Constants.ION_MODE mode = params.get(Parameter.ION_MODE, Constants.ION_MODE.class);
        ionMode = (mode == Constants.ION_MODE.POSITIVE) ? "pos" : "neg";
    }

    /**
     * Executes the task. The <code> Callable </code> returns a {@link uk.ac.ebi.masscascade.interfaces.container
     * .SpectrumContainer} with the processed data.
     *
     * @return the spectrum container with the processed data
     */
    public SpectrumContainer call() {

        String id = spectrumContainer.getId() + IDENTIFIER;
        SpectrumContainer outContainer = spectrumContainer.getBuilder().newInstance(SpectrumContainer.class, id,
                spectrumContainer.getWorkingDirectory());

        for (Profile profile : spectrumContainer.profileIterator()) {

            queryMetlin(profile);
        }

        return outContainer;
    }

    private void queryMetlin(Profile profile) {

        String urlPrefix = "http://metlin.scripps.edu/REST/search/index.php?";
        String urlToken = "token=" + token;
        String urlMz = "mass[]=";
        String urlIntensity = "intensity[]=";
        String urlMode = "mode=" + ionMode;
        String urlCollisionEnergy = "ce=" + collisionEnergy;
        String urlPpmMs1 = "tolPrec=" + ppmMS1;
        String urlPpmMs2 = "tolMS=" + ppmMS2;
        String urlParent = "prec=";

        String delimiter = "&";

        try {
            URL url = new URL("");

            // take note that special formatting is required for "+" signs
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
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
            String line = null;

            while ((line = rd.readLine()) != null) {
                sb.append(line + '\n');
            }

            System.out.println(sb.toString());

        } catch (Exception exception) {
            LOGGER.log(Level.ERROR, "Metlin query failed: " + exception);
        }
    }
}