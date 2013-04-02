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

package uk.ac.ebi.masscascade.smoothing;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealVector;

import java.util.Arrays;

import static java.lang.Math.pow;

/**
 * Savitzky-Golay filter implementation. For more information see
 * http://www.nrbook.com/a/bookcpdf/c14-8.pdf. This implementation, however,
 * does not use FFT
 *
 * @author Marcin Rzeźnicki
 */
@SuppressWarnings("deprecation")
public class SavitzkyGolayFilter {

    /**
     * Computes Savitzky-Golay coefficients for given parameters
     *
     * @param nl     numer of past data points filter will use
     * @param nr     number of future data points filter will use
     * @param degree order of smoothing polynomial
     * @return Savitzky-Golay coefficients
     * @throws IllegalArgumentException if {@code nl < 0} or {@code nr < 0} or
     *                                  {@code nl + nr <
     *                                  degree}
     */
    public static double[] computeSGCoefficients(int nl, int nr, int degree) {

        if (nl < 0 || nr < 0 || nl + nr < degree) throw new IllegalArgumentException("Bad arguments");
        Array2DRowRealMatrix matrix = new Array2DRowRealMatrix(degree + 1, degree + 1);
        double[][] a = matrix.getDataRef();
        double sum;
        for (int i = 0; i <= degree; i++) {
            for (int j = 0; j <= degree; j++) {
                sum = (i == 0 && j == 0) ? 1 : 0;
                for (int k = 1; k <= nr; k++)
                    sum += pow(k, i + j);
                for (int k = 1; k <= nl; k++)
                    sum += pow(-k, i + j);
                a[i][j] = sum;
            }
        }
        double[] b = new double[degree + 1];
        b[0] = 1;
        RealVector bV = new ArrayRealVector(b);

        DecompositionSolver lu = new LUDecomposition(matrix).getSolver();
        lu.solve(bV);

        b = bV.toArray();

        double[] coeffs = new double[nl + nr + 1];
        for (int n = -nl; n <= nr; n++) {
            sum = b[0];
            for (int m = 1; m <= degree; m++)
                sum += b[m] * pow(n, m);
            coeffs[n + nl] = sum;
        }
        return coeffs;
    }

    private int nl;

    private int nr;

    /**
     * Constructs Savitzky-Golay filter which uses specified numebr of
     * surrounding data points
     *
     * @param nl numer of past data points filter will use
     * @param nr numer of future data points filter will use
     * @throws IllegalArgumentException of {@code nl < 0} or {@code nr < 0}
     */
    public SavitzkyGolayFilter(int nl, int nr) {

        if (nl < 0 || nr < 0) throw new IllegalArgumentException("Bad arguments");
        this.nl = nl;
        this.nr = nr;
    }

    /**
     * @return number of past data points that this filter uses
     */
    public int getNl() {

        return nl;
    }

    /**
     * @return number of future data points that this filter uses
     */
    public int getNr() {

        return nr;
    }

    /**
     * Sets number of past data points for this filter
     *
     * @param nl number of past data points
     * @throws IllegalArgumentException if {@code nl < 0}
     */
    public void setNl(int nl) {

        if (nl < 0) throw new IllegalArgumentException("nl < 0");
        this.nl = nl;
    }

    /**
     * Sets number of future data points for this filter
     *
     * @param nr number of future data points
     * @throws IllegalArgumentException if {@code nr < 0}
     */
    public void setNr(int nr) {

        if (nr < 0) throw new IllegalArgumentException("nr < 0");
        this.nr = nr;
    }

    /**
     * Smooths data by using Savitzky-Golay filter. This method will use 0 for
     * any element beyond {@code data} which will be needed for computation.
     *
     * @param data   data for filter
     * @param coeffs filter coefficients
     * @return filtered data
     * @throws NullPointerException when any array passed as parameter is null
     */
    public double[] smooth(double[] data, double[] coeffs) {

        return smooth(data, 0, data.length, coeffs);
    }

    /**
     * Smooths data by using Savitzky-Golay filter. Smoothing uses
     * {@code leftPad} and/or {@code rightPad} if you want to augment data on
     * boundaries to achieve smoother results for your purpose. If you do not
     * need this feature you may pass empty arrays (filter will use 0s in this
     * place, so you may want to use appropriate preprocessor)
     *
     * @param data     data for filter
     * @param leftPad  left padding
     * @param rightPad right padding
     * @param coeffs   filter coefficients
     * @return filtered data
     * @throws NullPointerException when any array passed as parameter is null
     */
    public double[] smooth(double[] data, double[] leftPad, double[] rightPad, double[] coeffs) {

        return smooth(data, leftPad, rightPad, 0, new double[][]{coeffs});
    }

    /**
     * Smooths data by using Savitzky-Golay filter. Smoothing uses
     * {@code leftPad} and/or {@code rightPad} if you want to augment data on
     * boundaries to achieve smoother results for your purpose. If you do not
     * need this feature you may pass empty arrays (filter will use 0s in this
     * place, so you may want to use appropriate preprocessor). If you want to
     * use different (probably non-symmetrical) filter near both ends of
     * (padded) data, you will be using {@code bias} and {@code coeffs}.
     * {@code bias} essentially means
     * "how many points of pad should be left out when smoothing". Filters
     * taking this condition into consideration are passed in {@code coeffs}.
     * <tt>coeffs[0]</tt> is used for unbiased data (that is, for
     * <tt>data[bias]..data[data.length-bias-1]</tt>). Its length has to be
     * <tt>nr + nl + 1</tt>. Filters from range
     * <tt>coeffs[coeffs.length - 1]</tt> to
     * <tt>coeffs[coeffs.length - bias]</tt> are used for smoothing first
     * {@code bias} points (that is, from <tt>data[0]</tt> to
     * <tt>data[bias]</tt>) correspondingly. Filters from range
     * <tt>coeffs[1]</tt> to <tt>coeffs[bias]</tt> are used for smoothing last
     * {@code bias} points (that is, for
     * <tt>data[data.length-bias]..data[data.length-1]</tt>). For example, if
     * you use 5 past points and 5 future points for smoothing, but have only 3
     * meaningful padding points - you would use {@code bias} equal to 2 and
     * would pass in {@code coeffs} param filters taking 5-5 points (for regular
     * smoothing), 5-4, 5-3 (for rightmost range of data) and 3-5, 4-5 (for
     * leftmost range). If you do not wish to use pads completely for
     * symmetrical filter then you should pass <tt>bias = nl = nr</tt>
     *
     * @param data     data for filter
     * @param leftPad  left padding
     * @param rightPad right padding
     * @param bias     how many points of pad should be left out when smoothing
     * @param coeffs   array of filter coefficients
     * @return filtered data
     * @throws IllegalArgumentException  when <tt>bias < 0</tt> or
     *                                   <tt>bias > min(nr, nl)</tt>
     * @throws IndexOutOfBoundsException when {@code coeffs} has less than
     *                                   <tt>2*bias + 1</tt> elements
     * @throws NullPointerException      when any array passed as parameter is null
     */
    public double[] smooth(double[] data, double[] leftPad, double[] rightPad, int bias, double[][] coeffs) {

        if (bias < 0 || bias > nr || bias > nl)
            throw new IllegalArgumentException("bias < 0 or bias > nr or bias > nl");
        int dataLength = data.length;
        if (dataLength == 0) return data;
        int n = dataLength + nl + nr;
        double[] dataCopy = new double[n];
        // copy left pad reversed
        int leftPadOffset = nl - leftPad.length;
        if (leftPadOffset >= 0) for (int i = 0; i < leftPad.length; i++) {
            dataCopy[leftPadOffset + i] = leftPad[i];
        }
        else for (int i = 0; i < nl; i++) {
            dataCopy[i] = leftPad[i - leftPadOffset];
        }
        // copy actual data
        for (int i = 0; i < dataLength; i++) {
            dataCopy[i + nl] = data[i];
        }
        // copy right pad
        int rightPadOffset = nr - rightPad.length;
        if (rightPadOffset >= 0) for (int i = 0; i < rightPad.length; i++) {
            dataCopy[i + dataLength + nl] = rightPad[i];
        }
        else for (int i = 0; i < nr; i++) {
            dataCopy[i + dataLength + nl] = rightPad[i];
        }
        // convolution (with savitzky-golay coefficients)
        double[] sdata = new double[dataLength];
        double[] sg;
        for (int b = bias; b > 0; b--) {
            sg = coeffs[coeffs.length - b];
            int x = (nl + bias) - b;
            float sum = 0;
            for (int i = -nl + b; i <= nr; i++) {
                sum += dataCopy[x + i] * sg[nl - b + i];
            }
            sdata[x - nl] = sum;
        }
        sg = coeffs[0];
        for (int x = nl + bias; x < n - nr - bias; x++) {
            float sum = 0;
            for (int i = -nl; i <= nr; i++) {
                sum += dataCopy[x + i] * sg[nl + i];
            }
            sdata[x - nl] = sum;
        }
        for (int b = 1; b <= bias; b++) {
            sg = coeffs[b];
            int x = (n - nr - bias) + (b - 1);
            float sum = 0;
            for (int i = -nl; i <= nr - b; i++) {
                sum += dataCopy[x + i] * sg[nl + i];
            }
            sdata[x - nl] = sum;
        }
        for (int i = 0; i < sdata.length; i++) {
            if (sdata[i] < 0) {
                sdata[i] = 0;
            }
        }
        return sdata;
    }

    /**
     * Runs filter on data from {@code from} (including) to {@code to}
     * (excluding). Data beyond range spanned by {@code from} and {@code to}
     * will be used for padding
     *
     * @param data   data for filter
     * @param from   inedx of the first element of data
     * @param to     index of the first element omitted
     * @param coeffs filter coefficients
     * @return filtered data
     * @throws ArrayIndexOutOfBoundsException if <tt>to > data.length</tt>
     * @throws IllegalArgumentException       if <tt>from < 0</tt> or
     *                                        <tt>to > data.length</tt>
     * @throws NullPointerException           if {@code data} is null or {@code coeffs} is
     *                                        null
     */
    public double[] smooth(double[] data, int from, int to, double[] coeffs) {

        return smooth(data, from, to, 0, new double[][]{coeffs});
    }

    /**
     * Runs filter on data from {@code from} (including) to {@code to}
     * (excluding). Data beyond range spanned by {@code from} and {@code to}
     * will be used for padding. See
     * {@link #smooth(double[], double[], double[], int, double[][])} for usage
     * of {@code bias}
     *
     * @param data   data for filter
     * @param from   inedx of the first element of data
     * @param to     index of the first element omitted
     * @param bias   how many points of pad should be left out when smoothing
     * @param coeffs filter coefficients
     * @return filtered data
     * @throws ArrayIndexOutOfBoundsException if <tt>to > data.length</tt> or
     *                                        when {@code coeffs} has less than <tt>2*bias + 1</tt>
     *                                        elements
     * @throws IllegalArgumentException       if <tt>from < 0</tt> or
     *                                        <tt>to > data.length</tt> or <tt>from > to</tt> or when
     *                                        <tt>bias < 0</tt> or <tt>bias > min(nr, nl)</tt>
     * @throws NullPointerException           if {@code data} is null or {@code coeffs} is
     *                                        null
     */
    public double[] smooth(double[] data, int from, int to, int bias, double[][] coeffs) {

        double[] leftPad = Arrays.copyOfRange(data, 0, from);
        double[] rightPad = Arrays.copyOfRange(data, to, data.length);
        double[] dataCopy = Arrays.copyOfRange(data, from, to);
        return smooth(dataCopy, leftPad, rightPad, bias, coeffs);
    }
}
