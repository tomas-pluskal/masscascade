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
    }
}
