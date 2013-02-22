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

package uk.ac.ebi.masscascade.alignment.renderer;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.DecimalFormat;

/**
 * Renders a cell in a <code> JTable </code>. <code> Double </code> values are formatted to one decimal points and
 * decorated with a "s" suffix indicating the range of the value.
 */
public class PrettySecRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = -2972487224258966497L;

    private double sec;
    private DecimalFormat numberFormat;

    public PrettySecRenderer(double sec) {
        this.sec = sec;
        numberFormat = new DecimalFormat("#,###0.0;(#,###0.0)");
    }

    public PrettySecRenderer(double sec, DecimalFormat numberFormat) {
        this.sec = sec;
        this.numberFormat = numberFormat;
    }

    public Component getTableCellRendererComponent(JTable jTable, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {

        Component c = super.getTableCellRendererComponent(jTable, value, isSelected, hasFocus, row, column);

        if (c instanceof JLabel && value instanceof Number) {
            JLabel label = (JLabel) c;
            label.setHorizontalAlignment(JLabel.RIGHT);
            Number num = (Number) value;
            String text = numberFormat.format(num);
            text += " \u00B1 " + sec + "s";
            label.setText(text);
        }
        return c;
    }
}
