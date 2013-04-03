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

package uk.ac.ebi.masscascade.tracebuilder;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.math3.util.FastMath;
import uk.ac.ebi.masscascade.core.raw.RawLevel;
import uk.ac.ebi.masscascade.interfaces.Scan;
import uk.ac.ebi.masscascade.interfaces.container.RawContainer;
import uk.ac.ebi.masscascade.parameters.Constants;

import java.util.List;
import java.util.Map;

public class ProfileMsnHelper {

    // msn : parentId : childId : mz , where msn(0) = MS2
    private List<Map<Integer, Map<Integer, Double>>> msnMap;

    private static final double EPSILON = 0.001;

    public ProfileMsnHelper(RawContainer container) {

        msnMap = Lists.newArrayList();
        init(container);
    }

    private void init(RawContainer container) {

        int msn = 0;
        if (container.size() < 2) return;

        Map<Integer, Map<Integer, Double>> singleMsnMap = Maps.newHashMap();
        msnMap.add(singleMsnMap);

        for (RawLevel level : container.getRawLevels()) {
            if (level.getMsn() == Constants.MSN.MS1) continue;

            Map<Integer, Map<Integer, Double>> currentMap = msnMap.get(msn);

            for (Scan scan : container.iterator(level.getMsn())) {

                if (scan.getParentScan() == -1) continue;

                if (currentMap.containsKey(scan.getParentScan()))
                    currentMap.get(scan.getParentScan()).put(scan.getIndex(), scan.getParentMz());
                else {
                    Map<Integer, Double> dIndexdMass = Maps.newHashMap();
                    dIndexdMass.put(scan.getIndex(), scan.getParentMz());
                    currentMap.put(scan.getParentScan(), dIndexdMass);
                }
            }

            msn++;
        }
    }

    public HashMultimap<Integer, Integer> getChildIds(int parentId, double mz) {

        int msn = 0;
        HashMultimap<Integer, Integer> msnToChildIds = HashMultimap.create();

        getChildren(parentId, mz, msnToChildIds, msn);
        return msnToChildIds;
    }

    private void getChildren(int parentId, double mz, HashMultimap<Integer, Integer> msnToChildIds, int msn) {

        if (msn == msnMap.size()) return;

        if (msnMap.get(msn).containsKey(parentId)) {
            for (Map.Entry<Integer, Double> entry : msnMap.get(msn).get(parentId).entrySet()) {

                if (FastMath.abs(entry.getValue() - mz) <= EPSILON) {
                    msnToChildIds.put(msn, entry.getKey());
                    getChildren(entry.getKey(), entry.getValue(), msnToChildIds, msn + 1);
                }
            }
        }
    }
}
