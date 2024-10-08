package com.radynamics.xrplservermgr.ui.streamview;

import com.radynamics.xrplservermgr.xrpl.subscription.LedgerStreamData;

import javax.swing.table.AbstractTableModel;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class LedgerStreamTableModel extends AbstractTableModel {
    private final ArrayList<LedgerStreamData> items = new ArrayList<>();

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    @Override
    public int getRowCount() {
        return items.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return ZonedDateTime.class;
            case 1:
                return String.class;
            case 2:
                return String.class;
            case 3:
                return String.class;
        }
        return Object.class;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Local Time";
            case 1:
                return "Ledger Index";
            case 2:
                return "TX count";
            case 3:
                return "Hash";
        }
        return null;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        var item = items.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return item.dateTime().format(formatter);
            case 1:
                return item.ledgerIndex();
            case 2:
                return item.txnCount();
            case 3:
                return item.ledgerHash();
        }
        return null;
    }

    public void add(LedgerStreamData data) {
        items.add(data);
        fireTableDataChanged();
    }
}
