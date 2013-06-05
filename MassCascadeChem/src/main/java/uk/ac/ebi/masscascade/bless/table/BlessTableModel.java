package uk.ac.ebi.masscascade.bless.table;

import uk.ac.ebi.masscascade.commons.compound.CompoundSpectrum;
import uk.ac.ebi.masscascade.commons.Evidence;
import uk.ac.ebi.masscascade.commons.Status;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlessTableModel extends AbstractTableModel {

    public static final int MS_COLUMN = 0;
    public static final int COMPOUNDS_COLUMN = 1;
    public static final int ANNOTATIONS_COLUMN = 2;
    public static final int SCORE_COLUMN = 3;
    public static final int STATUS_COLUMN = 4;
    public static final int MSI_COLUMN = 5;
    public static final int MSN_COLUMN = 6;
    public static final int MSN_ANNOTATIONS_COLUMN = 7;
    public static final int DELETE_COLUMN = 8;

    private String[] header =
            new String[]{"Compound Spectrum", "Compounds", "Annotations", "Score", "Status", "MSI", "MSn",
                         "MSn Annotations", "Remove"};

    private List<CompoundSpectrum> spectra;
    private Map<Integer, Integer> indexToEntityIndex;

    public BlessTableModel() {
        this(new ArrayList<CompoundSpectrum>());
    }

    public BlessTableModel(List<CompoundSpectrum> spectra) {
        this.spectra = spectra;
        indexToEntityIndex = new HashMap<>();
    }

    public CompoundSpectrum getSpectrum(int index) {
        return spectra.get(index);
    }

    public void setData(List<CompoundSpectrum> idToSpectra) {
        this.spectra = idToSpectra;
    }

    /**
     * Returns the number of rows in the model. A
     * <code>JTable</code> uses this method to determine how many rows it
     * should display.  This method should be quick, as it
     * is called frequently during rendering.
     *
     * @return the number of rows in the model
     * @see #getColumnCount
     */
    @Override
    public int getRowCount() {
        return spectra.size();
    }

    /**
     * Returns the number of columns in the model. A
     * <code>JTable</code> uses this method to determine how many columns it
     * should create and display by default.
     *
     * @return the number of columns in the model
     * @see #getRowCount
     */
    @Override
    public int getColumnCount() {
        return header.length;
    }

    /**
     * Returns the name of column x.
     *
     * @param col a column index
     */
    public String getColumnName(int col) {
        return header[col];
    }

    /**
     * Returns the value for the cell at <code>columnIndex</code> and
     * <code>rowIndex</code>.
     *
     * @param rowIndex    the row whose value is to be queried
     * @param columnIndex the column whose value is to be queried
     * @return the value Object at the specified cell
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        int id = indexToEntityIndex.containsKey(rowIndex) ? indexToEntityIndex.get(rowIndex) : 0;

        switch (columnIndex) {
            case MS_COLUMN:
                return spectra.get(rowIndex).getPeakList();
            case COMPOUNDS_COLUMN:
                return id + 1 + ": " + spectra.get(rowIndex).getCompound(id).getName();
            case ANNOTATIONS_COLUMN:
                return "";
            case SCORE_COLUMN:
                return spectra.get(rowIndex).getCompound(id).getScore();
            case STATUS_COLUMN:
                return spectra.get(rowIndex).getCompound(id).getStatus();
            case MSI_COLUMN:
                return spectra.get(rowIndex).getCompound(id).getEvidence();
            case MSN_COLUMN:
                return spectra.get(rowIndex).getPeakList2();
            case MSN_ANNOTATIONS_COLUMN:
                return "";
            case DELETE_COLUMN:
                return true;
        }

        return null;
    }

    @Override
    public Class getColumnClass(int c) {

        switch (c) {
            case MS_COLUMN:
                return List.class;
            case COMPOUNDS_COLUMN:
                return JComboBox.class;
            case ANNOTATIONS_COLUMN:
                return String.class;
            case SCORE_COLUMN:
                return Integer.class;
            case STATUS_COLUMN:
                return Status.class;
            case MSI_COLUMN:
                return Evidence.class;
            case MSN_COLUMN:
                return List.class;
            case MSN_ANNOTATIONS_COLUMN:
                return String.class;
            case DELETE_COLUMN:
                return Boolean.class;
        }

        return null;
    }

    /**
     * Removes the row at <code>row</code> from the model.  Notification
     * of the row being removed will be sent to all the listeners.
     *
     * @param row the row index of the row to be removed
     * @throws ArrayIndexOutOfBoundsException if the row was invalid
     */
    public void removeRow(int row) {

        spectra.remove(row);
        indexToEntityIndex.remove(row);
        fireTableRowsDeleted(row, row);
    }

    public void updateById(int index, int entityIndex) {

        indexToEntityIndex.put(index, entityIndex);
        fireTableRowsUpdated(index, index);
    }
}
