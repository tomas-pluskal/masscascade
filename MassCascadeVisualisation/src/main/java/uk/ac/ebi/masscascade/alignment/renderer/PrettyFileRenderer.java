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

package uk.ac.ebi.masscascade.alignment.renderer;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.DecimalFormat;

/**
 * Renders a cell in a <code> JTable </code> based on its boolean value. Green if <code> true </code>, red if <code>
 * false </code>.
 */
public class PrettyFileRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = -3037535081597806282L;

    private Color red = new Color(255, 0, 0, 128);
    private Color green = new Color(0, 255, 0, 128);

    public Component getTableCellRendererComponent(JTable jTable, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {

        Component c = super.getTableCellRendererComponent(jTable, value, isSelected, hasFocus, row, column);

        if (c instanceof JLabel && value instanceof Double) {
            JLabel label = (JLabel) c;
            double intensity = (Double) value;
            if (intensity > 0) label.setBackground(green);
            else label.setBackground(red);
            label.setText("");
        }
        return c;
    }
}
