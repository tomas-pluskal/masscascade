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

package uk.ac.ebi.masscascade.binning;

import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.interfaces.Scan;
import uk.ac.ebi.masscascade.utilities.ScanUtils;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import java.util.concurrent.Callable;

/**
 * Class implementing binning for the mass domain.
 */
@Deprecated
public class MzBinning implements Callable<Scan> {

    /**
     * Binning modes
     */
    public static enum BinningType {

        SUM, MAX, MIN, AVG
    }

    private Scan scan;
    private double xWidth;
    private MzBinning.BinningType binningType;

    /**
     * Constructs a mass binning task.
     *
     * @param scan        the scan list for binning
     * @param xWidth      the bin width (seconds)
     * @param binningType the binning mode
     */
    public MzBinning(Scan scan, double xWidth, MzBinning.BinningType binningType) {

        this.scan = scan;
        this.xWidth = xWidth;
        this.binningType = binningType;
    }

    /**
     * Bins the mass values of a given scan.
     *
     * @return the binned scan
     * @throws Exception unexpected behaviour
     */
    @Override
    public Scan call() {

        Range mzRange = scan.getMzRange();
        Double[] noOfEntries = null;

        double[] binXValues = new double[(int) Math.ceil(mzRange.getUpperBounds() / xWidth)];
        Double[] binYValues = new Double[binXValues.length];

        for (int valueIndex = 0; valueIndex < scan.getData().size(); valueIndex++) {

            XYPoint xyPoint = scan.getData().get(valueIndex);

            int binIndex = (int) ((xyPoint.x - mzRange.getLowerBounds()) / xWidth);
            if (binIndex == binYValues.length) binIndex--;

            switch (binningType) {
                case MAX:
                    if (binYValues[binIndex] == null) {
                        binYValues[binIndex] = xyPoint.y;
                    } else {
                        if (binYValues[binIndex] < xyPoint.y) {
                            binYValues[binIndex] = xyPoint.y;
                        }
                    }
                    break;
                case MIN:
                    if (binYValues[binIndex] == null) {
                        binYValues[binIndex] = xyPoint.y;
                    } else {
                        if (binYValues[binIndex] > xyPoint.y) {
                            binYValues[binIndex] = xyPoint.y;
                        }
                    }
                    break;
                case AVG:
                    if (noOfEntries == null) {
                        noOfEntries = new Double[binYValues.length];
                    }
                    if (binYValues[binIndex] == null) {
                        noOfEntries[binIndex] = 1.0;
                        binYValues[binIndex] = xyPoint.y;
                    } else {
                        noOfEntries[binIndex]++;
                        binYValues[binIndex] += xyPoint.y;
                    }
                    break;

                case SUM:
                default:
                    if (binYValues[binIndex] == null) {
                        binYValues[binIndex] = xyPoint.y;
                    } else {
                        binYValues[binIndex] += xyPoint.y;
                    }
                    break;
            }
        }

        // calculate the AVG
        if (binningType.equals(BinningType.AVG)) {
            for (int binIndex = 0; binIndex < binYValues.length; binIndex++) {
                if (binYValues[binIndex] != null) {
                    binYValues[binIndex] /= noOfEntries[binIndex];
                }
            }
        }

        XYList binnedXyList = new XYList();
        for (int binIndex = 0; binIndex < binYValues.length; binIndex++) {
            if (binYValues[binIndex] == null) {
                continue;
            } else {
                double mz = mzRange.getLowerBounds() + (binIndex * xWidth) + (xWidth / 2);
                binnedXyList.add(new XYPoint(mz, binYValues[binIndex]));
            }
        }

        return ScanUtils.getModifiedScan(scan, binnedXyList);
    }
}
