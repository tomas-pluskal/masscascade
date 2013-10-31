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

import com.google.common.collect.TreeMultimap;
import uk.ac.ebi.masscascade.core.feature.FeatureImpl;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Feature;
import uk.ac.ebi.masscascade.interfaces.container.FeatureContainer;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.DataUtils;
import uk.ac.ebi.masscascade.utilities.xyz.XYZPoint;
import uk.ac.ebi.masscascade.utilities.range.ToleranceRange;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Class implementing a mass trace compilation method that builds a mass trace from a set of profiles.
 * <p/>
 * All profiles that fall within the defined mass range are grouped together and form the mass trace.
 * <ul>
 * <li>Parameter <code> MZ WINDOW PPM </code>- The mass window in ppm.</li>
 * <li>Parameter <code> PROFILE FILE </code>- The input feature container.</li>
 * </ul>
 */
@Deprecated
public class ProfileJoiner extends CallableTask {

    private FeatureContainer featureContainer;
    private double ppm;
    private int id;

    /**
     * Constructs a feature joiner task.
     *
     * @param params the parameter map holding all required task parameters
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the task fails
     */
    public ProfileJoiner(ParameterMap params) throws MassCascadeException {

        super(ProfileJoiner.class);

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
        featureContainer = params.get(Parameter.FEATURE_CONTAINER, FeatureContainer.class);
    }

    /**
     * Executes the task. The <code> Callable </code> returns a {@link uk.ac.ebi.masscascade.interfaces.container
     * .ScanContainer} with the processed data.
     *
     * @return the scan container with the processed data
     */
    @Override
    public FeatureContainer call() {

        // map a list of feature ids to the m/z of the feature.
        TreeMultimap<Double, Integer> mzIdMap = TreeMultimap.create();

        for (Feature feature : featureContainer) resortProfile(feature, mzIdMap);

        return buildJoinedProfiles(mzIdMap);
    }

    /**
     * Adds or inserts a feature to a mass trace.
     *
     * @param trace   the feature to be added
     * @param mzIdMap the map of m/z-id relations
     */
    private void resortProfile(Feature trace, TreeMultimap<Double, Integer> mzIdMap) {

        double mz = trace.getMz();
        Double mzScanKey = DataUtils.getClosestValue(mz, new TreeSet<Double>(mzIdMap.keySet()));

        if (mzScanKey == null) mzIdMap.put(mz, trace.getId());
        else if (new ToleranceRange(mzScanKey, ppm).contains(mz)) mzIdMap.get(mzScanKey).add(trace.getId());
        else mzIdMap.put(mz, trace.getId());
    }

    /**
     * Compiles the extracted mass traces based on the sorted profiles.
     *
     * @param mzIdMap the map of mass-scan id relations
     * @return the extracted mass traces
     */
    private FeatureContainer buildJoinedProfiles(TreeMultimap<Double, Integer> mzIdMap) {

        String id = featureContainer.getId() + IDENTIFIER;
        FeatureContainer outFeatureContainer = featureContainer.getBuilder().newInstance(FeatureContainer.class, id,
                featureContainer.getIonMode(), featureContainer.getWorkingDirectory());

        for (double mz : mzIdMap.keySet()) {
            Range mzRange = new ToleranceRange(mz, ppm);
            outFeatureContainer.addFeature(buildProfile(mzIdMap.get(mz), mzRange));
        }

        outFeatureContainer.finaliseFile();
        return outFeatureContainer;
    }

    /**
     * Creates a new mass trace based on the list of feature ids.
     *
     * @param ids     the list of feature ids making up the mass trace
     * @param mzRange the interval of mz covered by the trace
     * @return the mass trace
     */
    private Feature buildProfile(SortedSet<Integer> ids, Range mzRange) {

        Feature feature;
        Map<Double, XYZPoint> dataMap = new TreeMap<Double, XYZPoint>();

        for (int traceId : ids) {
            feature = featureContainer.getFeature(traceId);

            for (XYZPoint dp : feature.getData()) {

                if (dataMap.containsKey(dp.x)) {
                    double avgInt = Math.max(dp.z, dataMap.get(dp.x).z);
                    double avgMz = (dp.z == 0) ? dataMap.get(dp.x).y : (dataMap.get(dp.x).y + dp.y) / 2d;
                    dataMap.put(dp.x, new XYZPoint(dp.x, avgMz, avgInt));
                } else dataMap.put(dp.x, dp);
            }
        }

        Feature joinedFeature = null;
        for (double rt : dataMap.keySet()) {
            if (joinedFeature == null) joinedFeature = new FeatureImpl(id, dataMap.get(rt), mzRange);
            else joinedFeature.addFeaturePoint(dataMap.get(rt));
        }
        assert joinedFeature != null;
        joinedFeature.closeFeature();

        id++;

        return joinedFeature;
    }
}
