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

package uk.ac.ebi.masscascade.tables.model;

public class BoxProfileTableModel extends ATableModel {

    public BoxProfileTableModel() {

        super(new String[]{"id", "display", "rt [s]", "width [s]", "m/z", "m/z dev.", "area", "info", "shape"});
    }

    @Override
    public void setValueAt(Object aValue, int row, int col) {

        if (col == 1) {
            data[row][col] = (Boolean) aValue;
            this.fireTableCellUpdated(row, col);
        }
    }

    /**
     * Returns the column class.
     *
     * @param c a column index
     */
    @Override
    public Class getColumnClass(int c) {

        switch (c) {
            case 0:
                return String.class;
            case 1:
                return Boolean.class;
            case 2:
                return Double.class;
            case 3:
                return Double.class;
            case 4:
                return Double.class;
            case 5:
                return Double.class;
            case 6:
                return Double.class;
            case 7:
                return String.class;
            case 8:
                return String.class;
            default:
                return String.class;
        }
    }
}
