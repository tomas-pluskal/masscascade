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
package uk.ac.ebi.masscascade.normalization;

import uk.ac.ebi.masscascade.core.scan.ScanImpl;
import uk.ac.ebi.masscascade.interfaces.Chromatogram;
import uk.ac.ebi.masscascade.interfaces.NormMethod;
import uk.ac.ebi.masscascade.interfaces.Scan;
import uk.ac.ebi.masscascade.interfaces.container.ScanContainer;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

/**
 * Class for total signal normalization of samples.
 */
public class TotalSignalNorm implements NormMethod {

    private static final int NORM_MAX = 100000;
    private double totalIntensity;

    /**
     * Constructor for a total signal normalization task.
     */
    public TotalSignalNorm(ScanContainer scanContainer) {

        Chromatogram c = scanContainer.getTicChromatogram(Constants.MSN.MS1);
        for (XYPoint dp : c.getData()) {
            totalIntensity += dp.y;
        }
    }

    /**
     * Normalizes a single scan.
     *
     * @param scan the input scan
     * @return the normalized scan.
     */
    public Scan normalize(Scan scan) {

        XYList normList = new XYList();
        for (XYPoint dp : scan.getData()) {
            normList.add(new XYPoint(dp.x, dp.y / totalIntensity * NORM_MAX));
        }

        return new ScanImpl(scan.getIndex(), scan.getMsn(), scan.getIonMode(), normList,
                scan.getRetentionTime(), scan.getParentScan(), scan.getParentCharge(), scan.getParentMz());
    }
}
