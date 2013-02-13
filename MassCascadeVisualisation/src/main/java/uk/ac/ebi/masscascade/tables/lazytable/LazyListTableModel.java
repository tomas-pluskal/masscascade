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

package uk.ac.ebi.masscascade.tables.lazytable;

import uk.ac.ebi.masscascade.tables.lazytable.util.IndexInterval;
import uk.ac.ebi.masscascade.tables.lazytable.util.LazyList;
import uk.ac.ebi.masscascade.tables.lazytable.util.OnLoadEvent;
import uk.ac.ebi.masscascade.tables.lazytable.util.OnLoadListener;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class LazyListTableModel<T> extends AbstractTableModel {

    private final LazyList<T> lazyList;
    private final List<JTable> dependentTables;

    public LazyListTableModel(LazyList<T> lazyList, JTable table) {

        this(lazyList);
        addDependentTable(table);
    }

    public LazyListTableModel(LazyList<T> lazyList) {

        this.lazyList = lazyList;
        dependentTables = new ArrayList<JTable>();
        addOnLoadListener();
    }

    protected void addOnLoadListener() {

        getLazyList().addOnLoadListener(createOnLoadListener());
    }

    protected OnLoadListener createOnLoadListener() {

        return new OnLoadListener() {

            public void elementLoaded(final OnLoadEvent event) {

                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {

                        for (TableModelListener listener : getTableModelListeners()) {
                            listener.tableChanged(
                                    new TableModelEvent(LazyListTableModel.this, event.getIndexInterval().getStart(),
                                            event.getIndexInterval().getEnd()));
                        }
                        loadAllVisibleRows();
                    }
                });
            }
        };
    }

    protected LazyList<T> getLazyList() {

        return lazyList;
    }

    public void addDependentTable(JTable table) {

        dependentTables.add(table);
    }

    public void removeDependentTable(JTable table) {

        dependentTables.remove(table);
    }

    protected void loadAllVisibleRows() {

        for (JTable table : dependentTables) {
            loadAllVisibleRowsForTable(table);
        }
    }

    protected void loadAllVisibleRowsForTable(JTable table) {

        IndexInterval startEnd = getVisibleRows(table);
        for (int i = startEnd.getStart(); i <= startEnd.getEnd(); i++) {
            getLazyList().getAsynchronous(i);
        }
    }

    protected IndexInterval getVisibleRows(JTable table) {

        Rectangle rect = table.getVisibleRect();
        Point location = rect.getLocation();
        int startRow = table.rowAtPoint(location);
        location.y += rect.getHeight() - 1;
        int endRow = table.rowAtPoint(location);
        return new IndexInterval(startRow, endRow);
    }

    public int getRowCount() {

        return getLazyList().size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {

        if (getLazyList().isLoaded(rowIndex)) {
            return getColumnValue(columnIndex, getLazyList().get(rowIndex));
        } else {
            getLazyList().getAsynchronous(rowIndex);
            return getDummyValueAt(rowIndex, columnIndex);
        }
    }

    public Object getDummyValueAt(int rowIndex, int columnIndex) {

        return 0;
    }

    public abstract Object getColumnValue(int columnIndex, T listElement);
}
