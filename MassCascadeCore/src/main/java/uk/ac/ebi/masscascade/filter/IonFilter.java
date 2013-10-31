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

package uk.ac.ebi.masscascade.filter;

import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Feature;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.interfaces.container.FeatureContainer;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.DataUtils;
import uk.ac.ebi.masscascade.utilities.range.ToleranceRange;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * Class implementing a mass filter. Pre-defined ion masses are detected and removed from the feature collection.
 * <ul>
 * <li>Parameter <code> MZ_WINDOW_PPM </code>- The mz window in ppm.</li>
 * <li>Parameter <code> MASSES_FOR_REMOVAL </code>- The mz values that should be removed.</li>
 * <li>Parameter <code> FEATURE_FILE </code>- The input feature container.</li>
 * </ul>
 */
public class IonFilter extends CallableTask {

    // task variables
    private double ppm;
    private TreeSet<Double> mzForRemoval;
    private Map<Double, Range> mzRanges;
    private FeatureContainer sampleContainer;

    /**
     * Constructs a mass filter task.
     *
     * @param params the parameter map holding all required task parameters
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the task fails
     */
    public IonFilter(ParameterMap params) throws MassCascadeException {

        super(IonFilter.class);
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

        ppm = params.get(Parameter.MZ_WINDOW_PPM, Double.class);
        mzForRemoval = params.get(Parameter.MZ_FOR_REMOVAL, (new TreeSet<Double>()).getClass());
        sampleContainer = params.get(Parameter.FEATURE_CONTAINER, FeatureContainer.class);

        mzRanges = new HashMap<Double, Range>();
        for (double mz : mzForRemoval)
            mzRanges.put(mz, new ToleranceRange(mz, ppm));
    }

    /**
     * Executes the task. The <code> Callable </code> returns a {@link uk.ac.ebi.masscascade.interfaces.container
     * .ScanContainer} with the processed data.
     *
     * @return the feature container with the processed data
     */
    @Override
    public FeatureContainer call() {

        String id = sampleContainer.getId() + IDENTIFIER;
        FeatureContainer featureContainer = sampleContainer.getBuilder().newInstance(FeatureContainer.class, id,
                sampleContainer.getIonMode(), sampleContainer.getWorkingDirectory());

        for (Feature feature : sampleContainer) {

            Double mz = DataUtils.getClosestValue(feature.getMz(), mzForRemoval);
            if (mz == null) featureContainer.addFeature(feature);
            else if (!(mzForRemoval.contains(mz) && mzRanges.get(mz).contains(feature.getMz())))
                featureContainer.addFeature(feature);
        }
        featureContainer.finaliseFile();

        return featureContainer;
    }
}
