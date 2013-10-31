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

package uk.ac.ebi.masscascade.featurebuilder;

import uk.ac.ebi.masscascade.core.feature.FeatureImpl;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Feature;
import uk.ac.ebi.masscascade.interfaces.container.FeatureContainer;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.xyz.XYZList;
import uk.ac.ebi.masscascade.utilities.xyz.XYZPoint;

/**
 * Class implementing a mass trace splitter method.
 * <p/>
 * The traces are searched for zero intensity values which serve as boundary signal to define separate profiles.
 * <ul>
 * <li>Parameter <code> PROFILE FILE </code>- The input feature container.</li>
 * </ul>
 */
@Deprecated
public class ProfileSplitter extends CallableTask {

    private FeatureContainer featureContainer;
    private FeatureContainer outFeatureContainer;
    private int id;

    /**
     * Constructs a trace splitter task.
     *
     * @param params the parameter map holding all required task parameters
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the task fails
     */
    public ProfileSplitter(ParameterMap params) throws MassCascadeException {

        super(ProfileSplitter.class);
        setParameters(params);
        id = 0;
    }

    /**
     * Sets the task class variables using the parameter map.
     *
     * @param params the parameter map containing the <code> Parameter </code> to <code> Object </code> relations.
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the parameter map does not contain all variables required by this class
     */
    public void setParameters(ParameterMap params) throws MassCascadeException {
        featureContainer = params.get(Parameter.FEATURE_CONTAINER, FeatureContainer.class);
    }

    /**
     * Executes the task. The <code> Callable </code> returns a {@link uk.ac.ebi.masscascade.interfaces.container
     * .ScanContainer} with the processed data.
     *
     * @return the feature container with the processed data
     */
    @Override
    public FeatureContainer call() {

        String id = featureContainer.getId() + IDENTIFIER;
        outFeatureContainer = featureContainer.getBuilder().newInstance(FeatureContainer.class, id,
                featureContainer.getIonMode(), featureContainer.getWorkingDirectory());

        for (Feature feature : featureContainer) extractTraces(feature);

        outFeatureContainer.finaliseFile();
        return outFeatureContainer;
    }

    /**
     * Extracts the profiles from the mass trace using zero intensity values as boundary condition.
     *
     * @param inFeature the mass trace containing the profiles
     */
    private void extractTraces(Feature inFeature) {

        XYZPoint dp;
        boolean isNew = true;
        Feature partFeature = null;
        XYZList data = inFeature.getData();

        for (int i = 0; i < data.size(); i++) {
            dp = data.get(i);
            if (isNew) {
                if (dp.z != 0 && partFeature != null) {
                    partFeature = new FeatureImpl(id, data.get(i - 1), inFeature.getMzRange());
                    partFeature.addFeaturePoint(dp);
                } else partFeature = new FeatureImpl(id, dp, inFeature.getMzRange());
                id++;
                isNew = false;
            } else partFeature.addFeaturePoint(dp);

            if (dp.z == 0 && partFeature.getData().size() > 1) {
                partFeature.closeFeature();
                outFeatureContainer.addFeature(partFeature);
                isNew = true;
            }
        }
    }
}
