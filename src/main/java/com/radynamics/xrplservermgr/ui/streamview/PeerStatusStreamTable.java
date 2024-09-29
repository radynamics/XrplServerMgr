package com.radynamics.xrplservermgr.ui.streamview;

import com.radynamics.xrplservermgr.xrpl.subscription.PeerStatusStreamData;
import com.radynamics.xrplservermgr.xrpl.subscription.PeerStatusStreamListener;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

public class PeerStatusStreamTable implements Presentation, PeerStatusStreamListener {
    private final JTable table;
    private final PeerStatusStreamTableModel model;

    public PeerStatusStreamTable() {
        model = new PeerStatusStreamTableModel();
        table = new JTable(model);

        table.setAutoCreateColumnsFromModel(false);
        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        var rightRenderer = new AlignedCellRenderer(JLabel.RIGHT);

        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setHeaderRenderer(rightRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setHeaderRenderer(rightRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(5).setPreferredWidth(110);
        table.getColumnModel().getColumn(5).setHeaderRenderer(rightRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(6).setPreferredWidth(450);
    }

    @Override
    public void onReceive(PeerStatusStreamData data) {
        model.add(data);
        table.scrollRectToVisible(table.getCellRect(table.getRowCount(), 0, true));
    }

    @Override
    public JComponent view() {
        return table;
    }
}
