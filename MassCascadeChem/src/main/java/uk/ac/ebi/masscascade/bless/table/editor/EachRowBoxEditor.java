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
import uk.ac.ebi.masscascade.commons.compound.CompoundEntity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class EachRowBoxEditor extends DefaultCellEditor {

    public EachRowBoxEditor() {
        super(new JComboBox());
    }

    public Component getTableCellEditorComponent(final JTable table, Object value, final boolean isSelected, final int row,
            int column) {

//        final JComboBox combo = (JComboBox) super.getTableCellEditorComponent(table, value, isSelected, row, column);
        final JComboBox combo = new JComboBox();
        combo.removeAllItems();
        combo.addMouseListener(new MouseAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                String item = (String) combo.getSelectedItem();
                int id = Integer.parseInt(item.substring(0, item.indexOf(":"))) - 1;
                ((BlessTableModel) table.getModel()).updateById(row, id);
            }
        });

        int i = 1;
        for (CompoundEntity entry : ((BlessTableModel) table.getModel()).getSpectrum(row).getCompounds())
            combo.addItem(i++ + ": " + entry.getName());

        return combo;
    }
}