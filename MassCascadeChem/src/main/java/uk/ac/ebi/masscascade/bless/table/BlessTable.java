package uk.ac.ebi.masscascade.bless.table;

import uk.ac.ebi.masscascade.bless.table.editor.EachRowBoxEditor;
import uk.ac.ebi.masscascade.bless.table.renderer.CompoundRenderer;
import uk.ac.ebi.masscascade.bless.table.renderer.DeleteRenderer;
import uk.ac.ebi.masscascade.bless.table.renderer.MsRenderer;
import uk.ac.ebi.masscascade.bless.table.renderer.MsnRenderer;
import uk.ac.ebi.masscascade.bless.table.renderer.StatusRenderer;
import uk.ac.ebi.masscascade.commons.compound.CompoundSpectrum;
import uk.ac.ebi.masscascade.interfaces.Spectrum;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class BlessTable extends JTable {

    public BlessTable(List<CompoundSpectrum> spectra) {

        super(new BlessTableModel(spectra));

        UIManager.put("ComboBox.background", new ColorUIResource(UIManager.getColor("TextField.background")));
        UIManager.put("ComboBox.foreground", new ColorUIResource(UIManager.getColor("TextField.foreground")));
        UIManager.put("ComboBox.selectionBackground", new ColorUIResource(Color.RED));
        UIManager.put("ComboBox.selectionForeground", new ColorUIResource(Color.WHITE));

        setRowMargin(4);
        setRowHeight(120);
        setCellSelectionEnabled(true);
        setAutoResizeMode(AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setPreferredScrollableViewportSize(getPreferredSize());

        addMouseListener(new MouseAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseReleased(e);
                int row = rowAtPoint(e.getPoint());
                int col = columnAtPoint(e.getPoint());

                if (col == BlessTableModel.DELETE_COLUMN)
                    ((BlessTableModel) getModel()).removeRow(row);
            }
        });

        getColumnModel().getColumn(BlessTableModel.MS_COLUMN).setCellRenderer(new MsRenderer());
        getColumnModel().getColumn(BlessTableModel.MS_COLUMN).setPreferredWidth(120);
        getColumnModel().getColumn(BlessTableModel.MS_COLUMN).setMinWidth(120);

        getColumnModel().getColumn(BlessTableModel.COMPOUNDS_COLUMN).setCellEditor(new EachRowBoxEditor());
//        getColumnModel().getColumn(BlessTableModel.COMPOUNDS_COLUMN).setCellRenderer(new CompoundRenderer());

        getColumnModel().getColumn(BlessTableModel.MSN_COLUMN).setCellRenderer(new MsRenderer());
        getColumnModel().getColumn(BlessTableModel.MSN_COLUMN).setPreferredWidth(120);
        getColumnModel().getColumn(BlessTableModel.MSN_COLUMN).setMinWidth(120);

        getColumnModel().getColumn(BlessTableModel.STATUS_COLUMN).setCellRenderer(new StatusRenderer());
        getColumnModel().getColumn(BlessTableModel.STATUS_COLUMN).setPreferredWidth(40);
        getColumnModel().getColumn(BlessTableModel.STATUS_COLUMN).setMinWidth(40);

        getColumnModel().getColumn(BlessTableModel.DELETE_COLUMN).setCellRenderer(new DeleteRenderer());
        getColumnModel().getColumn(BlessTableModel.DELETE_COLUMN).setPreferredWidth(40);
        getColumnModel().getColumn(BlessTableModel.DELETE_COLUMN).setMinWidth(40);
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return (col == BlessTableModel.COMPOUNDS_COLUMN);
    }
}
