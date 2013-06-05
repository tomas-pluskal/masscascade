package uk.ac.ebi.masscascade.bless.table.renderer;

import uk.ac.ebi.masscascade.bless.table.BlessTableModel;
import uk.ac.ebi.masscascade.commons.Status;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class StatusRenderer extends DefaultTableCellRenderer {

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {

        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (column == BlessTableModel.STATUS_COLUMN) {
            Status status = (Status) value;
            Icon icon = IconUtil.getStatusIcon(status);
            label.setIcon(icon);
            label.setText("");
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setVerticalAlignment(JLabel.CENTER);
        }

        return label;
    }
}
