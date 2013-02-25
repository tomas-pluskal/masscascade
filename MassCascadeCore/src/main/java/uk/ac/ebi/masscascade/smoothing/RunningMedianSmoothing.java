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

package uk.ac.ebi.masscascade.smoothing;

import uk.ac.ebi.masscascade.core.raw.RawLevel;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Scan;
import uk.ac.ebi.masscascade.interfaces.container.RawContainer;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.ScanUtils;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Class implementing a running median smoothing method for the m/z domain.
 * <ul>
 * <li>Parameter <code> DATA WINDOW </code>- The number of data points in the m/z domain.</li>
 * <li>Parameter <code> MS LEVEL </code>- The MSn level.</li>
 * <li>Parameter <code> RAW FILE </code>- The input raw container.</li>
 * </ul>
 */
public class RunningMedianSmoothing extends CallableTask {

    private Queue<Double> window = new LinkedList<Double>();
    private int mzWindow;
    private Constants.MSN msn;
    private RawContainer rawContainer;

    /**
     * Constructs a median smoother task.
     *
     * @param params the parameter map holding all required task parameters
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the task fails
     */
    public RunningMedianSmoothing(ParameterMap params) throws MassCascadeException {

        super(RunningMedianSmoothing.class);
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

        mzWindow = params.get(Parameter.DATA_WINDOW, Integer.class);
        msn = params.get(Parameter.MS_LEVEL, Constants.MSN.class);
        rawContainer = params.get(Parameter.RAW_CONTAINER, RawContainer.class);
    }

    /**
     * Maintains the size of the window.
     *
     * @param num the new number to append
     */
    public void newNum(double num) {

        window.add(num);
        if (window.size() > mzWindow) window.remove();
    }

    /**
     * Gets the median from the values in the active window.
     *
     * @return the median
     */
    public double getMedian() {

        if (window.isEmpty() || window.size() != mzWindow) return 0;
        double[] windowVal = new double[mzWindow];
        Iterator<Double> it = window.iterator();
        int i = 0;
        while (it.hasNext()) {
            windowVal[i] = it.next();
            i++;
        }
        Arrays.sort(windowVal);
        double res = windowVal[windowVal.length / 2];
        if (windowVal.length % 2d != 0) res = (res + windowVal[(windowVal.length / 2) + 1]) / 2d;
        windowVal = null;
        return res;
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
        RawContainer smoothedRawContainer = rawContainer.getBuilder().newInstance(RawContainer.class, id, rawContainer);

        XYList smoothedData;

        for (RawLevel level : rawContainer.getRawLevels()) {

            if (level.getMsn() == Constants.MSN.MS1) {
                for (Scan scan : rawContainer) {
                    smoothedData = new XYList();
                    for (XYPoint xyPoint : scan.getData()) {
                        double y = xyPoint.y;
                        newNum(y);
                        if ((y - getMedian()) < 0) continue;
                        double newY = y - getMedian();
                        smoothedData.add(new XYPoint(xyPoint.x, newY));
                    }
                    smoothedRawContainer.addScan(ScanUtils.getModifiedScan(scan, smoothedData));
                    smoothedData = null;
                }
            } else {
                for (Scan msnScan : rawContainer.iterator(level.getMsn())) {
                    smoothedRawContainer.addScan(msnScan);
                }
            }
        }

        smoothedRawContainer.finaliseFile(rawContainer.getRawInfo().getDate());
        return smoothedRawContainer;
    }
}
