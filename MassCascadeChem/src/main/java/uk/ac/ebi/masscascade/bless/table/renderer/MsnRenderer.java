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

package uk.ac.ebi.masscascade.bless.table.renderer;

import uk.ac.ebi.masscascade.bless.table.BlessTableModel;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.utilities.math.LinearEquation;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;

public class MsnRenderer extends DefaultTableCellRenderer {

    private ArrayList<XYPoint> dps;
    private Range mzR;
    private Range inR;

    private static final int lp = 5;
    private static final int lpp = 10;
    private static final int d = 2;
    private static final int f = 12;

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {

        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (column == BlessTableModel.MSN_COLUMN && ((ArrayList) value).size() != 0) {
            label.setText("");

            dps = (ArrayList) value;
            mzR = new ExtendableRange(dps.get(0).x);
            inR = new ExtendableRange(dps.get(0).y);
            for (XYPoint xp : dps) {
                mzR.extendRange(xp.x);
                inR.extendRange(xp.y);
            }
        } else {
            dps = new ArrayList<>();
            mzR = new ExtendableRange();
            inR = new ExtendableRange();
        }

        return label;
    }

    public void paint(Graphics g) {

        int h = getHeight();
        int w = getWidth();

        g.drawLine(lp, h - lp, w - lp, h - lp);
        g.drawLine(w - lp, h - lp - d, w - lp, h - lp + d);
        g.drawLine(lp, h - lp, lp, lp);
        g.drawLine(lp - d, lp, lp + d, lp);

        g.setColor(Color.BLUE);

        int ubx;
        int lbx;
        int uby;
        int lby;

        if (dps.size() == 1) {
            ubx = (int) mzR.getUpperBounds() + 1;
            lbx = (int) mzR.getLowerBounds();
            uby = (int) inR.getUpperBounds();
            lby = (int) inR.getLowerBounds() + 1;
        } else {
            ubx = (int) mzR.getUpperBounds();
            lbx = (int) mzR.getLowerBounds();
            uby = (int) inR.getUpperBounds();
            lby = (int) inR.getLowerBounds();
        }

        LinearEquation eqX = new LinearEquation(new XYPoint(lbx, lpp), new XYPoint(ubx, w - lpp * d));
        LinearEquation eqY = new LinearEquation(new XYPoint(lby, h - lpp), new XYPoint(uby, lpp + f));

        g.setFont(new Font("Monospaced", Font.PLAIN, 11));

        int i = 1;
        for (XYPoint xy : dps) {
            g.drawString("" + i++, (int) eqX.getY(xy.x) - 3, (int) eqY.getY(xy.y) - d);
            g.drawLine((int) eqX.getY(xy.x), h - lp, (int) eqX.getY(xy.x), (int) eqY.getY(xy.y));
        }
    }
}
