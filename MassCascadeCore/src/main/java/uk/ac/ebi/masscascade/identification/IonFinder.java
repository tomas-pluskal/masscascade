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

package uk.ac.ebi.masscascade.identification;

import org.apache.log4j.Level;
import uk.ac.ebi.masscascade.core.spectrum.PseudoSpectrum;
import uk.ac.ebi.masscascade.core.spectrum.SpectrumContainer;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.ACallableTask;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.properties.Identity;
import uk.ac.ebi.masscascade.utilities.DataUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.TreeMap;

/**
 * Class implementing an ion finder method. The initial ion map is empty and needs to be populated first.
 * The input file containing the ion information must follow the following format:
 * <p/>
 * # comment line (x times)
 * 1.008,Hydrogen
 * ...
 * ...
 * <p/>
 * One or many comment lines followed by lines containing the major isotopic mass and its label in comma-separated
 * format.
 * <ul>
 * <li>Parameter <code> MZ TOLERANCE PPM </code>- The mz tolerance in ppm.</li>
 * <li>Parameter <code> ADDUCT LIST </code>- The adducts to be searched for.</li>
 * <li>Parameter <code> SPECTRUM FILE </code>- The input spectrum container.</li>
 * </ul>
 */
public class IonFinder extends ACallableTask {

    private SpectrumContainer spectrumContainer;
    private double massTolerance;
    private TreeMap<Double, String> ionMzs = new TreeMap<Double, String>();

    /**
     * Text constants for the adduct file format.
     */
    private static final String COMMENT = "#";
    private static final String SEPARATOR = ",";

    /**
     * Sets a new ion map defining the adduct detector.
     *
     * @param ionFile the file containing the ions
     */
    public void setIonList(File ionFile) {

        try {
            Reader ionFileReader = new FileReader(ionFile);
            BufferedReader br = new BufferedReader(ionFileReader);

            readIonList(br);
        } catch (FileNotFoundException exception) {
            LOGGER.log(Level.WARN, "Ion input file not found. " + exception.getMessage());
        }
    }

    /**
     * Sets a new ion list.
     *
     * @param ionMzs the ion list
     */
    public void setIonList(TreeMap<Double, String> ionMzs) {

        this.ionMzs = ionMzs;
    }

    /**
     * Reads the file line by line and parses the 'mass,label' records.
     */
    private void readIonList(BufferedReader br) {

        try {
            String line = "";
            while ((line = br.readLine()) != null) {

                if (line.startsWith(COMMENT)) continue;
                String[] lineElements = line.split(SEPARATOR);
                if (lineElements.length != 2) throw new IOException("Wrong format.");

                ionMzs.put(Double.parseDouble(lineElements[1]), lineElements[0]);
            }
        } catch (Exception exception) {
            LOGGER.log(Level.INFO, "Ion record not readable. " + exception.getMessage());
        }
    }

    /**
     * Constructor for a ion finder task.
     *
     * @param params the parameter map
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public IonFinder(ParameterMap params) throws MassCascadeException {

        super(IonFinder.class);

        setParameters(params);
    }

    /**
     * Sets the parameters for the ion finder task.
     *
     * @param params the new parameter values
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public void setParameters(ParameterMap params) throws MassCascadeException {

        spectrumContainer = params.get(Parameter.SPECTRUM_CONTAINER, SpectrumContainer.class);
        massTolerance = params.get(Parameter.MZ_WINDOW_PPM, Double.class);
        setIonList(params.get(Parameter.ADDUCT_LIST, (new TreeMap<Double, String>()).getClass()));
    }

    /**
     * Executes the ion finder task.
     *
     * @return the annotated spectrum container
     */
    public SpectrumContainer call() {

        String id = spectrumContainer.getId() + IDENTIFIER;
        SpectrumContainer outSpectrumContainer = new SpectrumContainer(id, spectrumContainer.getWorkingDirectory());

        double result;
        for (PseudoSpectrum spectrum : spectrumContainer) {

            for (Profile profile : spectrum) {

                double mz = profile.getMzIntDp().x;

                result = DataUtils.getNearestIndexRel(mz, massTolerance, ionMzs.keySet());

                if (result != -1) {

                    double score = Math.abs(mz - result) * Constants.PPM / mz;
                    Identity identity = new Identity("", ionMzs.get(result), "", score);
                    profile.setProperty(identity);
                }
            }
            outSpectrumContainer.addSpectrum(spectrum);
        }

        outSpectrumContainer.finaliseFile();

        return outSpectrumContainer;
    }
}
