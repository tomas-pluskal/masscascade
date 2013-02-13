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

import uk.ac.ebi.masscascade.core.raw.RawContainer;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.ACallableTask;
import uk.ac.ebi.masscascade.interfaces.Scan;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;

/**
 * Class implementing binning for the mass domain.
 * <ul>
 * <li>Parameter <code> MZ WINDOW AMU </code>- The mass window in amu.</li>
 * <li>Parameter <code> AGGREGATION </code>- The preferred binning type (average, sum, min, max).</li>
 * <li>Parameter <code> RAW FILE </code>- The input raw container.</li>
 * </ul>
 */
public class MzFileBinning extends ACallableTask {

    private RawContainer rawContainer;

    private double xWidth;
    private MzBinning.BinningType binType;

    /**
     * Constructs a custom mz binner task.
     *
     * @param params the parameter map
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public MzFileBinning(ParameterMap params) throws MassCascadeException {

        super(MzFileBinning.class);
        setParameters(params);
    }

    /**
     * Sets the parameters of the mz binner task.
     *
     * @param params the new parameters value
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public void setParameters(ParameterMap params) throws MassCascadeException {

        this.xWidth = params.get(Parameter.MZ_WINDOW_AMU, Double.class);
        this.binType = params.get(Parameter.AGGREGATION, MzBinning.BinningType.class);
        this.rawContainer = params.get(Parameter.RAW_CONTAINER, RawContainer.class);
    }

    /**
     * Executes the binning task.
     *
     * @return the processed sample file
     */
    public RawContainer call() {

        String id = rawContainer.getId() + IDENTIFIER;
        RawContainer outRawContainer =
                new RawContainer(id, rawContainer.getRawInfo(), rawContainer.getWorkingDirectory());

        Scan scan;
        Scan binnedScan;
        MzBinning binImpl;

        for (int level = Constants.MSN.MS1.getLvl(); level <= rawContainer.getRawLevels().size(); level++) {

            for (int number : rawContainer.getScanNumbers(Constants.MSN.get(level)).keySet()) {
                scan = rawContainer.getScan(number);
                binImpl = new MzBinning(scan, xWidth, binType);
                binnedScan = binImpl.call();
                outRawContainer.addScan(binnedScan);

                scan = null;
                binImpl = null;
                binnedScan = null;
            }
        }

        outRawContainer.finaliseFile(rawContainer.getRawInfo().getDate());
        return outRawContainer;
    }
}
