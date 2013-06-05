package uk.ac.ebi.masscascade.bless.table.renderer;

import uk.ac.ebi.masscascade.bless.table.BlessTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class CompoundRenderer extends DefaultTableCellRenderer {

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {

        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (column == BlessTableModel.COMPOUNDS_COLUMN) {
            System.out.println(value.getClass().getName());
            label.setText("");
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setVerticalAlignment(JLabel.CENTER);
        }

        return label;
    }
}
