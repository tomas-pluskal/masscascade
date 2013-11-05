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

package uk.ac.ebi.masscascade.charts.painter;

import info.monitorenter.gui.chart.ITracePoint2D;
import info.monitorenter.gui.chart.traces.painters.ATracePainter;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * A trace painter that connects data points with natural cubic splines.
 */
public class TracePainterSpline extends ATracePainter {

    /**
     * Generated <code>serialVersionUID</code>.
     */
    private static final long serialVersionUID = 705525809734208048L;

    /**
     * The list of x coordinates collected in one paint iteration.
     */
    private List<Integer> xPoints;

    /**
     * The list of y coordinates collected in one paint iteration.
     */
    private List<Integer> yPoints;

    /**
     * Resolution of splines (number of line segments between points)
     */
    private int precision;

    /**
     * Constructs a trace painter that uses natural cubic splines.
     *
     * @param precision the number of line segments between points
     */
    public TracePainterSpline(int precision) {

        if (precision <= 0) throw new IllegalArgumentException("Requires precision > 0");
        this.precision = precision;
    }

    /**
     * @see info.monitorenter.gui.chart.ITracePainter#endPaintIteration(java.awt.Graphics)
     */
    @Override
    public void endPaintIteration(final Graphics g2d) {

        if (g2d == null) {
            return;
        } else if (xPoints.size() < 5) {
            drawStraightPolyline(g2d);
        } else {
            // construct spline, adapted from JFreeChart
            xPoints.add(getPreviousX());
            yPoints.add(getPreviousY());
            int np = xPoints.size();
            float[] d = new float[np]; // Newton form coefficients
            float[] x = new float[np]; // x-coordinates of nodes
            float y;
            float t;

            float[] a = new float[np];
            float t1;
            float t2;
            float[] h = new float[np];
            int yMax = Integer.MIN_VALUE;

            for (int i = 0; i < np; i++) {
                x[i] = xPoints.get(i);
                d[i] = yPoints.get(i);
                if (yPoints.get(i) > yMax) yMax = yPoints.get(i);
            }

            for (int i = 1; i <= np - 1; i++) {
                h[i] = x[i] - x[i - 1];
            }
            float[] sub = new float[np - 1];
            float[] diag = new float[np - 1];
            float[] sup = new float[np - 1];

            for (int i = 1; i <= np - 2; i++) {
                diag[i] = (h[i] + h[i + 1]) / 3;
                sup[i] = h[i + 1] / 6;
                sub[i] = h[i] / 6;
                a[i] = (d[i + 1] - d[i]) / h[i + 1] - (d[i] - d[i - 1]) / h[i];
            }
            solveTridiag(sub, diag, sup, a, np - 2);

            // note that a[0]=a[np-1]=0, draw
            int[] g2dXPoints = new int[(np * precision) - precision * 3 + 1]; // padding
            int[] g2dYPoints = new int[(np * precision) - precision * 3 + 1]; // padding
            int g2dI = 1;
            g2dXPoints[0] = Math.round(x[0]);
            g2dYPoints[0] = Math.round(d[0]);
            for (int i = 1; i < np; i++) {
                if (i < 3 || i >= np - 2) { // padding
                    g2dXPoints[g2dI] = xPoints.get(i);
                    g2dYPoints[g2dI] = yPoints.get(i);
                    g2dI++;
                } else {
                    // loop over intervals between nodes
                    for (int j = 1; j <= precision; j++) {
                        t1 = (h[i] * j) / precision;
                        t2 = h[i] - t1;
                        y =
                                ((-a[i - 1] / 6 * (t2 + h[i]) * t1 + d[i - 1]) * t2 + (-a[i] / 6 * (t1 + h[i]) * t2 +
                                        d[i]) * t1) / h[i];
                        t = x[i - 1] + t1;

                        g2dXPoints[g2dI] = Math.round(t);
                        g2dYPoints[g2dI] = (Math.round(y) > yMax) ? yMax : Math.round(y);
                        g2dI++;
                    }
                }
            }
            // hack to avoid random paint action by g2d
            if (g2dYPoints.length > 5 && g2dYPoints[3] == 0 && g2dYPoints[4] == 0) {
                drawStraightPolyline(g2d);
            } else {
                g2d.drawPolyline(g2dXPoints, g2dYPoints, g2dXPoints.length);
            }
        }
    }

    private void drawStraightPolyline(Graphics g2d) {
        final int[] xAlt = new int[xPoints.size() + 1];
        int count = 0;
        for (final Integer xpoint : xPoints) {
            xAlt[count] = xpoint;
            count++;
        }
        xAlt[count] = this.getPreviousX();

        final int[] yAlt = new int[yPoints.size() + 1];
        count = 0;
        for (final Integer ypoint : yPoints) {
            yAlt[count] = ypoint;
            count++;
        }
        yAlt[count] = this.getPreviousY();

        g2d.drawPolyline(xAlt, yAlt, xAlt.length);
    }

    private void solveTridiag(float[] sub, float[] diag, float[] sup, float[] b, int n) {

        /**
         * solve linear system with tridiagonal n by n matrix a
         * using Gaussian elimination *without* pivoting
         * where   a(i,i-1) = sub[i]  for 2<=i<=n
         * a(i,i)   = diag[i] for 1<=i<=n
         * a(i,i+1) = sup[i]  for 1<=i<=n-1
         * (the values sub[1], sup[n] are ignored)
         * right hand side vector b[1:n] is overwritten with solution
         * NOTE: 1...n is used in all arrays, 0 is unused
         */
        int i;
        // factorization and forward substitution
        for (i = 2; i <= n; i++) {
            sub[i] = sub[i] / diag[i - 1];
            diag[i] = diag[i] - sub[i] * sup[i - 1];
            b[i] = b[i] - sub[i] * b[i - 1];
        }
        b[n] = b[n] / diag[n];
        for (i = n - 1; i >= 1; i--) {
            b[i] = (b[i] - sup[i] * b[i + 1]) / diag[i];
        }
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {

        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (this.getClass() != obj.getClass()) return false;
        final TracePainterSpline other = (TracePainterSpline) obj;

        if (this.xPoints == null) {
            if (other.xPoints != null) return false;
        } else if (!this.xPoints.equals(other.xPoints)) return false;
        if (this.yPoints == null) {
            if (other.yPoints != null) return false;
        } else if (!this.yPoints.equals(other.yPoints)) return false;

        return true;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((this.xPoints == null) ? 0 : this.xPoints.hashCode());
        result = prime * result + ((this.yPoints == null) ? 0 : this.yPoints.hashCode());

        return result;
    }

    /**
     * @see info.monitorenter.gui.chart.traces.painters.ATracePainter#paintPoint(int,
     *      int, int, int, java.awt.Graphics,
     *      info.monitorenter.gui.chart.ITracePoint2D)
     */
    @Override
    public void paintPoint(final int absoluteX, final int absoluteY, final int nextX, final int nextY, final Graphics g,
                           final ITracePoint2D original) {

        super.paintPoint(absoluteX, absoluteY, nextX, nextY, g, original);

        this.xPoints.add(absoluteX);
        this.yPoints.add(absoluteY);
    }

    /**
     * @see info.monitorenter.gui.chart.ITracePainter#startPaintIteration(java.awt.Graphics)
     */
    @Override
    public void startPaintIteration(final Graphics g2d) {

        super.startPaintIteration(g2d);

        if (xPoints == null) xPoints = new LinkedList<Integer>();
        else xPoints.clear();

        if (yPoints == null) yPoints = new LinkedList<Integer>();
        else yPoints.clear();
    }
}
