package com.radynamics.xrplservermgr.ui.streamview;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class AlignedCellRenderer extends DefaultTableCellRenderer {
    public AlignedCellRenderer(int alignment) {
        setHorizontalAlignment(alignment);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        var cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        cell.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        cell.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
        return cell;
    }
}
