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

package uk.ac.ebi.masscascade.bless.table.editor;

import uk.ac.ebi.masscascade.bless.table.BlessTableModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;

public class AnnotationEditor extends AbstractCellEditor implements TableCellEditor {

    private JLabel label;
    private JScrollPane pane;

    public AnnotationEditor() {

        label = new JLabel();
        label.setOpaque(true);
        label.setBackground(Color.WHITE);
        label.setForeground(Color.BLACK);
        pane = new JScrollPane(label, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int rowIndex,
            int vColIndex) {

        if (vColIndex != BlessTableModel.ANNOTATIONS_COLUMN) return null;

        label.setText(value.toString());

        pane.setHorizontalScrollBar(pane.createHorizontalScrollBar());
        pane.setVerticalScrollBar(pane.createVerticalScrollBar());
        pane.setBorder(new EmptyBorder(0, 0, 0, 0));
        pane.setToolTipText(value != null ? value.toString() : "");

        return pane;
    }

    public Object getCellEditorValue() {
        return label.getText();
    }
}
