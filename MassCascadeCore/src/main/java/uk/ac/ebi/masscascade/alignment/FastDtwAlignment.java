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

package uk.ac.ebi.masscascade.alignment;

import com.dtw.FastDTW;
import com.dtw.TimeWarpInfo;
import com.timeseries.TimeSeries;
import uk.ac.ebi.masscascade.core.raw.RawContainer;
import uk.ac.ebi.masscascade.core.raw.ScanImpl;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.ACallableTask;
import uk.ac.ebi.masscascade.interfaces.Scan;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;

import java.util.Collections;
import java.util.List;

/**
 * Class implementing sample alignment against a reference using fast dynamic time warping.<br />
 * <i>Toward accurate dynamic time wrapping in linear time and space. Stan Salvador and Philip Chan. Intelligent Data
 * Analysis, 11(5):561-580, 2007</i><br />
 * <i>http://code.google.com/p/fastdtw/</i><br />
 * <ul>
 * <li>Parameter <code> TIME WINDOW </code>- The scan window for the alignment in scans.</li>
 * <li>Parameter <code> REFERENCE FILE </code>- The input raw reference container.</li>
 * <li>Parameter <code> RAW FILE </code>- The input raw container.</li>
 * </ul>
 */
public class FastDtwAlignment extends ACallableTask {

    private RawContainer rawContainer;
    private RawContainer refRawContainer;
    private int scanRadius;

    /**
     * CConstructs an alignment task.
     *
     * @param params the parameter map
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public FastDtwAlignment(ParameterMap params) throws MassCascadeException {

        super(FastDtwAlignment.class);

        setParameters(params);
    }

    /**
     * Sets the parameters attribute for the alignment task.
     *
     * @param params the new parameter values
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public void setParameters(ParameterMap params) throws MassCascadeException {

        scanRadius = params.get(Parameter.TIME_WINDOW, Integer.class);
        refRawContainer = params.get(Parameter.REFERENCE_FILE, RawContainer.class);
        rawContainer = params.get(Parameter.RAW_CONTAINER, RawContainer.class);
    }

    /**
     * Aligns two samples along the time domain using fast dynamic time warping.
     *
     * @return the aligned sample
     * @throws Exception unexpected behaviour
     */
    public RawContainer call() {

        double[] targetTic = rawContainer.getBasePeakChromatogram().getData().getYs();
        double[] refTic = refRawContainer.getBasePeakChromatogram().getData().getYs();

        final TimeSeries tsI = new TimeSeries(targetTic, false, false, ',');
        final TimeSeries tsJ = new TimeSeries(refTic, false, false, ',');
        final TimeWarpInfo info = FastDTW.getWarpInfoBetween(tsI, tsJ, scanRadius);

        String id = rawContainer.getId() + IDENTIFIER;
        RawContainer alignedRawContainer =
                new RawContainer(id, rawContainer.getRawInfo(), rawContainer.getWorkingDirectory());

        Scan tarScan = null;
        Scan refScan;
        Scan corScan;

        XYList mergedData = new XYList();

        for (int i = 0; i < tsJ.size(); i++) {

            refScan = refRawContainer.getScanByIndex(i);
            List<Integer> tarPosList = info.getPath().getMatchingIndexesForJ(i);

            for (int tarPos : tarPosList) {

                tarScan = rawContainer.getScanByIndex(tarPos);
                mergedData.addAll(tarScan.getData());
            }

            if (mergedData.isEmpty()) continue;
            Collections.sort(mergedData);

            corScan = buildAlignedScan(tarScan, refScan, mergedData);
            alignedRawContainer.addScan(corScan);

            mergedData.clear();
        }

        tarScan = null;
        refScan = null;
        corScan = null;

        alignedRawContainer.finaliseFile(rawContainer.getRawInfo().getDate());
        return alignedRawContainer;
    }

    /**
     * Builds a new scan based on the target scan with the corrected retention time information.
     *
     * @param tarScan the target scan
     * @param refScan the reference scan
     * @return the aligned scan
     */
    private Scan buildAlignedScan(Scan tarScan, Scan refScan, XYList mergedData) {

        Scan corScan = new ScanImpl(tarScan.getIndex(), tarScan.getMsn(), tarScan.getIonMode(), mergedData,
                refScan.getRetentionTime(), tarScan.getParentScan(), tarScan.getParentCharge(), tarScan.getParentMz());

        return corScan;
    }
}
