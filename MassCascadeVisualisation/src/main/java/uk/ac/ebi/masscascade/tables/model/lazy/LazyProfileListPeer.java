/*
 * Copyright (c) 2013, Stephan Beisken. All rights reserved.
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
 */

package uk.ac.ebi.masscascade.tables.model.lazy;

import uk.ac.ebi.masscascade.core.container.file.profile.FileProfileContainer;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.container.ProfileContainer;
import uk.ac.ebi.masscascade.tables.lazytable.util.LazyListService;
import uk.ac.ebi.masscascade.utilities.ProfUtils;

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

    private ProfileContainer profileContainer;
    private List<Integer> profileNumbers;

    public LazyProfileListPeer(int rows, FileProfileContainer profileContainer) {

        this.profileContainer = profileContainer;
        this.profileNumbers = new LinkedList<Integer>(profileContainer.getProfileNumbers().keySet());
    }

    public Object[][] getData(int startElement, int endElement) {

        Object[][] result = new Object[endElement - startElement][LazyProfileTableModel.TABLEHEADERS.length];

        int i = 0;
        while (startElement < endElement) {

            Profile profile = profileContainer.getProfile(profileNumbers.get(startElement));

            result[i][ID] = profile.getId();
            result[i][RT] = profile.getRetentionTime();
            result[i][WIDTH] = profile.getRtRange().getSize();
            result[i][MZ] = profile.getMz();
            result[i][DEV] = profile.getDeviation();
            result[i][AREA] = profile.getArea();
            result[i][INFO] = ProfUtils.getProfileLabel(profile);
            result[i][SHAPE] = profile.getTrace();

            i++;
            startElement++;
        }

        return result;
    }

    public int getSize() {

        return profileNumbers.size();
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
