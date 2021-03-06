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

package uk.ac.ebi.masscascade.gapfilling;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import uk.ac.ebi.masscascade.core.chromatogram.MassChromatogram;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Feature;
import uk.ac.ebi.masscascade.interfaces.container.FeatureContainer;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import java.util.Collections;

/**
 * Class implementing gap filling for extracted ion chromatograms using natural cubic splines.
 * <p/>
 * Every mass trace is analysed for gaps. A gap is defined as the space between to unconnected peaks,
 * which is larger than the smallest scan interval found and smaller than that interval times the time factor. All
 * found gaps are closed by calculating intensity values for the times halfway through the gaps,
 * using cubic splines with four anchor points each.
 * <ul>
 * <li>Parameter <code> PROFILE FILE </code>- The input feature container.</li>
 * </ul>
 */
@Deprecated
public class SplineFilling extends CallableTask {

    // minimum number of scans (data points in XIC) for valid min. scan time estimation
    private static final double MIN_ESTIMATE = 5;

    private FeatureContainer featureContainer;
    private FeatureContainer profileRun;

    private SplineInterpolator interpolator;

    /**
     * Constructor for the gap filling task implementation.
     *
     * @param featureContainer the compiled feature data
     */
    public SplineFilling(double timeFactor, FeatureContainer featureContainer) {

        super(SplineFilling.class);

        this.featureContainer = featureContainer;

        interpolator = new SplineInterpolator();
    }

    /**
     * Constructs a gap filling task.
     *
     * @param params the parameter map holding all required task parameters
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the task fails
     */
    public SplineFilling(ParameterMap params) throws MassCascadeException {

        super(SplineFilling.class);

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

        featureContainer = params.get(Parameter.FEATURE_CONTAINER, FeatureContainer.class);
        interpolator = new SplineInterpolator();
    }

    /**
     * Executes the task. The <code> Callable </code> returns a {@link uk.ac.ebi.masscascade.interfaces.container
     * .ScanContainer} with the processed data.
     *
     * @return the scan container with the processed data
     */
    @Override
    public FeatureContainer call() {

        String id = featureContainer.getId() + IDENTIFIER;
        profileRun =
                featureContainer.getBuilder().newInstance(FeatureContainer.class, id,
                        featureContainer.getIonMode(), featureContainer.getWorkingDirectory());

        for (Feature feature : featureContainer) gapFillPeak(feature);

        profileRun.finaliseFile();
        return profileRun;
    }

    /**
     * Screens the mass trace for gaps and fills them if required.
     *
     * @param feature the mass trace
     */
    private void gapFillPeak(Feature feature) {

        // interpolated point cannot exceed max point in XIC, i.e., no need to reset the feature details
        MassChromatogram xic = (MassChromatogram) feature.getTrace();

        XYList xicData = xic.getData();
        Collections.sort(xicData);
        int xicDataSize = xicData.size();

        XYList mzData = feature.getMzData();

        Feature filledFeature = feature.copy();

        for (int i = 1; i < xicDataSize - 1; i++) {

            if (xicData.get(i - 1).y > 0 && xicData.get(i).y == 0 && xicData.get(i + 1).y > 0) {

                XYList splineData = getDataValues(xicData, i);
                filledFeature.addFeaturePoint(mzData.get(i).x, getInterpolatedValue(xicData.get(i).x, splineData));
                i++;
            } else filledFeature.addFeaturePoint(mzData.get(i).x, xicData.get(i));
        }

        // add the remaining last data point of the XIC to the new data set
        filledFeature.addFeaturePoint(mzData.get(xicDataSize - 1).x, xicData.get(xicDataSize - 1));
        filledFeature.closeFeature();

        profileRun.addFeature(filledFeature);
    }

    /**
     * Gets the four spline anchors for the interpolation.
     *
     * @param xyList    the XIC data
     * @param dataIndex the current data index
     * @return the four point data for interpolation
     */
    private XYList getDataValues(XYList xyList, int dataIndex) {

        XYList splineData = new XYList();
        // check if data point is a feature boundary, i.e., intensity = 0
        if (dataIndex - 2 > 0) splineData.add(xyList.get(dataIndex - 2));
        splineData.add(xyList.get(dataIndex - 1));
        splineData.add(xyList.get(dataIndex + 1));
        // check if data point is a feature boundary, i.e., intensity = 0
        if (dataIndex + 2 < xyList.size() && xyList.get(dataIndex + 2).y > 0) splineData.add(xyList.get(dataIndex + 2));

        return splineData;
    }

    /**
     * Gets the interpolated value for the given retention time based on the function derived from the data.
     *
     * @param gapRt      the retention time value of the gap
     * @param splineData the data used for deriving the spline
     * @return the interpolated value (retention time, intensity)
     */
    private XYPoint getInterpolatedValue(double gapRt, XYList splineData) {

        PolynomialSplineFunction function = interpolator.interpolate(splineData.getXs(), splineData.getYs());

        return new XYPoint(gapRt, function.value(gapRt));
    }
}
