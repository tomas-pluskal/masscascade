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

package uk.ac.ebi.masscascade.tables;

import uk.ac.ebi.masscascade.tables.renderer.NumberCellRenderer;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

/**
 * Class implementing a table for grouped profile comparison.
 */
public class GroupProfileTable extends JTable {

    /**
     * Constructs an empty table for grouped profile comparison.
     */
    public GroupProfileTable() {

        super();

        this.setAutoCreateRowSorter(true);
    }

    /**
     * Sets the table model with the correct renderers.
     *
     * @param tableModel the table model
     */
    public void setModel(AbstractTableModel tableModel) {

        super.setModel(tableModel);

        this.getColumnModel().getColumn(1).setCellRenderer(new NumberCellRenderer());
    }
}
