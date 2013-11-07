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

package uk.ac.ebi.masscascade.distance;

import uk.ac.ebi.masscascade.core.featureset.FeatureSetImpl;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Feature;
import uk.ac.ebi.masscascade.interfaces.FeatureSet;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.interfaces.container.FeatureContainer;
import uk.ac.ebi.masscascade.interfaces.container.FeatureSetContainer;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYZList;
import uk.ac.ebi.masscascade.utilities.xyz.XYZPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

/**
 * Groups all features into feature sets based on their retention time and feature using a modified Biehman approach
 * to component perception.
 * <ul>
 * <li>Parameter <code> FEATURE_CONTAINER </code>- The input scan container.</li>
 * <li>Parameter <code> BINS </code>- The number of bins.</li>
 * <li>Parameter <code> TIME WINDOW </code>- The approximate distance between two scans in seconds.</li>
 * </ul>
 */
public class BiehmanSimilarity extends CallableTask {

    private FeatureContainer featureContainer;
    private FeatureSetContainer featureSetContainer;

    private int binNumber;
    private double scanDistance;

    private static final int N_MAX = 3;

    /**
     * Constructs a Biemann similarity task.
     *
     * @param params the parameter map holding all required task parameters
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the task fails
     */
    public BiehmanSimilarity(ParameterMap params) throws MassCascadeException {

        super(BiehmanSimilarity.class);
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

        featureContainer = params.get(Parameter.FEATURE_CONTAINER, FeatureContainer.class);
        binNumber = params.get(Parameter.BINS, Integer.class);
        scanDistance = params.get(Parameter.TIME_WINDOW, Double.class);
    }

    /**
     * Executes the task. The <code> Callable </code> returns a {@link uk.ac.ebi.masscascade.interfaces.container
     * .ScanContainer} with the processed data.
     *
     * @return the featureset container with the processed data
     */
    @Override
    public FeatureSetContainer call() {

        String id = featureContainer.getId() + IDENTIFIER;
        featureSetContainer = featureContainer.getBuilder().newInstance(FeatureSetContainer.class, id,
                featureContainer.getIonMode(), featureContainer.getWorkingDirectory());

        SortedSet<Double> rtSet = featureContainer.getTimes().keySet();
        Range rtRange = new ExtendableRange(rtSet.first(), rtSet.last());

        double binWidth = scanDistance / binNumber;
        int noOfBins = (int) (rtRange.getSize() / binWidth) + 1;
        Bin[] bins = new Bin[noOfBins];

        Set<Integer> allFeatureIds = new HashSet<>();

        for (Feature feature : featureContainer) {
            allFeatureIds.add(feature.getId());
            XYZList featureData = feature.getData();

            int traceMax = 0;
            XYZPoint pDp = featureData.get(traceMax);
            for (XYZPoint dp : featureData) {
                if (dp.x > feature.getRetentionTime()) {
                    if (dp.x - feature.getRetentionTime() > feature.getRetentionTime() - pDp.x) traceMax--;
                    break;
                }
                pDp = dp;
                traceMax++;
            }

            int reachMax = 1;
            double sharpnessMaxL = 0;
            for (int leftI = traceMax - 1; leftI > 0; leftI++) {

                sharpnessMaxL = Math.max(sharpnessMaxL,
                        featureData.get(traceMax).z - featureData.get(leftI).z) / (reachMax * Math.sqrt(
                        featureData.get(traceMax).z));

                if (reachMax == N_MAX) break;
                reachMax++;
            }

            reachMax = 1;
            double sharpnessMaxR = 0;
            for (int rightI = traceMax + 1; rightI < featureData.size(); rightI++) {

                sharpnessMaxR = Math.max(sharpnessMaxR,
                        featureData.get(traceMax).z - featureData.get(rightI).z) / (reachMax * Math.sqrt(
                        featureData.get(traceMax).z));

                if (reachMax == N_MAX) break;
                reachMax++;
            }

            int binIndex = (int) ((feature.getRetentionTime() - rtRange.getLowerBounds()) / binWidth);

            if (bins[binIndex] == null) bins[binIndex] = new Bin((sharpnessMaxL + sharpnessMaxR) / 2d, feature.getId());
            else bins[binIndex].add((sharpnessMaxL + sharpnessMaxR) / 2d, feature.getId());
        }

        int index = 1;
        boolean isComponent = false;
        for (int i = 1; i < bins.length - 1; i++) {

            if (isComponent) {
                isComponent = false;
                continue;
            }
            isComponent = true;

            if (bins[i] == null) continue;

            double m = bins[i].getValue();
            double l = bins[i - 1] == null ? 0 : bins[i - 1].getValue();
            double r = bins[i + 1] == null ? 0 : bins[i + 1].getValue();

            if (m > l && m > r) {
                int reach = (int) Math.round((binNumber / (m + l + r)) * 10);

                int reachMax = 1;
                for (int down = i - 1; down >= 0; down--) {

                    l = bins[down] == null ? 0 : bins[down].getValue();
                    if (l > m) isComponent = false;

                    if (reachMax == reach) break;
                    reachMax++;
                }

                reachMax = 1;
                for (int up = i + 1; up < bins.length; up++) {

                    r = bins[up] == null ? 0 : bins[up].getValue();
                    if (r > m) isComponent = false;

                    if (reachMax == reach) break;
                    reachMax++;
                }
            }

            if (isComponent) {
                Set<Feature> features = new HashSet<>();

                Range specRange = new ExtendableRange();
                XYList featureSetData = new XYList();
                double rt = 0;

                List<Integer> tmpIds = new ArrayList<>();
                tmpIds.addAll(bins[i].getFeatureIds());
                if (bins[i - 1] != null) tmpIds.addAll(bins[i - 1].getFeatureIds());
                if (bins[i + 1] != null) tmpIds.addAll(bins[i + 1].getFeatureIds());

                Feature feature;
                for (int featureId : tmpIds) {
                    feature = featureContainer.getFeature(featureId);
                    features.add(feature);
                    featureSetData.add(feature.getMzIntDp());
                    specRange.extendRange(feature.getRetentionTime());
                    rt += feature.getRetentionTime();
                }

                rt /= tmpIds.size();
                Collections.sort(featureSetData);

                FeatureSet featureSet = new FeatureSetImpl(index, featureSetData, specRange, rt, features);
                featureSetContainer.addFeatureSet(featureSet);

                allFeatureIds.removeAll(tmpIds);
                index++;
            }
        }

        Feature feature;
        for (int featureId : allFeatureIds) {

            feature = featureContainer.getFeature(featureId);
            Set<Feature> featureSet = new HashSet<Feature>();
            featureSet.add(feature);

            double rt = feature.getRetentionTime();
            Range specRange = new ExtendableRange(rt);

            XYList featureSetData = new XYList();
            featureSetData.add(feature.getMzIntDp());

            FeatureSet pseudoFeatureSet = new FeatureSetImpl(index, featureSetData, specRange, rt, featureSet);
            featureSetContainer.addFeatureSet(pseudoFeatureSet);

            index++;
        }

        featureSetContainer.finaliseFile();
        return featureSetContainer;
    }

    /**
     * Inner class representing a signal bin on the time axis.
     */
    class Bin {

        private double value;
        private List<Integer> featureIds;

        public Bin() {

            value = 0;
            featureIds = new ArrayList<>();
        }

        public Bin(double value, int featureId) {

            this.value = value;

            this.featureIds = new ArrayList<>();
            featureIds.add(featureId);
        }

        public void add(double value, int featureId) {

            this.value += value;
            this.featureIds.add(featureId);
        }

        public double getValue() {
            return value;
        }

        public List<Integer> getFeatureIds() {
            return featureIds;
        }
    }
}


