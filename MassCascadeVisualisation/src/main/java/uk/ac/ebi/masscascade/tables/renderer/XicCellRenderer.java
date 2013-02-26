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

import uk.ac.ebi.masscascade.core.chromatogram.MassChromatogram;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

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

        double yMax = 0;
        double yMin = Double.MAX_VALUE;

        double xMin = Double.MAX_VALUE;
        double xMax = 0;

        if (xyList == null) return;

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

        double my = getHeight() / (yMax - yMin);
        double by = getHeight() - (my * yMax);

        if (xyList.size() < 3) {

            int y1 = getHeight();
            int y2 = y1;

            int x1 = (int) (mx * xyList.get(0).x + bx);
            int x2 = x1;

            for (int i = 0; i < xyList.size(); i++) {

                x2 = (int) (mx * xyList.get(i).x + bx);
                y2 = (int) (getHeight() - (my * xyList.get(i).y + by));

                g.drawLine(x1, y1, x2, y2);

                x1 = x2;
                y1 = y2;
            }

            g.drawLine(x1, y1, x1, getHeight());

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
                x[i] = (float) (mx * xyList.get(i).x + bx);
                d[i] = (float) (getHeight() - my * xyList.get(i).y + by);
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
            int[] g2dXPoints = new int[(np * 3) - 3 + 1];
            int[] g2dYPoints = new int[(np * 3) - 3 + 1];
            int g2dI = 1;
            g2dXPoints[0] = Math.round(x[0]);
            g2dYPoints[0] = Math.round(d[0]);
            for (int i = 1; i < np; i++) {
                // loop over intervals between nodes
                for (int j = 1; j <= 3; j++) {
                    t1 = (h[i] * j) / 3;
                    t2 = h[i] - t1;
                    y =
                            ((-a[i - 1] / 6 * (t2 + h[i]) * t1 + d[i - 1]) * t2 + (-a[i] / 6 * (t1 + h[i]) * t2 +
                                    d[i]) * t1) / h[i];
                    t = x[i - 1] + t1;

                    g2dXPoints[g2dI] = Math.round(t);
                    g2dYPoints[g2dI] = (int) ((Math.round(y) > yMax) ? yMax : Math.round(y));
                    g2dI++;
                }
            }

            g.drawPolyline(g2dXPoints, g2dYPoints, g2dXPoints.length);
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
}
