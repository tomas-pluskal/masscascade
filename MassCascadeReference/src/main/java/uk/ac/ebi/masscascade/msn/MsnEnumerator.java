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
import uk.ac.ebi.masscascade.core.PropertyManager;
import uk.ac.ebi.masscascade.interfaces.CallableSearch;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.Property;
import uk.ac.ebi.masscascade.interfaces.Spectrum;
import uk.ac.ebi.masscascade.interfaces.container.Container;
import uk.ac.ebi.masscascade.interfaces.container.SpectrumContainer;
import uk.ac.ebi.masscascade.library.LibraryParameter;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.properties.Identity;
import uk.ac.ebi.masscascade.utilities.DataUtils;
import uk.ac.ebi.masscascade.utilities.TextUtils;
import uk.ac.ebi.masscascade.utilities.math.MathUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
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
 * <li>Parameter <code> SPECTRUM CONTAINER </code>- The input spectrum container.</li>
 * </ul>
 */
public class MsnEnumerator extends CallableSearch {

    private String executable;
    private int depth;
    private double amu;
    private SpectrumContainer spectrumContainer;

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

        spectrumContainer = params.get(Parameter.SPECTRUM_CONTAINER, SpectrumContainer.class);
        depth = params.get(LibraryParameter.DEPTH, Integer.class);
        amu = params.get(Parameter.MZ_WINDOW_AMU, Double.class);
        executable = params.get(Parameter.EXECUTABLE, String.class);
    }

    /**
     * Executes the task. The <code> Callable </code> returns a {@link uk.ac.ebi.masscascade.interfaces.container
     * .SpectrumContainer} with the processed data.
     *
     * @return the spectrum container with the processed data
     */
    @Override
    public Container call() {

        String id = spectrumContainer.getId() + IDENTIFIER;
        SpectrumContainer outSpectrumContainer = spectrumContainer.getBuilder().newInstance(SpectrumContainer.class, id,
                spectrumContainer.getWorkingDirectory());

        for (Spectrum spectrum : spectrumContainer) {
            int msnId = 0;
            for (Profile profile : spectrum) {
                if (!(profile.hasProperty(PropertyManager.TYPE.Identity) && profile.hasMsnSpectra(Constants.MSN.MS2)))
                    continue;

                Spectrum msnSpectrum = profile.getMsnSpectra(Constants.MSN.MS2).get(0);
                for (Property property : profile.getProperty(PropertyManager.TYPE.Identity)) {
                    Identity identity = (Identity) property;
                    String notation = identity.getNotation();
                    if (notation == null || notation.isEmpty()) continue;

                    TreeMap<Double, List<String>> massToSmiles = enumerate(notation, depth);
                    for (Profile msnProfile : msnSpectrum) {
                        Double closestKey = DataUtils.getClosestKey(msnProfile.getMz(), massToSmiles);
                        if (closestKey == null) continue;
                        if (MathUtils.getRangeFromAbs(msnProfile.getMz(), amu).contains(closestKey)) {
                            for (String smiles : massToSmiles.get(closestKey)) {
                                Identity msnIdentity =
                                        new Identity(msnId++ + "", smiles, smiles, 0, identity.getId(), "calc.", "");
                                msnProfile.setProperty(msnIdentity);
                            }
                        }
                    }
                }
            }
            outSpectrumContainer.addSpectrum(spectrum);
        }

        outSpectrumContainer.finaliseFile();
        return outSpectrumContainer;
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
            ProcessBuilder pb = new ProcessBuilder(commands);
            Process process = pb.start();

            InputStream inputStream = process.getInputStream();
            bufStream = new BufferedInputStream(inputStream);

            String line;
            TextUtils tx = new TextUtils();
            while ((line = tx.readLineFromStream(bufStream)) != null) {
                String[] elements = line.split(":");
                double mass = Double.parseDouble(elements[2]);
                String fragment = elements[3];

                if (massToSmiles.containsKey(mass)) {
                    massToSmiles.get(mass).add(fragment);
                } else {
                    List<String> smiles = new ArrayList<>();
                    smiles.add(fragment);
                    massToSmiles.put(mass, smiles);
                }
            }
        } catch (IOException exception) {
            LOGGER.log(Level.ERROR, "MSnEnumerator process error: ", exception);
        } finally {
            TextUtils.close(bufStream);
        }

        return massToSmiles;
    }
}
