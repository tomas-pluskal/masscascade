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

import org.apache.commons.math3.util.FastMath;
import uk.ac.ebi.masscascade.interfaces.Scan;
import uk.ac.ebi.masscascade.interfaces.container.RawContainer;
import uk.ac.ebi.masscascade.utilities.DataUtils;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Class to reverse fill gaps in cross-sample profile alignments. Gaps are looked up in the complementary raw
 * container,
 * taking time shifts into account if provided.
 */
public class ProfileBinFiller {

    private double deltaRt;
    private double deltaPpm;
    private double defaultIntensity;

    private TreeMap<Double, Double> timeToShift;

    /**
     * Constructs a new gap filler.
     *
     * @param deltaRt          the time tolerance value in seconds
     * @param deltaPpm         the m/z tolerance value in ppm
     * @param defaultIntensity the default intensity to be set when no signal can be found in the raw container
     */
    public ProfileBinFiller(double deltaRt, double deltaPpm, double defaultIntensity) {

        this.deltaRt = deltaRt;
        this.deltaPpm = deltaPpm;
        this.defaultIntensity = defaultIntensity;

        timeToShift = new TreeMap<>();
    }

    /**
     * Sets optional shifts to compensate for time shifts on the profile level, e.g., due to previous profile
     * alignment.
     *
     * @param timeToShift the time shifts
     */
    public void setShifts(TreeMap<Double, Double> timeToShift) {
        this.timeToShift = timeToShift;
    }

    /**
     * Reverse fills a gap for a cross-sample profile bin at a given index.
     *
     * @param index      the index to be filled
     * @param container  the corresponding raw container
     * @param bins       the total list of profile bins
     * @param binIndices the list of bin indices pointing to the profile bins to be filled
     */
    public void reverseFill(int index, RawContainer container, List<ProfileBin> bins, Collection<Integer> binIndices) {

        int minI = -1;
        Set<Integer> checked = new HashSet<>();
        Map<Integer, XYPoint> indexToIntensity = new HashMap<>();
        for (Scan scan : container) {
            for (int i = minI; i < bins.size(); i++) {

                if (!binIndices.contains(i)) continue;

                ProfileBin bin = bins.get(i);

                double binRt = bin.getRt();
                if (!timeToShift.isEmpty()) binRt = getCorrectTime(binRt);

                if (scan.getRetentionTime() < binRt - deltaRt) {
                    break;
                } else if (scan.getRetentionTime() < binRt + deltaRt) {
                    XYPoint nearestDp = scan.getNearestPoint(bin.getMz(), deltaPpm);
                    if (nearestDp == null) continue;
                    else if (!indexToIntensity.containsKey(i)) indexToIntensity.put(i, new XYPoint(binRt, nearestDp.y));
                    else if (FastMath.abs(scan.getRetentionTime() - binRt) < indexToIntensity.get(i).x)
                        indexToIntensity.put(i, new XYPoint(binRt, nearestDp.y));
                } else if (minI == -1) {
                    minI = i;
                } else if (checked.contains(minI)) {
                    minI = i;
                } else {
                    if (indexToIntensity.containsKey(minI)) {
                        bins.get(minI).setPresent(index, indexToIntensity.get(minI).y);
                        indexToIntensity.remove(minI);
                    } else {
                        bins.get(minI).setPresent(index, defaultIntensity);
                    }
                    checked.add(minI);
                    minI = i;
                }
            }
        }

//        for (int i = minI; i < bins.size(); i++) {
//
//            if (!binIndices.contains(i)) continue;
//
//            ProfileBin bin = bins.get(i);
//
//            if (indexToIntensity.containsKey(i)) {
//                bin.setPresent(index, indexToIntensity.get(i).y);
//                indexToIntensity.remove(i);
//            } else {
//                bin.setPresent(index, defaultIntensity);
//            }
//        }
    }

    /**
     * Corrects the retention time based on the given time shift information.
     *
     * @param rt the retention time
     * @return the corrected retetntion time
     */
    private double getCorrectTime(double rt) {

        Double closestRt = DataUtils.getClosestKey(rt, timeToShift);
        return closestRt == null ? rt : rt - timeToShift.get(closestRt);
    }
}
