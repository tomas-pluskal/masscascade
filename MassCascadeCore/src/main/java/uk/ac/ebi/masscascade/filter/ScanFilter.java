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

package uk.ac.ebi.masscascade.filter;

import uk.ac.ebi.masscascade.core.raw.RawContainer;
import uk.ac.ebi.masscascade.core.raw.RawLevel;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.ACallableTask;
import uk.ac.ebi.masscascade.interfaces.Container;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.interfaces.Scan;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;
import uk.ac.ebi.masscascade.utilities.ScanUtils;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

/**
 * Class to filter a collection of scans by the given mass and time range.
 * <ul>
 * <li>Parameter <code> MZ RANGE </code>- The mass range used for filtering in amu.</li>
 * <li>Parameter <code> TIME RANGE </code>- The retention time range used for filtering in seconds.</li>
 * <li>Parameter <code> RAW FILE </code>- The input raw container.</li>
 * </ul>
 */
public class ScanFilter extends ACallableTask {

    private Range timeRange;
    private Range massRange;

    private RawContainer rawContainer;

    /**
     * Constructor for a scan filter task.
     *
     * @param params the parameter map
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          description of the exception
     */
    public ScanFilter(ParameterMap params) throws MassCascadeException {

        super(ScanFilter.class);
        setParameters(params);
    }

    /**
     * Sets the parameters for the scan filter task.
     *
     * @param params the new parameter values
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          description of the Exception
     */
    public void setParameters(ParameterMap params) throws MassCascadeException {

        massRange = params.get(Parameter.MZ_RANGE, ExtendableRange.class);
        timeRange = params.get(Parameter.TIME_RANGE, ExtendableRange.class);
        rawContainer = params.get(Parameter.RAW_CONTAINER, RawContainer.class);
    }

    /**
     * Executes the task.
     *
     * @return the filtered scan collection
     */
    public Container call() {

        String id = rawContainer.getId() + IDENTIFIER;
        RawContainer outRawContainer =
                new RawContainer(id, rawContainer.getRawInfo(), rawContainer.getWorkingDirectory());

        for (RawLevel level : rawContainer.getRawLevels()) {

            if (level.getMsn() == Constants.MSN.MS1) {

                for (Scan scan : rawContainer) {
                    XYList xyList = new XYList();
                    if (timeRange.contains(scan.getRetentionTime())) {

                        for (XYPoint xyPoint : scan.getData())
                            if (massRange.contains(xyPoint.x)) xyList.add(xyPoint);

                        Scan processedScan = ScanUtils.getModifiedScan(scan, xyList);
                        outRawContainer.addScan(processedScan);

                        processedScan = null;
                    }
                }
            } else {
                for (Scan scan : rawContainer.iterator(level.getMsn()))
                    outRawContainer.addScan(scan);
            }
        }

        outRawContainer.finaliseFile(rawContainer.getRawInfo().getDate());
        return outRawContainer;
    }
}
