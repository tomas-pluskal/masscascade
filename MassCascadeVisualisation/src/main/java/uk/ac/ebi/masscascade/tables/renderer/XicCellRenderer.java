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

package uk.ac.ebi.masscascade.tables.renderer;

import org.apache.commons.math3.util.FastMath;
import uk.ac.ebi.masscascade.core.chromatogram.MassChromatogram;
import uk.ac.ebi.masscascade.utilities.math.LinearEquation;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.Collections;

/**
 * Custom number cell renderer for XIC objects.
 *
 * @author Stephan Beisken
 */
public class XicCellRenderer extends DefaultTableCellRenderer {

    private XYList xyList;

    @Override
    public Component getTableCellRendererComponent(JTable jTable, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {

        Component c = super.getTableCellRendererComponent(jTable, value, isSelected, hasFocus, row, column);

        if (c instanceof JLabel && value instanceof MassChromatogram) {

            JLabel label = (JLabel) c;
            label.setHorizontalAlignment(JLabel.RIGHT);

            xyList = ((MassChromatogram) value).getData();
        }
        return c;
    }

    public void paint(Graphics g) {

        g.setColor(Color.RED);

        if (xyList == null) return;

        if (xyList.size() < 3) {

            LinearEquation[] eqXY = getLqXY();

            int y1 = getHeight();
            int y2;

            int x1 = (int) eqXY[0].getY(xyList.get(0).x);
            int x2;

            for (int i = 0; i < xyList.size(); i++) {

                x2 = (int) eqXY[0].getY(xyList.get(i).x);
                y2 = (int) (getHeight() - eqXY[1].getY(xyList.get(i).y));

                g.drawLine(x1, y1, x2, y2);

                x1 = x2;
                y1 = y2;
            }

//            g.drawLine(x1, y1, x1, getHeight());
        } else {

            int np = xyList.size();
            float[] d = new float[np]; // Newton form coefficients
            float[] x = new float[np]; // x-coordinates of nodes
            float y;
            float t;

            float[] a = new float[np];
            float t1;
            float t2;
            float[] h = new float[np];

            for (int i = 0; i < np; i++) {
                x[i] = (float) xyList.get(i).x;
                d[i] = (float) xyList.get(i).y;
            }

            for (int i = 1; i <= np - 1; i++) h[i] = x[i] - x[i - 1];

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
            float[] g2dXPoints = new float[(np * 3) - 3 + 1];
            float[] g2dYPoints = new float[(np * 3) - 3 + 1];
            int g2dI = 1;
            g2dXPoints[0] = x[0];
            g2dYPoints[0] = d[0];
            for (int i = 1; i < np; i++) {
                // loop over intervals between nodes
                for (int j = 1; j <= 3; j++) {
                    t1 = (h[i] * j) / 3;
                    t2 = h[i] - t1;
                    y =
                            ((-a[i - 1] / 6 * (t2 + h[i]) * t1 + d[i - 1]) * t2 + (-a[i] / 6 * (t1 + h[i]) * t2 +
                                    d[i]) * t1) / h[i];
                    t = x[i - 1] + t1;

                    g2dXPoints[g2dI] = t;
                    g2dYPoints[g2dI] = y;
                    g2dI++;
                }
            }

            LinearEquation[] eqXY = getLqXY(g2dXPoints, g2dYPoints);

            int x1 = (int) eqXY[0].getY(g2dXPoints[0]);
            int x2;
            int y1 = (int) (getHeight() - eqXY[1].getY(g2dYPoints[0]));
            int y2;

            for (int i = 1; i < g2dXPoints.length; i++) {

                x2 = (int) FastMath.round(eqXY[0].getY(g2dXPoints[i]));
                y2 = (int) FastMath.round(getHeight() - eqXY[1].getY(g2dYPoints[i]));

                if (x1 == x2) {
                    continue;
                }

                g.drawLine(x1, y1, x2, y2);

                x1 = x2;
                y1 = y2;
            }
        }
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

    private LinearEquation[] getLqXY() {

        double yMax = 0;
        double yMin = Double.MAX_VALUE;

        double xMin = Double.MAX_VALUE;
        double xMax = 0;

        for (XYPoint xyPoint : xyList) {

            if (yMax < xyPoint.y) yMax = xyPoint.y;
            if (yMin > xyPoint.y) yMin = xyPoint.y;

            if (xMax < xyPoint.x) xMax = xyPoint.x;
            if (xMin > xyPoint.x) xMin = xyPoint.x;
        }

        xMin -= 5;
        xMax += 5;

        double mx = getWidth() / (xMax - xMin);
        double bx = getWidth() - (mx * xMax);
        LinearEquation lqX = new LinearEquation(mx ,bx);

        double my = getHeight() / (yMax - yMin);
        double by = getHeight() - (my * yMax);
        LinearEquation lqY = new LinearEquation(my ,by);

        return new LinearEquation[] { lqX, lqY };
    }

    private LinearEquation[] getLqXY(float[] g2dXPoints, float[] g2dYPoints) {

        double yMax = 0;
        double yMin = Double.MAX_VALUE;

        double xMin = Double.MAX_VALUE;
        double xMax = 0;

        for (int i = 0; i < g2dXPoints.length; i++) {

            if (yMax < g2dYPoints[i]) yMax = g2dYPoints[i];
            if (yMin > g2dYPoints[i]) yMin = g2dYPoints[i];

            if (xMax < g2dXPoints[i]) xMax = g2dXPoints[i];
            if (xMin > g2dXPoints[i]) xMin = g2dXPoints[i];
        }

        xMin -= 5;
        xMax += 5;

        double mx = getWidth() / (xMax - xMin);
        double bx = getWidth() - (mx * xMax);
        LinearEquation lqX = new LinearEquation(mx ,bx);

        double my = getHeight() / (yMax - yMin);
        double by = getHeight() - (my * yMax);
        LinearEquation lqY = new LinearEquation(my ,by);

        return new LinearEquation[] { lqX, lqY };
    }
}
