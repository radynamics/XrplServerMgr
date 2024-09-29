package com.radynamics.xrplservermgr.ui.streamview;

import com.radynamics.xrplservermgr.utils.Utils;
import com.radynamics.xrplservermgr.xrpl.subscription.PeerStatusStreamData;

import javax.swing.table.AbstractTableModel;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class PeerStatusStreamTableModel extends AbstractTableModel {
    private final ArrayList<PeerStatusStreamData> items = new ArrayList<>();

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    @Override
    public int getRowCount() {
        return items.size();
    }

    @Override
    public int getColumnCount() {
        return 7;
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
            case 4:
                return String.class;
            case 5:
                return String.class;
            case 6:
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
                return "Action";
            case 3:
                return "Date";
            case 4:
                return "Ledger Index min";
            case 5:
                return "Ledger Index max";
            case 6:
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
                return item.action();
            case 3:
                return Utils.fromRippleTime(item.date()).format(formatter);
            case 4:
                return item.ledgerIndexMin();
            case 5:
                return item.ledgerIndexMax();
            case 6:
                return item.ledgerHash();
        }
        return null;
    }

    public void add(PeerStatusStreamData data) {
        items.add(data);
        fireTableDataChanged();
    }
}
