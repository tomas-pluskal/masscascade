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

/**
 * Table model for a comprehensive feature-centered feature table.
 *
 * @author Stephan Beisken
 */
public class DetailFeatureTableModel extends ATableModel {

    public DetailFeatureTableModel() {

        super(new String[]{"id", "rt [s]", "width [s]", "m/z", "m/z dev.", "area", "info", "shape"});
    }

    /**
     * Returns the column class.
     *
     * @param c a column index
     */
    @Override
    public Class getColumnClass(int c) {

        if (c == 4 || c == 5) return Double.class;
        return getValueAt(0, c).getClass();
    }
}