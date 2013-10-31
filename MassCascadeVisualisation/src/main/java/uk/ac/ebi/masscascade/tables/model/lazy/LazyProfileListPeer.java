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

package uk.ac.ebi.masscascade.tables.model.lazy;

import uk.ac.ebi.masscascade.core.container.file.feature.FileFeatureContainer;
import uk.ac.ebi.masscascade.interfaces.Feature;
import uk.ac.ebi.masscascade.interfaces.FeatureSet;
import uk.ac.ebi.masscascade.interfaces.container.Container;
import uk.ac.ebi.masscascade.interfaces.container.FeatureContainer;
import uk.ac.ebi.masscascade.interfaces.container.FeatureSetContainer;
import uk.ac.ebi.masscascade.tables.lazytable.util.LazyListService;
import uk.ac.ebi.masscascade.utilities.FeatureUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class LazyProfileListPeer implements LazyListService<Object[]> {

    private static final int ID = 0;
    private static final int RT = 1;
    private static final int WIDTH = 2;
    private static final int MZ = 3;
    private static final int DEV = 4;
    private static final int AREA = 5;
    private static final int INFO = 6;
    private static final int SHAPE = 7;

    private Container profileContainer;
    HashMap<Integer, Integer> profileToSpectraNumbers;
    private List<Integer> ids;

    public LazyProfileListPeer(Container profileContainer) {

        this.profileContainer = profileContainer;
        ids = new ArrayList<Integer>();
        profileToSpectraNumbers = new LinkedHashMap<Integer, Integer>();

        if (profileContainer instanceof FeatureContainer) {
            ids = new LinkedList<Integer>(((FileFeatureContainer) profileContainer).getFeatureNumbers().keySet());
        } else {
            profileToSpectraNumbers = new LinkedHashMap<Integer, Integer>();
            for (FeatureSet featureSet : (FeatureSetContainer) profileContainer) {
                for (Feature feature : featureSet) {
                    profileToSpectraNumbers.put(feature.getId(), featureSet.getIndex());
                    ids.add(feature.getId());
                }
            }
        }
    }

    public Object[][] getData(int startElement, int endElement) {

        Object[][] result = new Object[endElement - startElement][LazyProfileTableModel.TABLEHEADERS.length];

        int i = 0;
        while (startElement < endElement) {

            Feature feature;
            if (profileContainer instanceof FeatureContainer) {
                feature = ((FileFeatureContainer) profileContainer).getFeature(ids.get(startElement));
            } else {
                int startId = ids.get(startElement);
                feature = ((FeatureSetContainer) profileContainer).getFeatureSet(
                        profileToSpectraNumbers.get(startId)).getFeature(startId);
            }

            result[i][ID] = feature.getId();
            result[i][RT] = feature.getRetentionTime();
            result[i][WIDTH] = feature.getRtRange().getSize();
            result[i][MZ] = feature.getMz();
            result[i][DEV] = feature.getDeviation();
            result[i][AREA] = feature.getArea();
            result[i][INFO] = FeatureUtils.getProfileLabel(feature);
            result[i][SHAPE] = feature.getTrace(3);

            i++;
            startElement++;
        }

        return result;
    }

    public int getSize() {
        return ids.size();
    }

    public void add(int position, Object[] element) {
        throw new UnsupportedOperationException();
    }

    public void set(int position, Object[] element) {
        throw new UnsupportedOperationException();
    }

    public void remove(int position) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }
}
