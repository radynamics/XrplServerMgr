package com.radynamics.xrplservermgr.ui.logview;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.radynamics.xrplservermgr.xrpl.parser.debuglog.Severity;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class SeverityCellRenderer extends JLabel implements TableCellRenderer {
    private final static int WIDTH = 16;
    private final static int HEIGHT = 16;
    private final static FlatSVGIcon fatal = new FlatSVGIcon("img/fatal.svg", WIDTH, HEIGHT);
    private final static FlatSVGIcon error = new FlatSVGIcon("img/error.svg", WIDTH, HEIGHT);
    private final static FlatSVGIcon warning = new FlatSVGIcon("img/warning.svg", WIDTH, HEIGHT);
    private final static FlatSVGIcon info = new FlatSVGIcon("img/info.svg", WIDTH, HEIGHT);

    public SeverityCellRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        var severity = (Severity) value;
        setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
        setText(severity.name());
        setIcon(getImage(severity));

        return this;
    }

    private static FlatSVGIcon getImage(Severity value) {
        switch (value) {
            case Fatal -> {
                return fatal;
            }
            case Error -> {
                return error;
            }
            case Warning -> {
                return warning;
            }
            case Info, Debug -> {
                return info;
            }
            default -> throw new IllegalStateException("Unexpected value: " + value);
        }
    }
}
