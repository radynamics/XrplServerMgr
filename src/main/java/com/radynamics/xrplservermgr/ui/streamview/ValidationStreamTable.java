package com.radynamics.xrplservermgr.ui.streamview;

import com.radynamics.xrplservermgr.xrpl.KnownValidatorRepo;
import com.radynamics.xrplservermgr.xrpl.subscription.ValidationStreamData;
import com.radynamics.xrplservermgr.xrpl.subscription.ValidationStreamListener;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

public class ValidationStreamTable implements Presentation, ValidationStreamListener {
    private final JTable table;
    private final ValidationStreamTableModel model;

    public ValidationStreamTable(KnownValidatorRepo knownValidatorRepo) {
        model = new ValidationStreamTableModel(knownValidatorRepo);
        table = new JTable(model);

        table.setAutoCreateColumnsFromModel(false);
        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        var rightRenderer = new AlignedCellRenderer(JLabel.RIGHT);

        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setHeaderRenderer(rightRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setHeaderRenderer(rightRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(3).setPreferredWidth(400);
        table.getColumnModel().getColumn(4).setPreferredWidth(200);
    }

    @Override
    public void onReceive(ValidationStreamData data) {
        model.add(data);
        table.scrollRectToVisible(table.getCellRect(table.getRowCount(), 0, true));
    }

    @Override
    public JComponent view() {
        return table;
    }
}
