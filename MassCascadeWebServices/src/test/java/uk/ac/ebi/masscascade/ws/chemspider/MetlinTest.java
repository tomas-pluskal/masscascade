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

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.MappingIterator;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MetlinTest {

    private static final String token = "GW8fXPSP9aCdsnVj";

    @Test
    public void testMetlinMetabolite() throws Exception {

        URL url = new URL("http://metlin.scripps.edu/REST/search/index.php?token=" + token + "&mass[]=195" +
                ".0877&mass[]=181.0702&adduct[]=M%2BH&adduct[]=M%2BNa&adduct[]=M%2BK&tolunits=ppm&tolerance=30");

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
    }

    @Test
    public void testMetlinSpectrum() throws Exception {

        URL url =
                new URL("http://metlin.scripps.edu/REST/match/index.php?token=" + token + "&mass[]=138.066&mass[]=110" +
                        ".071&mass[]=42.034&mass[]=69.045&mass[]=123.043&mass[]=83.060&mass[]=109" +
                        ".036&intensity[]=10877&intensity[]=2221&intensity[]=644&intensity[]=351&intensity[]=350" +
                        "&intensity[]=318&intensity[]=234&mode=pos&ce=20&tolMS=0.5&tolPrec=100&prec=195.0877");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(60000);
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
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line + '\n');
        }

        line = sb.toString();
        System.out.println(line);

        ObjectMapper mapper = new ObjectMapper();
        ArrayList<ArrayList<LinkedHashMap<String, Object>>> metlin =
                mapper.readValue(line, new ArrayList<ArrayList<LinkedHashMap<String, Object>>>().getClass());

        for (ArrayList<LinkedHashMap<String, Object>> metlinSpectrum : metlin) {
            for (LinkedHashMap<String, Object> metlinAsso : metlinSpectrum) {
                String key = (String) metlinAsso.get("key");
                Object value = metlinAsso.get("value");

                System.out.println(key + ": " + value);
            }
        }
    }
}