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

package uk.ac.ebi.masscascade.alignment;

import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.container.ProfileContainer;
import uk.ac.ebi.masscascade.utilities.DataUtils;
import uk.ac.ebi.masscascade.utilities.range.ToleranceRange;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Table model for arranging multiple profile containers in a <code> JTable </code>. The profiles in the containers are
 * grouped by their m/z and retention time values based on the given tolerance values.
 */
public class AlignedProfileModel extends AbstractTableModel {

    private static final long serialVersionUID = 1526497097606220131L;

    private List<ProfileContainer> profileContainers;
    private double ppm;
    private double sec;

    private List<AlignedRow> alignedRows;
    private String[] headers = new String[]{"m/z", "rt", "area", "label", "m/z dev", "shape"};

    /**
     * Constructs a table model for arranging multiple profile containers in a single table.
     *
     * @param profileContainers the list of profile containers
     * @param ppm               the m/z tolerance value in ppm
     * @param sec               the time tolerance value in seconds
     */
    public AlignedProfileModel(List<ProfileContainer> profileContainers, double ppm, double sec) {

        this.profileContainers = profileContainers;
        this.ppm = ppm;
        this.sec = sec;

        init();
    }

    /**
     * Returns a map of profile container ids to profiles, that are grouped within the row at the <code> rowIndex
     * </code>.
     *
     * @param rowIndex the row index for which the map should be retrieved
     * @return the profile container ids to profiles map
     */
    public Map<String, Profile> getProfilesForRow(int rowIndex) {

        Map<String, Profile> idToProfile = new HashMap<String, Profile>();
        for (Map.Entry<Integer, Integer> entry : alignedRows.get(rowIndex).getContainerIndexToProfileId().entrySet()) {
            Profile profile = profileContainers.get(entry.getKey()).getProfile(entry.getValue());
            String id = "(" + entry.getKey() + "-" + profile.getId() + ")";
            idToProfile.put(id, profile);
        }
        return idToProfile;
    }

    /**
     * Returns the m/z tolerance value in ppm.
     *
     * @return the m/z tolerance value
     */
    public double getPpm() {
        return ppm;
    }

    /**
     * Returns the time tolerance value in seconds.
     *
     * @return the time tolerance value
     */
    public double getSec() {
        return sec;
    }

    /**
     * Returns the number of rows in the table model.
     *
     * @return the number of rows
     */
    @Override
    public int getRowCount() {
        return alignedRows.size();
    }

    /**
     * Returns the number of columns in the table model
     *
     * @return the number of columns
     */
    @Override
    public int getColumnCount() {
        return AlignedRow.COLUMNS + profileContainers.size();
    }

    /**
     * Returns the column name.
     *
     * @param col the column index
     * @return the column name
     */
    public String getColumnName(int col) {

        if (col < AlignedRow.COLUMNS) return headers[col];
        else return "(" + (col - AlignedRow.COLUMNS) + ") " + profileContainers.get(col - AlignedRow.COLUMNS).getId();
    }

    /**
     * Returns the value at a specific position in the table defined by its row and column index.
     *
     * @param rowIndex    the row index
     * @param columnIndex the column index
     * @return the value at the defined position
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        Object value;
        switch (columnIndex) {
            case 0:
                value = alignedRows.get(rowIndex).getMz();
                break;
            case 1:
                value = alignedRows.get(rowIndex).getRt();
                break;
            case 2:
                value = alignedRows.get(rowIndex).getArea();
                break;
            case 3:
                value = alignedRows.get(rowIndex).getLabel();
                break;
            case 4:
                value = alignedRows.get(rowIndex).getMzDev();
                break;
            case 5:
                value = alignedRows.get(rowIndex).getChromatogram();
                break;
            default:
                value = alignedRows.get(rowIndex).isPresent(columnIndex - AlignedRow.COLUMNS);
                break;
        }
        return value;
    }

    /**
     * Returns the class of the objects stored in a particular column.
     *
     * @param c the column index
     * @return the class of the objects in that column
     */
    @Override
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    /**
     * Initiates the table model by grouping all profiles of all profile containers into m/z and rt bins based on the
     * given m/z and time tolerance values.
     */
    private void init() {

        int index = 0;
        TreeSet<AlignedRow> alignedRows = new TreeSet<AlignedRow>(new AlignedRowComparator(sec));
        for (ProfileContainer container : profileContainers) {
            for (Profile profile : container) {
                AlignedRow row = new AlignedRow(index, profile, container.size());
                AlignedRow closestAlignedRow = DataUtils.getClosestValue(row, alignedRows);

                if (closestAlignedRow == null) alignedRows.add(row);
                else if (isWithinMz(closestAlignedRow, row)) {
                    closestAlignedRow.add(index, profile);
                    alignedRows.add(closestAlignedRow);
                } else alignedRows.add(row);
            }
            index++;
        }

        this.alignedRows = new ArrayList<AlignedRow>(alignedRows);
    }

    /**
     * Tests if m/z values of two rows are within the defined m/z tolerance range of each other.
     *
     * @param alignedRow the target row to be compared to
     * @param row        the query row to be used for comparison
     * @return if the row is in range of the aligned row
     */
    private boolean isWithinMz(AlignedRow alignedRow, AlignedRow row) {
        return new ToleranceRange(alignedRow.getMz(), ppm).contains(row.getMz());
    }
}
