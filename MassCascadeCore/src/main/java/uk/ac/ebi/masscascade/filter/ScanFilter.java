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

package uk.ac.ebi.masscascade.filter;

import uk.ac.ebi.masscascade.core.scan.ScanLevel;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.interfaces.Scan;
import uk.ac.ebi.masscascade.interfaces.container.Container;
import uk.ac.ebi.masscascade.interfaces.container.ScanContainer;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.ScanUtils;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

/**
 * Class to filter a collection of scans by the given mass and time range.
 * <ul>
 * <li>Parameter <code> MZ_RANGE </code>- The mass range used for filtering in amu.</li>
 * <li>Parameter <code> TIME_RANGE </code>- The retention time range used for filtering in seconds.</li>
 * <li>Parameter <code> SCAN_FILE </code>- The input scan container.</li>
 * </ul>
 */
public class ScanFilter extends CallableTask {

    private Range timeRange;
    private Range massRange;

    private ScanContainer scanContainer;

    /**
     * Constructs a scan filter task.
     *
     * @param params the parameter map holding all required task parameters
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the task fails
     */
    public ScanFilter(ParameterMap params) throws MassCascadeException {

        super(ScanFilter.class);
        setParameters(params);
    }

    /**
     * Sets the task class variables using the parameter map.
     *
     * @param params the parameter map containing the <code> Parameter </code> to <code> Object </code> relations.
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the parameter map does not contain all variables required by this class
     */
    @Override
    public void setParameters(ParameterMap params) throws MassCascadeException {

        massRange = params.get(Parameter.MZ_RANGE, ExtendableRange.class);
        timeRange = params.get(Parameter.TIME_RANGE, ExtendableRange.class);
        scanContainer = params.get(Parameter.SCAN_CONTAINER, ScanContainer.class);
    }

    /**
     * Executes the task. The <code> Callable </code> returns a {@link uk.ac.ebi.masscascade.interfaces.container
     * .ScanContainer} with the processed data.
     *
     * @return the scan container with the processed data
     */
    @Override
    public Container call() {

        String id = scanContainer.getId() + IDENTIFIER;
        ScanContainer outScanContainer = scanContainer.getBuilder().newInstance(ScanContainer.class, id, scanContainer);

        for (ScanLevel level : scanContainer.getScanLevels()) {

            if (level.getMsn() == Constants.MSN.MS1) {

                for (Scan scan : scanContainer) {
                    XYList xyList = new XYList();
                    if (timeRange.contains(scan.getRetentionTime())) {

                        for (XYPoint xyPoint : scan.getData())
                            if (massRange.contains(xyPoint.x)) xyList.add(xyPoint);

                        Scan processedScan = ScanUtils.getModifiedScan(scan, xyList);
                        outScanContainer.addScan(processedScan);

                        processedScan = null;
                    }
                }
            } else {
                for (Scan scan : scanContainer.iterator(level.getMsn())) outScanContainer.addScan(scan);
            }
        }

        outScanContainer.finaliseFile(scanContainer.getScanInfo().getDate());
        return outScanContainer;
    }
}
