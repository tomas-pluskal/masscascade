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
package uk.ac.ebi.masscascade.tables;

import uk.ac.ebi.masscascade.tables.renderer.NumberCellRenderer;
import uk.ac.ebi.masscascade.tables.renderer.ScientificCellRenderer;
import uk.ac.ebi.masscascade.tables.renderer.XicCellRenderer;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

/**
 * Class implementing a table for feature comparison.
 */
public class DetailFeatureTable extends JTable {

    /**
     * Constructs an empty table for feature comparison.
     */
    public DetailFeatureTable() {

        super();

        this.setAutoCreateRowSorter(true);
        this.getTableHeader().setResizingAllowed(true);
    }

    /**
     * Sets the table model with the correct renderers.
     *
     * @param tableModel the table model
     */
    public void setModel(AbstractTableModel tableModel) {

        super.setModel(tableModel);

        this.setDefaultRenderer(Double.class, new NumberCellRenderer());
        this.getColumnModel().getColumn(4).setCellRenderer(new ScientificCellRenderer());
        this.getColumnModel().getColumn(5).setCellRenderer(new ScientificCellRenderer());
        this.getColumnModel().getColumn(7).setCellRenderer(new XicCellRenderer());
    }
}
