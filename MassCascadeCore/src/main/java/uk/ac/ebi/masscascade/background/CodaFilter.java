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

package uk.ac.ebi.masscascade.background;

import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Feature;
import uk.ac.ebi.masscascade.interfaces.container.FeatureContainer;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.properties.Score;
import uk.ac.ebi.masscascade.utilities.xyz.XYZList;

/**
 * Class for feature selection using the CODA algorithm.
 * <p/>
 * Calculates the mass chromatographic quality (MCQ) score for all profiles.
 * Profiles below the threshold will be removed.
 * <ul>
 * <li>Parameter <code> CODA </code>- The CODA threshold.</li>
 * <li>Parameter <code> DATA_WINDOW </code>- The size of the rectangular smoothing window.</li>
 * <li>Parameter <code> FEATURE_CONTAINER </code>- The input feature container.</li>
 * </ul>
 */
@Deprecated
public class CodaFilter extends CallableTask {

    private double mcqThreshold;
    private int windowSize;
    private FeatureContainer featureContainer;

    /**
     * Constructs a coda filter task.
     *
     * @param params the parameter map holding all required task parameters
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the task fails
     */
    public CodaFilter(ParameterMap params) throws MassCascadeException {

        super(CodaFilter.class);
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

        mcqThreshold = params.get(Parameter.CODA, Double.class);
        windowSize = params.get(Parameter.SCAN_WINDOW, Integer.class);
        featureContainer = params.get(Parameter.FEATURE_CONTAINER, FeatureContainer.class);
    }

    /**
     * Executes the task. The <code> Callable </code> returns a {@link uk.ac.ebi.masscascade.interfaces.container
     * .FeatureContainer} with the processed data.
     *
     * @return the feature container with the processed data
     */
    @Override
    public FeatureContainer call() {

        String id = featureContainer.getId() + IDENTIFIER;
        FeatureContainer outFeatureContainer = featureContainer.getBuilder().newInstance(FeatureContainer.class, id,
                featureContainer.getIonMode(), featureContainer.getWorkingDirectory());

        for (Feature feature : featureContainer) {
            double mcq = getMCQ(feature.getData());
            if (mcq >= mcqThreshold) {
                feature.setProperty(new Score("mcq", mcq));
                outFeatureContainer.addFeature(feature);
            }
        }

        outFeatureContainer.finaliseFile();
        return outFeatureContainer;
    }

    /**
     * Calculates the MCQ value from the given data list.
     *
     * @param data a data list
     * @return the MCQ score
     */
    private double getMCQ(XYZList data) {

        double mean = 0;
        double n = 0;
        double m2 = 0;

        double scaleFactor = 0;

        if (data.size() - windowSize - 3 <= 0) return 0;
        double[] intensities = new double[data.size() - windowSize - 2];
        for (int i = 1; i <= data.size() - windowSize - 2; i++) {
            double intensity = 0;
            for (int j = 0; j < windowSize; j++) intensity += data.get(i + j).z;
            intensity /= windowSize;

            scaleFactor += data.get(i).z * data.get(i).z;
            // Welford's method
            n += 1;
            double delta = intensity - mean;
            mean += delta / n;
            m2 += delta * (intensity - mean);

            intensities[i - 1] = intensity;
        }

        for (int i = data.size() - windowSize; i < data.size() - 1; i++) scaleFactor += data.get(i).z * data.get(i).z;
        scaleFactor = Math.sqrt(scaleFactor);

        double stdDev = Math.sqrt(m2 / (n - 1));
        double mcq = 0;

        for (int i = 0; i < intensities.length; i++)
            mcq += (data.get(i + 1).z / scaleFactor) * ((intensities[i] - mean) / stdDev);

        if (mcq < 0) mcq = 0;
        return mcq / Math.sqrt(data.size() - windowSize);
    }
}
