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

import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.utilities.math.MathUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.DecimalFormat;

/**
 * Custom number cell renderer for profile objects.
 *
 * @author Stephan Beisken
 */
public class ScientificCellRenderer extends DefaultTableCellRenderer {

    public Component getTableCellRendererComponent(JTable jTable, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {

        Component c = super.getTableCellRendererComponent(jTable, value, isSelected, hasFocus, row, column);

        if (c instanceof JLabel && value instanceof Profile) {
            JLabel label = (JLabel) c;
            label.setHorizontalAlignment(JLabel.RIGHT);
            label.setText("" + (MathUtils.SCIENTIFIC_FORMAT.format(((Profile) value).getArea())));
        }

        if (c instanceof JLabel && value instanceof Number) {
            JLabel label = (JLabel) c;
            label.setHorizontalAlignment(JLabel.RIGHT);
            Number num = (Number) value;
            String text = MathUtils.SCIENTIFIC_FORMAT.format(num);
            label.setText(text);
        }

        return c;
    }
}