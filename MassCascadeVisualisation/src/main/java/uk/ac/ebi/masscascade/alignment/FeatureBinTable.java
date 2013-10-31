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

package uk.ac.ebi.masscascade.alignment;

import uk.ac.ebi.masscascade.alignment.featurebins.FeatureBin;
import uk.ac.ebi.masscascade.alignment.renderer.PrettyFileRenderer;
import uk.ac.ebi.masscascade.alignment.renderer.PrettyPpmRenderer;
import uk.ac.ebi.masscascade.alignment.renderer.PrettySecRenderer;
import uk.ac.ebi.masscascade.tables.renderer.ScientificCellRenderer;
import uk.ac.ebi.masscascade.tables.renderer.XicCellRenderer;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.util.ArrayList;
import java.util.List;

/**
 * A table tailored to the {@link FeatureBinTableModel}. The table automatically sets column sorter for the m/z and
 * retention time columns and renderer for all columns.
 */
public class FeatureBinTable extends JTable {

    private static final long serialVersionUID = 401128003027178352L;

    /**
     * Constructs a table tailored to the <code> FeatureBinTableModel </code>.
     */
    public FeatureBinTable() {

        super();
        setAutoCreateRowSorter(true);
        getTableHeader().setResizingAllowed(true);
    }

    /**
     * Sets the table model which should be of type <code> FeatureBinTableModel </code>. The methods layouts and
     * decorates the table.
     *
     * @param tableModel the <code> FeatureBinTableModel </code>
     */
    @Override
    public void setModel(TableModel tableModel) {

        super.setModel(tableModel);

        if (!(tableModel instanceof FeatureBinTableModel)) return;

        getColumnModel().getColumn(0).setCellRenderer(
                new PrettyPpmRenderer(((FeatureBinTableModel) tableModel).getPpm()));
        getColumnModel().getColumn(1).setCellRenderer(
                new PrettySecRenderer(((FeatureBinTableModel) tableModel).getSec()));
        getColumnModel().getColumn(2).setCellRenderer(new ScientificCellRenderer());
        getColumnModel().getColumn(4).setCellRenderer(new ScientificCellRenderer());
        getColumnModel().getColumn(5).setCellRenderer(new XicCellRenderer());
        for (int i = FeatureBin.COLUMNS; i < tableModel.getColumnCount(); i++)
            getColumnModel().getColumn(i).setCellRenderer(new PrettyFileRenderer());

        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>();
        List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
        sorter.setModel(tableModel);
        sorter.setSortKeys(sortKeys);

        revalidate();
    }
}
