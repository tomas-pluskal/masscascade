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

package uk.ac.ebi.masscascade.tables.model;

import javax.swing.table.AbstractTableModel;

/**
 * Abstract table model for frequently used MassCascade tables.
 */
public abstract class ATableModel extends AbstractTableModel {

    protected String[] tableHeaders;
    protected Object[][] data;

    /**
     * Constructs a model with the given headers.
     *
     * @param tableHeaders the column header array
     */
    public ATableModel(String[] tableHeaders) {

        this.tableHeaders = tableHeaders;
        this.data = new Object[0][0];
    }

    /**
     * Sets the model data.
     *
     * @param data model data
     */
    public void setData(Object[][] data) {

        this.data = data;
    }

    /**
     * Returns the model data.
     *
     * @return the model data
     */
    public Object[][] getData() {

        return data;
    }

    /**
     * Returns the number of column headers.
     */
    public int getColumnCount() {

        return tableHeaders.length;
    }

    /**
     * Returns the number of rows.
     */
    public int getRowCount() {

        return data.length;
    }

    /**
     * Returns the name of column x.
     *
     * @param col a column index
     */
    public String getColumnName(int col) {

        return tableHeaders[col];
    }

    /**
     * Returns the specified row value.
     *
     * @param row a row index
     * @param col a col index
     */
    public Object getValueAt(int row, int col) {

        return data[row][col];
    }

    /**
     * Returns the column class.
     *
     * @param c a column index
     */
    public Class getColumnClass(int c) {

        return getValueAt(0, c).getClass();
    }
}
