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

package uk.ac.ebi.masscascade.msn;

import org.apache.log4j.Level;
import uk.ac.ebi.masscascade.core.PropertyType;
import uk.ac.ebi.masscascade.interfaces.CallableSearch;
import uk.ac.ebi.masscascade.interfaces.Feature;
import uk.ac.ebi.masscascade.interfaces.FeatureSet;
import uk.ac.ebi.masscascade.interfaces.container.Container;
import uk.ac.ebi.masscascade.interfaces.container.FeatureSetContainer;
import uk.ac.ebi.masscascade.library.LibraryParameter;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.properties.Identity;
import uk.ac.ebi.masscascade.utilities.DataUtils;
import uk.ac.ebi.masscascade.utilities.TextUtils;
import uk.ac.ebi.masscascade.utilities.math.MathUtils;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Annotates MSn signals based on identities found in MS1 profiles. Requires either InChI or SMILES to exist.
 * <ul>
 * <li>Parameter <code> MZ WINDOW AMU </code>- The m/z tolerance value for the MSn signals in dalton.</li>
 * <li>Parameter <code> DEPTH </code>- The fragment depth.</li>
 * <li>Parameter <code> EXECUTABLE </code>- The full path to the MSnFragExplorer executable.</li>
 * <li>Parameter <code> SPECTRUM CONTAINER </code>- The input featureset container.</li>
 * </ul>
 */
public class MsnEnumerator extends CallableSearch {

    private String executable;
    private int depth;
    private double amu;
    private FeatureSetContainer featureSetContainer;

    /**
     * Constructs an enumerator task.
     *
     * @param params the parameter map holding all required task parameters
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the task fails
     */
    public MsnEnumerator(ParameterMap params) {

        super(MsnEnumerator.class);
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
    public void setParameters(ParameterMap params) {

        featureSetContainer = params.get(Parameter.FEATURE_SET_CONTAINER, FeatureSetContainer.class);
        depth = params.get(LibraryParameter.DEPTH, Integer.class);
        amu = params.get(Parameter.MZ_WINDOW_AMU, Double.class);
        executable = params.get(Parameter.EXECUTABLE, String.class);
    }

    /**
     * Executes the task. The <code> Callable </code> returns a {@link uk.ac.ebi.masscascade.interfaces.container
     * .FeatureSetContainer} with the processed data.
     *
     * @return the featureset container with the processed data
     */
    @Override
    public Container call() {

        String id = featureSetContainer.getId() + IDENTIFIER;
        FeatureSetContainer outFeatureSetContainer = featureSetContainer.getBuilder().newInstance(FeatureSetContainer.class, id,
                featureSetContainer.getIonMode(), featureSetContainer.getWorkingDirectory());

        for (FeatureSet featureSet : featureSetContainer) {
            int msnId = 0;
            for (Feature feature : featureSet) {
                if (!(feature.hasProperty(PropertyType.Identity) && feature.hasMsnSpectra(Constants.MSN.MS2)))
                    continue;

                FeatureSet msnFeatureSet = feature.getMsnSpectra(Constants.MSN.MS2).get(0);
                for (Identity identity : feature.getProperty(PropertyType.Identity, Identity.class)) {
                    String notation = identity.getNotation();

                    if (notation == null || notation.isEmpty()) continue;
                    if (notation.contains("-") || notation.contains("+")) continue;

                    TreeMap<Double, List<String>> massToSmiles = enumerate(notation, depth);
                    for (Feature msnFeature : msnFeatureSet) {
                        Double closestKey = DataUtils.getClosestKey(msnFeature.getMz(), massToSmiles);
                        if (closestKey == null) continue;
                        if (MathUtils.getRangeFromAbs(msnFeature.getMz(), amu).contains(closestKey)) {
                            for (String smiles : massToSmiles.get(closestKey)) {
                                Identity msnIdentity =
                                        new Identity(msnId++ + "", smiles, smiles, 0, identity.getId(), "calc.", "");
                                msnFeature.setProperty(msnIdentity);
                            }
                        }
                    }
                }
            }
            outFeatureSetContainer.addFeatureSet(featureSet);
        }

        outFeatureSetContainer.finaliseFile();
        return outFeatureSetContainer;
    }

    /**
     * Executes the Backtracker binary using the parameters defined by the parameter map.
     *
     * @param notation the molecule line notation
     * @param depth    the depth
     * @return the resulting mass to SMILES map
     */
    public TreeMap<Double, List<String>> enumerate(String notation, int depth) {

        TreeMap<Double, List<String>> massToSmiles = new TreeMap<>();
        BufferedInputStream bufStream = null;

        try {
            List<String> commands = new ArrayList<>();
            commands.add(executable);
            commands.add(notation);
            commands.add(depth + "");
            commands.add("fragonly");
            ProcessBuilder pb = new ProcessBuilder(commands);
            Process process = pb.start();

            InputStream inputStream = process.getInputStream();
            bufStream = new BufferedInputStream(inputStream);

            String line;
            TextUtils tx = new TextUtils();
            while ((line = tx.readLineFromStream(bufStream)) != null) {
                String[] elements = line.split("\\s");
                double mass = ((int) (Double.parseDouble(elements[1]) * 1000d)) / 1000d;
                String fragment = elements[2];

                if (massToSmiles.containsKey(mass)) {
                    massToSmiles.get(mass).add(fragment);
                } else {
                    List<String> smiles = new ArrayList<>();
                    smiles.add(fragment);
                    massToSmiles.put(mass, smiles);
                }
            }
        } catch (Exception exception) {
            LOGGER.log(Level.ERROR, "MSnEnumerator process error: ", exception);
        } finally {
            TextUtils.close(bufStream);
        }

        return massToSmiles;
    }
}
