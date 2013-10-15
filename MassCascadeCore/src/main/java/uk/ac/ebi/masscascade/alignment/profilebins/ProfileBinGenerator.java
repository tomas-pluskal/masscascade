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

package uk.ac.ebi.masscascade.alignment.profilebins;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.apache.commons.math3.util.FastMath;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.Trace;
import uk.ac.ebi.masscascade.interfaces.container.Container;
import uk.ac.ebi.masscascade.utilities.DataUtils;
import uk.ac.ebi.masscascade.utilities.comparator.ProfileMassComparator;
import uk.ac.ebi.masscascade.utilities.range.ToleranceRange;
import uk.ac.ebi.masscascade.utilities.xyz.XYTrace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ProfileBinGenerator {

    /**
     * Groups the profiles of all profile containers into m/z and rt bins based on the given m/z and time tolerance
     * values. Only profiles present in > missing percent of all samples are included.
     */
    public static List<ProfileBin> createBins(Multimap<Integer, Container> profileContainers, double ppm, double sec,
                                              double missing) {

        ProfileMap timeBins = group(profileContainers, ppm, sec);

        List<ProfileBin> profileBins = new ArrayList<>();
        for (List<ProfileBin> bins : timeBins.values()) {
            for (ProfileBin timeBin : bins) {
                for (int groupId : profileContainers.keySet()) {
                    if (profileContainers.get(groupId).size() - timeBin.getnProfiles(groupId)
                            <= missing * profileContainers.get(groupId).size() / 100d) {
                        profileBins.add(timeBin);
                        break;
                    }
                }
            }
        }

        return profileBins;
    }

    /**
     * Returns a multi map containing container id to profile id relation for a list of profile containers.
     *
     * @param profileContainers the profile containers
     * @param ppm               the ppm tolerance value
     * @param sec               the time tolerance value
     * @param missing           the percentage of max. missing profiles [0-100]
     * @return the container id to profile id multi map
     */
    public static HashMultimap<Integer, Integer> createContainerToProfileMap(
            Multimap<Integer, Container> profileContainers, double ppm, double sec, double missing) {

        HashMultimap<Integer, Integer> cToPIdMap = HashMultimap.create();

        ProfileMap timeBins = group(profileContainers, ppm, sec);
        for (List<ProfileBin> bins : timeBins.values()) {
            for (ProfileBin timeBin : bins) {
                for (int groupId : profileContainers.keySet()) {
                    if (profileContainers.get(groupId).size() - timeBin.getnProfiles(groupId)
                            <= missing * profileContainers.get(groupId).size() / 100d) {
                        for (Map.Entry<Integer, Integer> entry : timeBin.getContainerIndexToProfileId().entrySet()) {
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
     * Groups profile container by their m/z and time proximity. The resulting map contains averaged m/z to cross-sample
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
     * @param profileContainers the profile containers
     * @param ppm               the ppm tolerance value
     * @param sec               the time tolerance value
     * @return
     */
    public static ProfileMap group(Multimap<Integer, Container> profileContainers, double ppm, double sec) {

        int index = -1;
        ProfileMap timeBins = new ProfileMap();
        for (int groupId : profileContainers.keySet()) {
            for (Container container : profileContainers.get(groupId)) {
                List<Profile> profiles = Lists.newArrayList(container.profileIterator());
                Collections.sort(profiles, new ProfileMassComparator());
                index++;
                for (Profile profile : profiles) {
                    double rt = profile.getRetentionTime();
                    XYTrace mzTrace = new XYTrace(profile.getMzIntDp());
                    Trace closestMzTrace = DataUtils.getClosestKey(mzTrace, timeBins);

                    ProfileBin timeBin = new ProfileBin(index, groupId, profile, profileContainers.size());
                    if (closestMzTrace != null && timeBins.containsKey(closestMzTrace)) {

                        if (new ToleranceRange(closestMzTrace.getAvg(), ppm).contains(profile.getMz())) {

                            int cIndex = 0;
                            List<ProfileBin> mzTimeBins = timeBins.get(closestMzTrace);
                            ProfileBin cTimeBin = mzTimeBins.get(cIndex);
                            for (int i = 1; i < mzTimeBins.size(); i++) {
                                ProfileBin nTimeBin = mzTimeBins.get(i);
                                if (FastMath.abs(nTimeBin.getRt() - rt) < FastMath.abs(cTimeBin.getRt() - rt)) {
                                    cTimeBin = nTimeBin;
                                    cIndex = i;
                                }
                            }
                            if (cTimeBin.getRt() - sec <= rt && cTimeBin.getRt() + sec > rt) {
                                cTimeBin.add(index, groupId, profile);
                                timeBins.add(closestMzTrace, cTimeBin, cIndex);
                            } else timeBins.put(closestMzTrace, timeBin);

                            continue;
                        }
                    }
                    timeBins.put(mzTrace, timeBin);
                }
            }
        }

        return timeBins;
    }
}