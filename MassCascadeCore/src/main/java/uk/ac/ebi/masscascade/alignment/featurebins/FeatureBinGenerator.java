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

package uk.ac.ebi.masscascade.alignment.featurebins;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.apache.commons.math3.util.FastMath;
import uk.ac.ebi.masscascade.interfaces.Feature;
import uk.ac.ebi.masscascade.interfaces.Trace;
import uk.ac.ebi.masscascade.interfaces.container.Container;
import uk.ac.ebi.masscascade.utilities.DataUtils;
import uk.ac.ebi.masscascade.utilities.TextUtils;
import uk.ac.ebi.masscascade.utilities.comparator.FeatureMassComparator;
import uk.ac.ebi.masscascade.utilities.range.ToleranceRange;
import uk.ac.ebi.masscascade.utilities.xyz.XYTrace;

import java.util.*;

public class FeatureBinGenerator {

    /**
     * Groups the features of all feature containers into m/z and rt bins based on the given m/z and time tolerance
     * values. Only features present in > missing percent of all samples are included.
     */
    public static List<FeatureBin> createBins(Multimap<Integer, Container> featureContainers, double ppm, double sec,
                                              double missing) {

        FeatureMap timeBins = group(featureContainers, ppm, sec);

        List<FeatureBin> featureBins = new ArrayList<>();
        for (List<FeatureBin> bins : timeBins.values()) {
            for (FeatureBin timeBin : bins) {
                for (int groupId : featureContainers.keySet()) {
                    if (featureContainers.get(groupId).size() - timeBin.getnFeatures(groupId)
                            <= missing * featureContainers.get(groupId).size() / 100d) {
                        featureBins.add(timeBin);
                        break;
                    }
                }
            }
        }

        return featureBins;
    }

    /**
     * Returns a multi map containing container id to feature id relation for a list of feature containers.
     *
     * @param featureContainers the feature containers
     * @param ppm               the ppm tolerance value
     * @param sec               the time tolerance value
     * @param missing           the percentage of max. missing features [0-100]
     * @return the container id to feature id multi map
     */
    public static HashMultimap<Integer, Integer> createContainerToFeatureMap(
            Multimap<Integer, Container> featureContainers, double ppm, double sec, double missing) {

        HashMultimap<Integer, Integer> cToPIdMap = HashMultimap.create();

        FeatureMap timeBins = group(featureContainers, ppm, sec);
        for (List<FeatureBin> bins : timeBins.values()) {
            for (FeatureBin timeBin : bins) {
                for (int groupId : featureContainers.keySet()) {
                    if (featureContainers.get(groupId).size() - timeBin.getnFeatures(groupId)
                            <= missing * featureContainers.get(groupId).size() / 100d) {
                        for (Map.Entry<Integer, Integer> entry : timeBin.getContainerIndexToFeatureId().entrySet()) {
                            cToPIdMap.put(entry.getKey(), entry.getValue());
                        }
                        break;
                    }
                }
            }
        }

        return cToPIdMap;
    }

    /**
     * Groups feature container by their m/z and time proximity. The resulting map contains averaged m/z to cross-sample
     * m/z associations for specific times.
     *
     *                  s_1   s_2   s_3
     * avg mz_1 - rt_a   x     x     x
     *            rt_b   x           x
     *            rt_c   x     x
     * avg mz_2 - rt_a   x           x
     *            rt_b   x     x     x
     * avg mz_3 - rt_a   x     x     x
     *
     * @param featureContainers the feature containers
     * @param ppm               the ppm tolerance value
     * @param sec               the time tolerance value
     * @return
     */
    public static FeatureMap group(Multimap<Integer, Container> featureContainers, double ppm, double sec) {

        int index = -1;
        FeatureMap timeBins = new FeatureMap();
        for (int groupId : featureContainers.keySet()) {
            List<Container> featureCs = new ArrayList<>(featureContainers.get(groupId));

            Collections.sort(featureCs, new Comparator<Container>() { // really needs to be changed, dirty hack

                @Override
                public int compare(Container o1, Container o2) {
                    return o1.getId().compareTo(o2.getId());
                }
            });

            // iterate over every container in the group
            for (Container container : featureContainers.get(groupId)) {
                // get all features...
                List<Feature> features = Lists.newArrayList(container.featureIterator());
                // and sort them by m/z in ascending order
                Collections.sort(features, new FeatureMassComparator());
                index++;
                // itearte over every feature
                for (Feature feature : features) {
                    // get the retention time,
                    double rt = feature.getRetentionTime();
                    // and representative m/z signal
                    XYTrace mzTrace = new XYTrace(feature.getMzIntDp());
                    // search for the closest feature trace in the trace map
                    Trace closestMzTrace = DataUtils.getClosestKey(mzTrace, timeBins);

                    // create a new feature bin
                    FeatureBin timeBin = new FeatureBin(index, groupId, feature, featureContainers.size());
                    // if there is a proximate trace
                    if (closestMzTrace != null && timeBins.containsKey(closestMzTrace)) {

                        if (new ToleranceRange(closestMzTrace.getAvg(), ppm).contains(feature.getMz())) {

                            int cIndex = 0;
                            List<FeatureBin> mzTimeBins = timeBins.get(closestMzTrace);
                            FeatureBin cTimeBin = mzTimeBins.get(cIndex);
                            for (int i = 1; i < mzTimeBins.size(); i++) {
                                FeatureBin nTimeBin = mzTimeBins.get(i);
                                if (FastMath.abs(nTimeBin.getRt() - rt) < FastMath.abs(cTimeBin.getRt() - rt)) {
                                    cTimeBin = nTimeBin;
                                    cIndex = i;
                                }
                            }
                            if (cTimeBin.getRt() - sec <= rt && cTimeBin.getRt() + sec > rt) {
                                cTimeBin.add(index, groupId, feature);
                                timeBins.add(closestMzTrace, cTimeBin, cIndex);
                            } else {
                                timeBins.put(closestMzTrace, timeBin);
                            }

                            continue;
                        }
                    }
                    // else add to the trace map
                    timeBins.put(mzTrace, timeBin);
                }
            }
        }

        return timeBins;
    }
}