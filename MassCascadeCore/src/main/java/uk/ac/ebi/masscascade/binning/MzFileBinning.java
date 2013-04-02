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

import uk.ac.ebi.masscascade.core.raw.RawLevel;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Scan;
import uk.ac.ebi.masscascade.interfaces.container.RawContainer;
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
public class MzFileBinning extends CallableTask {

    private RawContainer rawContainer;

    private double xWidth;
    private MzBinning.BinningType binType;

    /**
     * Constructs a mz binner task.
     *
     * @param params the parameter map holding all required task parameters
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the task fails
     */
    public MzFileBinning(ParameterMap params) throws MassCascadeException {

        super(MzFileBinning.class);
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

        this.xWidth = params.get(Parameter.MZ_WINDOW_AMU, Double.class);
        this.binType = params.get(Parameter.AGGREGATION, MzBinning.BinningType.class);
        this.rawContainer = params.get(Parameter.RAW_CONTAINER, RawContainer.class);
    }

    /**
     * Executes the task. The <code> Callable </code> returns a {@link uk.ac.ebi.masscascade.interfaces.container
     * .RawContainer} with the processed data.
     *
     * @return the raw container with the processed data
     */
    @Override
    public RawContainer call() {

        String id = rawContainer.getId() + IDENTIFIER;
        RawContainer outRawContainer = rawContainer.getBuilder().newInstance(RawContainer.class, id, rawContainer);

        Scan binnedScan;
        MzBinning binImpl;

        for (RawLevel level : rawContainer.getRawLevels()) {

            if (level.getMsn() == Constants.MSN.MS1) {
                for (Scan scan : rawContainer) {
                    binImpl = new MzBinning(scan, xWidth, binType);
                    binnedScan = binImpl.call();
                    outRawContainer.addScan(binnedScan);
                    binImpl = null;
                    binnedScan = null;
                }
            }
        }

        outRawContainer.finaliseFile(rawContainer.getRawInfo().getDate());
        return outRawContainer;
    }
}
