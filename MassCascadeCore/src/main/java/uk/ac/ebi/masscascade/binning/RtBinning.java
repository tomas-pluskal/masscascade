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

package uk.ac.ebi.masscascade.binning;

import uk.ac.ebi.masscascade.core.file.raw.FileRawContainer;
import uk.ac.ebi.masscascade.core.raw.ScanImpl;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.container.RawContainer;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.interfaces.Scan;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * Class implementing binning for the time domain.
 * <ul>
 * <li>Parameter <code> SCAN WINDOW </code>- The time window.</li>
 * <li>Parameter <code> RAW FILE </code>- The input raw container.</li>
 * </ul>
 */
public class RtBinning extends CallableTask {

    private FileRawContainer rawContainer;

    private double timeWindow;
    private Scan tmpScan;
    private int scanIndex;

    /**
     * Constructs a custom rt binner task.
     *
     * @param params the parameter map
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public RtBinning(ParameterMap params) throws MassCascadeException {

        super(RtBinning.class);
        setParameters(params);
    }

    /**
     * Sets the parameters attribute of the rt binner task.
     *
     * @param params the new parameter values
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public void setParameters(ParameterMap params) throws MassCascadeException {

        timeWindow = params.get(Parameter.SCAN_WINDOW, Double.class);
        rawContainer = params.get(Parameter.RAW_CONTAINER, FileRawContainer.class);

        scanIndex = 1;
    }

    /**
     * Bins a scan list along the retention time domain.
     *
     * @return the binned scan list
     */
    public RawContainer call() {

        String id = rawContainer.getId() + IDENTIFIER;
        RawContainer outRawContainer =
                new FileRawContainer(id, rawContainer.getRawInfo(), rawContainer.getWorkingDirectory());

        for (int i = Constants.MSN.MS1.getLvl(); i <= rawContainer.getRawLevels().size(); i++) {

            Map<Integer, Long> scanNumbers = rawContainer.getScanNumbers(Constants.MSN.get(i));
            Iterator<Integer> scanIndexIt = scanNumbers.keySet().iterator();

            Scan pScan = rawContainer.getScan(scanIndexIt.next());
            Scan cScan;
            double pRetentionTime = pScan.getRetentionTime();

            while (scanIndexIt.hasNext()) {

                cScan = rawContainer.getScan(scanIndexIt.next());
                if ((cScan.getRetentionTime() - pRetentionTime) < timeWindow) {
                    pScan = combineScans(cScan, pScan);
                    if (!scanIndexIt.hasNext()) {
                        outRawContainer.addScan(getScanCopy(pScan));
                    }
                } else {
                    outRawContainer.addScan(getScanCopy(pScan));
                    pRetentionTime = cScan.getRetentionTime();
                    pScan = cScan;
                    if (!scanIndexIt.hasNext()) {
                        outRawContainer.addScan(getScanCopy(cScan));
                    }
                }

                cScan = null;
                tmpScan = null;
            }
        }
        outRawContainer.finaliseFile(rawContainer.getRawInfo().getDate());
        return outRawContainer;
    }

    /**
     * Returns a deep copy of the scan.
     *
     * @param scan the scan
     * @return the scan clone
     */
    private Scan getScanCopy(Scan scan) {

        tmpScan = new ScanImpl(scanIndex, scan.getMsn(), scan.getIonMode(), scan.getData(), scan.getMzRange(),
                scan.getBasePeak(), scan.getRetentionTime(), scan.getTotalIonCurrent(), scan.getParentScan(),
                scan.getParentCharge(), scan.getParentMz());
        scanIndex++;
        return tmpScan;
    }

    /**
     * Helper method to combine two scans.
     *
     * @param cScan the scan to be merged
     * @param pScan the scan to be merged to
     * @return the resulting merged scan
     */
    private Scan combineScans(Scan cScan, Scan pScan) {

        XYList basePeak = pScan.getBasePeak();
        if (pScan.getBasePeak().get(0).y < cScan.getBasePeak().get(0).y) {
            basePeak = cScan.getBasePeak();
        }

        XYList tData = new XYList();
        tData.addAll(cScan.getData());
        tData.addAll(pScan.getData());
        Collections.sort(tData);

        Range mzRange = new ExtendableRange(tData.get(0).x, tData.get(tData.size() - 1).x);
        double totalIonCurrent = cScan.getTotalIonCurrent() + pScan.getTotalIonCurrent();

        Scan tmpScan = new ScanImpl(scanIndex, pScan.getMsn(), pScan.getIonMode(), tData, mzRange, basePeak,
                pScan.getRetentionTime(), totalIonCurrent, pScan.getParentScan(), pScan.getParentCharge(),
                pScan.getParentMz());

        scanIndex++;
        return tmpScan;
    }
}
