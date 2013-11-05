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
package uk.ac.ebi.masscascade.background;

import org.apache.commons.math3.util.FastMath;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Feature;
import uk.ac.ebi.masscascade.interfaces.container.FeatureContainer;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.xyz.XYZList;
import uk.ac.ebi.masscascade.utilities.xyz.XYZPoint;

import java.util.Arrays;

/**
 * Baseline subtraction according to the vHGW Top-Hat algorithm (morphological filter).
 * <p/>
 * Van Herk, M., 1992. A fast algorithm for local minimum and maximum filters on rectangular and octagonal kernels.
 * Pattern Recognition Letters, 13, pp.517–521.
 * <ul>
 * <li>Parameter <code> SCAN_WINDOW </code>- The window width.</li>
 * <li>Parameter <code> FEATURE_CONTAINER </code>- The input feature container.</li>
 * </ul>
 */
public class BaselineSubtraction extends CallableTask {

    private int halfWindowWidth;
    private FeatureContainer featureContainer;

    private static final int BASE_INTENSITY = 10;

    /**
     * Constructs a baseline subtraction task.
     *
     * @param params the parameter map holding all required task parameters
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the task fails
     */
    public BaselineSubtraction(ParameterMap params) throws MassCascadeException {

        super(BaselineSubtraction.class);
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

        halfWindowWidth = FastMath.round(params.get(Parameter.SCAN_WINDOW, Integer.class) / 2);
        featureContainer = params.get(Parameter.FEATURE_CONTAINER, FeatureContainer.class);
    }

    /**
     * Executes the task. The <code> Callable </code> returns a {@link uk.ac.ebi.masscascade.interfaces.container
     * .FeatureContainer} with the processed data.
     *
     * @return the feature container with the processed data
     */
    @Override
    public FeatureContainer call() {

        String id = featureContainer.getId() + IDENTIFIER;
        FeatureContainer outFeatureContainer = featureContainer.getBuilder().newInstance(FeatureContainer.class, id,
                featureContainer.getIonMode(), featureContainer.getWorkingDirectory());

        for (Feature feature : featureContainer) {
            Feature bcFeature = applyTopHat(feature);
            outFeatureContainer.addFeature(bcFeature);
        }

        outFeatureContainer.finaliseFile();
        return outFeatureContainer;
    }

    /**
     * Applies a morphological top hat filter on the feature of type opening: erosion followed by dilation.
     *
     * @param feature the feature to be filtered
     * @return the baseline reduced feature
     */
    private Feature applyTopHat(Feature feature) {

        XYZList xyz = feature.getData();

//        int n = xyz.size() - 2;
        int n = xyz.size();
        int q = halfWindowWidth;
        int k = 2 * q + 1;
        int fn = n + 2 * q + (k - (n % k));

        // check if the half window width is not too small or too large.
        if (!isValidHalfWindowWidth(n)) {
            return feature;
        }

//        double[] yn = Arrays.copyOfRange(xyz.getXZSlice().getYs(), 1, xyz.size() - 1);
        double[] yn = xyz.getXZSlice().getYs();
        double[] es = erosion(yn, q, n, k, fn);
        double[] ys = dilation(es, q, n, k, fn);

        // creates a copy of the feature including its starting data point
        Feature corFeature = feature.copy();
        for (int i = 1; i < ys.length; i++) {
            XYZPoint xyzOri = xyz.get(i);
            XYZPoint xyzPoint = new XYZPoint(xyzOri.x, xyzOri.y, xyzOri.z - ys[i] + BASE_INTENSITY);
            corFeature.addFeaturePoint(xyzPoint);
        }
        corFeature.closeFeature();

        return corFeature;
    }

    private boolean isValidHalfWindowWidth(int n) {
        return halfWindowWidth >= 1 && halfWindowWidth * 2 + 1 <= n;
    }

    /**
     * Erosion of the feature according to vHGW.
     *
     * @param y  intensity vector of the feature
     * @param q  half width of the structure element
     * @param n  total number of data points
     * @param k  twice the half width of the structure element plus one
     * @param fn length of the padded intensity vector for the algorithm
     * @return the eroded intensity vector
     */
    private double[] erosion(double[] y, int q, int n, int k, int fn) {

        // instantiate primitive arrays of size fn
        double[] ys = new double[fn];
        double[] gs = new double[fn];
        double[] hs = new double[fn];
        double[] yo = new double[n];

        // copy the intensity array into padded array
        System.arraycopy(y, 0, ys, q, n);

        // instantiate left extrema
        for (int i = 0; i < q; i++) {
            ys[i] = ys[q];
            hs[i] = ys[q];
        }
        // instantiate right extrema
        int ri = q + n - 1;
        for (int i = q + n; i < fn; i++) {
            ys[i] = ys[ri];
            gs[i] = ys[ri];
        }

        // iterate block wise over every structure element
        for (int i = q, r = i + k - 1; i < n + q; i += k, r += k) {
            // instantiate current element in the working arrays
            gs[i] = ys[i];
            hs[r] = ys[r];
            // move working arrays over the copied intensity array from
            // left to right and right to left and keep track of the minimum
            for (int j = 1, gi = i + 1, hi = r - 1; j < k; j++, gi++, hi--) {
                gs[gi] = (gs[gi - 1] > ys[gi]) ? ys[gi] : gs[gi - 1];
                hs[hi] = (hs[hi + 1] > ys[hi]) ? ys[hi] : hs[hi + 1];
            }
        }

        // merge both working arrays by their minimum values
        for (int i = 0, gi = k - 1, hi = 0; i < n; i++, gi++, hi++) {
            yo[i] = (gs[gi] > hs[hi]) ? hs[hi] : gs[gi];
        }

        return yo;
    }

    /**
     * Dilation of the feature according to vHGW.
     *
     * @param es eroded intensity vector of the feature
     * @param q  half width of the structure element
     * @param n  total number of data points
     * @param k  twice the half width of the structure element plus one
     * @param fn length of the padded intensity vector for the algorithm
     * @return the dilated intensity vector
     */
    private double[] dilation(double[] es, int q, int n, int k, int fn) {

        // instantiate primitive arrays of size fn
        double[] ys = new double[fn];
        double[] gs = new double[fn];
        double[] hs = new double[fn];
        double[] yo = new double[n];

        // copy eroded intensity array into padded array
        System.arraycopy(es, 0, ys, q, n);

        // instantiate left extrema
        for (int i = 0; i < q; i++) {
            ys[i] = ys[q];
            hs[i] = ys[q];
        }
        // instantiate right extrema
        int ri = q + n - 1;
        for (int i = q + n; i < fn; i++) {
            ys[i] = ys[ri];
            gs[i] = ys[ri];
        }
        // iterate block wise over every structure element
        for (int i = q, r = i + k - 1; i < n + q; i += k, r += k) {
            gs[i] = ys[i];
            hs[r] = ys[r];
            // move working arrays over the copied intensity array from
            // left to right and right to left and keep track of the maximum
            for (int j = 1, gi = i + 1, hi = r - 1; j < k; j++, gi++, hi--) {
                gs[gi] = (gs[gi - 1] < ys[gi]) ? ys[gi] : gs[gi - 1];
                hs[hi] = (hs[hi + 1] < ys[hi]) ? ys[hi] : hs[hi + 1];
            }
        }

        // merge both working arrays by their maximum values
        for (int i = 0, gi = k - 1, hi = 0; i < n; i++, gi++, hi++) {
            yo[i] = (gs[gi] < hs[hi]) ? hs[hi] : gs[gi];
        }

        return yo;
    }
}
