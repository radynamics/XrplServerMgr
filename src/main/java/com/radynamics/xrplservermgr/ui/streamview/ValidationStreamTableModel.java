package com.radynamics.xrplservermgr.ui.streamview;

import com.radynamics.xrplservermgr.xrpl.KnownValidatorRepo;
import com.radynamics.xrplservermgr.xrpl.subscription.ValidationStreamData;

import javax.swing.table.AbstractTableModel;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ValidationStreamTableModel extends AbstractTableModel {
    private final ArrayList<ValidationStreamData> items = new ArrayList<>();
    private final KnownValidatorRepo knownValidatorRepo;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    public ValidationStreamTableModel(KnownValidatorRepo knownValidatorRepo) {
        this.knownValidatorRepo = knownValidatorRepo;
    }

    @Override
    public int getRowCount() {
        return items.size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return ZonedDateTime.class;
            case 1:
                return String.class;
            case 2:
                return Long.class;
            case 3:
                return String.class;
            case 4:
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
                return "Signing Time";
            case 3:
                return "Validator";
            case 4:
                return "Domain";
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
                return item.signingTime();
            case 3:
                return item.masterKey() == null ? item.validationPublicKey() : item.masterKey();
            case 4:
                var knownValidator = knownValidatorRepo.get(item.masterKey()).orElse(null);
                return knownValidator == null ? null : knownValidator.domain();
        }
        return null;
    }

    public void add(ValidationStreamData data) {
        items.add(data);
        fireTableDataChanged();
    }
}
