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

import uk.ac.ebi.masscascade.alignment.profilebins.ProfileBin;
import uk.ac.ebi.masscascade.interfaces.Trace;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;
import uk.ac.ebi.masscascade.utilities.xyz.XYTrace;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Stores m/z trace to time bins associations. The traces are ordered by their m/z anchor.
 */
public class ProfileMap extends TreeMap<Trace, List<ProfileBin>> {

    private static final long serialVersionUID = -8922110551070275447L;

    /**
     * Updates an existing time bin with a new time bin.
     *
     * @param mzTrace the m/z trace key
     * @param timeBin the new time bin
     * @param index   the index of the old time bin
     */
    public void add(Trace mzTrace, ProfileBin timeBin, int index) {

        List<ProfileBin> timeBins = get(mzTrace);
        timeBins.remove(index);
        timeBins.add(timeBin);
    }

    /**
     * Adds a m/z trace to time bin association to the map.
     *
     * @param key   the m/z trace
     * @param value the time bin
     */
    public void put(Trace key, ProfileBin value) {

        if (containsKey(key)) {
            ((XYTrace) key).add(new XYPoint(value.getMz(), 0));
            List<ProfileBin> bins = get(key);
            bins.add(value);
            super.put(key, bins);
        } else {
            List<ProfileBin> bins = new ArrayList<>();
            bins.add(value);
            super.put(key, bins);
        }
    }
}
