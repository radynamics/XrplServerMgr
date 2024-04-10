package com.radynamics.xrplservermgr.ui.logview;

import com.radynamics.xrplservermgr.xrpl.parser.debuglog.LogEvent;
import com.radynamics.xrplservermgr.xrpl.parser.debuglog.Severity;

import javax.swing.table.AbstractTableModel;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class LogEventTableModel extends AbstractTableModel {
    private final List<LogEvent> items;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final int COL_MESSAGE = 3;

    public LogEventTableModel() {
        items = new ArrayList<>(25);
    }

    public LogEventTableModel(List<LogEvent> items) {
        this.items = items;
    }

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
                return Severity.class;
            case 1:
                return ZonedDateTime.class;
            case 2:
                return String.class;
            case COL_MESSAGE:
                return String.class;
        }
        return Object.class;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Severity";
            case 1:
                return "Date";
            case 2:
                return "Partition";
            case COL_MESSAGE:
                return "Message";
        }
        return null;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        var item = items.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return item.severity();
            case 1:
                return item.dateTime().format(formatter);
            case 2:
                return item.partition();
            case COL_MESSAGE:
                return item.message();
        }
        return null;
    }

    public LogEvent get(int rowIndex) {
        return items.get(rowIndex);
    }

    public void add(LogEvent item) {
        items.add(item);
        int row = items.indexOf(item);
        fireTableRowsInserted(row, row);
    }

    public void remove(LogEvent item) {
        if (items.contains(item)) {
            int row = items.indexOf(item);
            items.remove(row);
            fireTableRowsDeleted(row, row);
        }
    }

    public void setData(List<LogEvent> events) {
        items.clear();
        items.addAll(events);
        fireTableDataChanged();
    }
}
