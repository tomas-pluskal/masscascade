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

import uk.ac.ebi.masscascade.core.scan.ScanLevel;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.NormMethod;
import uk.ac.ebi.masscascade.interfaces.Scan;
import uk.ac.ebi.masscascade.interfaces.container.ScanContainer;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;

import java.io.IOException;

/**
 * Class for signal normalization.
 * <p/>
 * Normalizes scan containers using the selected normalization method.
 * <ul>
 * <li>Parameter <code> NORM_METHOD </code>- The normalization method.</li>
 * <li>Parameter <code> SCAN_CONTAINER </code>- The input scan container.</li>
 * </ul>
 */
public class Normalizer extends CallableTask {

    private Constants.NORM_METHOD normMethod;
    private ScanContainer scanContainer;

    /**
     * Constructs a normalization task.
     *
     * @param params the parameter map holding all required task parameters
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the task fails
     */
    public Normalizer(ParameterMap params) {

        super(Normalizer.class);
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

        normMethod = params.get(Parameter.NORM_METHOD, Constants.NORM_METHOD.class);
        scanContainer = params.get(Parameter.SCAN_CONTAINER, ScanContainer.class);
    }

    /**
     * Executes the task. The <code> Callable </code> returns a {@link uk.ac.ebi.masscascade.interfaces.container
     * .ScanContainer} with the processed data.
     *
     * @return the scan container with the processed data
     */
    @Override
    public ScanContainer call() {

        String id = scanContainer.getId() + IDENTIFIER;
        ScanContainer outScanContainer =
                scanContainer.getBuilder().newInstance(ScanContainer.class, id, scanContainer.getWorkingDirectory());

        NormMethod method = null;
        if (normMethod == Constants.NORM_METHOD.TOTAL_SIGNAL) {
            method = new TotalSignalNorm(scanContainer);
        } else {
            method = new DummyNorm();
        }

        for (ScanLevel level : scanContainer.getScanLevels()) {
            if (level.getMsn() == Constants.MSN.MS1) {
                for (Scan scan : scanContainer) {
                    Scan normScan = method.normalize(scan);
                    outScanContainer.addScan(normScan);
                }
            } else {
                for (Scan scan : scanContainer.iterator(level.getMsn())) {
                    outScanContainer.addScan(scan);
                }
            }
        }

        outScanContainer.finaliseFile(scanContainer.getScanInfo().getDate());
        return outScanContainer;
    }
}
