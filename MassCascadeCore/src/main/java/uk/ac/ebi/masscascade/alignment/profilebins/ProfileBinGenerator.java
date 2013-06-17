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
    public static List<ProfileBin> createBins(List<? extends Container> profileContainers, double ppm, double sec,
            double missing) {

        ProfileMap timeBins = group(profileContainers, ppm, sec);

        List<ProfileBin> profileBins = new ArrayList<>();
        for (List<ProfileBin> bins : timeBins.values()) {
            for (ProfileBin timeBin : bins) {
                if (profileContainers.size() - timeBin.getnProfiles() <= missing * profileContainers.size() / 100d)
                    profileBins.add(timeBin);
            }
        }

        return profileBins;
    }

    public static HashMultimap<Integer, Integer> createContainerToProfileMap(
            List<? extends Container> profileContainers, double ppm, double sec, double missing) {

        HashMultimap<Integer, Integer> cToPIdMap = HashMultimap.create();

        ProfileMap timeBins = group(profileContainers, ppm, sec);
        for (List<ProfileBin> bins : timeBins.values()) {
            for (ProfileBin timeBin : bins) {
                if (profileContainers.size() - timeBin.getnProfiles() <= missing * profileContainers.size() / 100d) {
                    for (Map.Entry<Integer, Integer> entry : timeBin.getContainerIndexToProfileId().entrySet()) {
                        cToPIdMap.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }

        return cToPIdMap;
    }

    private static ProfileMap group(List<? extends Container> profileContainers, double ppm, double sec) {

        int index = -1;
        ProfileMap timeBins = new ProfileMap();
        for (Container container : profileContainers) {
            List<Profile> profiles = Lists.newArrayList(container.profileIterator());
            Collections.sort(profiles, new ProfileMassComparator());
            index++;
            for (Profile profile : profiles) {
                double rt = profile.getRetentionTime();
                XYTrace mzTrace = new XYTrace(profile.getMzIntDp());
                Trace closestMzTrace = DataUtils.getClosestKey(mzTrace, timeBins);

                ProfileBin timeBin = new ProfileBin(index, profile, profileContainers.size());
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
                            cTimeBin.add(index, profile);
                            timeBins.add(closestMzTrace, cTimeBin, cIndex);
                        } else timeBins.put(closestMzTrace, timeBin);

                        continue;
                    }
                }
                timeBins.put(mzTrace, timeBin);
            }
        }

        return timeBins;
    }
}