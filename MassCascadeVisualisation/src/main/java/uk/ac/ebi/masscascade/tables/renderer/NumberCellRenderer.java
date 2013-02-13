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

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.DecimalFormat;

/**
 * Custom number cell renderer for double values.
 *
 * @author Stephan Beisken
 */
public class NumberCellRenderer extends DefaultTableCellRenderer {

    private DecimalFormat numberFormat;

    public NumberCellRenderer() {

        numberFormat = new DecimalFormat("#,###0.000;(#,###0.000)");
    }

    public NumberCellRenderer(DecimalFormat numberFormat) {

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
            label.setText(text);

            label.setForeground(num.doubleValue() < 0 ? Color.RED : Color.BLACK);
        }
        return c;
    }
}
