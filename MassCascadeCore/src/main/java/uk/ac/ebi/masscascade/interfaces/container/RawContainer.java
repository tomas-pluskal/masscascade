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

package uk.ac.ebi.masscascade.interfaces.container;

import uk.ac.ebi.masscascade.core.raw.RawInfo;
import uk.ac.ebi.masscascade.core.raw.RawLevel;
import uk.ac.ebi.masscascade.interfaces.Chromatogram;
import uk.ac.ebi.masscascade.interfaces.Scan;
import uk.ac.ebi.masscascade.parameters.Constants;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public interface RawContainer extends Container, Iterable<Scan> {

    void addScan(Scan scan);

    void addScanList(List<Scan> scans);

    void finaliseFile(String date);

    List<RawLevel> getRawLevels();

    RawInfo getRawInfo();

    Chromatogram getBasePeakChromatogram();

    Chromatogram getTicChromatogram(Constants.MSN msn);

    Scan getScan(int i);

    Map<Integer, HashMap<Integer, Double>> getMSnParentDaughterMap();

    @Override
    Iterator<Scan> iterator();

    Iterable<Scan> iterator(Constants.MSN msn);
}
