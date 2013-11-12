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

package uk.ac.ebi.masscascade.identification;

import org.apache.commons.math3.util.FastMath;
import org.apache.log4j.Level;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Feature;
import uk.ac.ebi.masscascade.interfaces.FeatureSet;
import uk.ac.ebi.masscascade.interfaces.container.FeatureSetContainer;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.properties.Identity;
import uk.ac.ebi.masscascade.utilities.DataUtils;
import uk.ac.ebi.masscascade.utilities.range.ToleranceRange;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
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
 * <li>Parameter <code> MZ_TOLERANCE_PPM </code>- The mz tolerance in ppm.</li>
 * <li>Parameter <code> EXACT_MASS_LIST </code>- The adducts to be searched for.</li>
 * <li>Parameter <code> FEATURE_SET_CONTAINER </code>- The input feature set container.</li>
 * </ul>
 */
public class IonFinder extends CallableTask {

    private FeatureSetContainer featureSetContainer;
    private double ppm;
    private TreeMap<Double, String> ionMzs = new TreeMap<>();

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
     * @param exMass the ion list
     */
    public void setExactMassList(TreeMap<Double, String> exMass) {

        double delta = 0;
        if (featureSetContainer.getIonMode() == Constants.ION_MODE.POSITIVE) {
            delta = Constants.PARTICLES.PROTON.getMass();
        } else if (featureSetContainer.getIonMode() == Constants.ION_MODE.NEGATIVE) {
            delta = Constants.PARTICLES.PROTON.getMass() * -1;
        }

        TreeMap<Double, String> adductMap = new TreeMap<>();
        for (Map.Entry<Double, String> entry : exMass.entrySet()) {
            adductMap.put(entry.getKey() + delta, entry.getValue());
        }
        this.ionMzs = adductMap;
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
     * Constructs an ion finder task.
     *
     * @param params the parameter map holding all required task parameters
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the task fails
     */
    public IonFinder(ParameterMap params) throws MassCascadeException {

        super(IonFinder.class);

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

        featureSetContainer = params.get(Parameter.FEATURE_SET_CONTAINER, FeatureSetContainer.class);
        ppm = params.get(Parameter.MZ_WINDOW_PPM, Double.class);
        setExactMassList(params.get(Parameter.EXACT_MASS_LIST, TreeMap.class));
    }

    /**
     * Executes the task. The <code> Callable </code> returns a {@link uk.ac.ebi.masscascade.interfaces.container
     * .ScanContainer} with the aprocessed data.
     *
     * @return the featureset container with the processed data
     */
    @Override
    public FeatureSetContainer call() {

        String id = featureSetContainer.getId() + IDENTIFIER;
        FeatureSetContainer outFeatureSetContainer = featureSetContainer.getBuilder().newInstance(FeatureSetContainer.class, id,
                featureSetContainer.getIonMode(), featureSetContainer.getWorkingDirectory());

        Double closestIon;
        for (FeatureSet featureSet : featureSetContainer) {
            for (Feature feature : featureSet) {
                double mz = feature.getMzIntDp().x;

                closestIon = DataUtils.getClosestKey(mz, ionMzs);
                if (closestIon != null && new ToleranceRange(mz, ppm).contains(closestIon)) {
                    double score = FastMath.abs(mz - closestIon) * Constants.PPM / mz;
                    score = FastMath.round(-0.001 * score + 1000);
                    Identity identity = new Identity("", ionMzs.get(closestIon), "", score, "IonFinder", "m/z", "");
                    feature.setProperty(identity);
                }
            }
            outFeatureSetContainer.addFeatureSet(featureSet);
        }

        outFeatureSetContainer.finaliseFile();
        return outFeatureSetContainer;
    }
}
