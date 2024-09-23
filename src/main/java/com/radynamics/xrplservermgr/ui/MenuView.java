package com.radynamics.xrplservermgr.ui;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.radynamics.xrplservermgr.ui.contentview.MenuItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.HashMap;

public class MenuView extends JPanel {
    private final ArrayList<MenuItemListener> listener = new ArrayList<>();
    private final JLabel lblServerStatus;
    private final JLabel lblAmendments;
    private final JLabel lblConfiguration;
    private final JLabel lblLogs;
    private final JLabel lblPeers;
    private final JLabel lblStreams;
    private final JLabel lblComnandLine;

    public MenuView() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        lblServerStatus = createNavLabel("Server Status");
        add(createRow(lblServerStatus, "start.svg", MenuItem.ServerStatus));
        lblAmendments = createNavLabel("Amendments");
        add(createRow(lblAmendments, "poll.svg", MenuItem.Amendments));
        lblConfiguration = createNavLabel("Configuration");
        add(createRow(lblConfiguration, "gear.svg", MenuItem.Configuration));
        lblLogs = createNavLabel("Logs");
        add(createRow(lblLogs, "file.svg", MenuItem.Logs));
        lblPeers = createNavLabel("Peers");
        add(createRow(lblPeers, "globe.svg", MenuItem.Peers));
        lblStreams = createNavLabel("Streams");
        add(createRow(lblStreams, "stream.svg", MenuItem.Streams));
        lblComnandLine = createNavLabel("Console");
        add(createRow(lblComnandLine, "console.svg", MenuItem.CommandLine));
    }

    private JPanel createRow(JLabel lbl, String iconName, MenuItem item) {
        var l = new SpringLayout();
        var pnl = new JPanel();
        pnl.setLayout(l);
        pnl.setPreferredSize(new Dimension(200, 18));

        pnl.add(lbl);
        var icon = new FlatSVGIcon("img/%s".formatted(iconName), 16, 16);
        icon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> UIManager.getColor("Label.disabledForeground")));
        lbl.setIcon(icon);
        lbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (lbl.isEnabled()) {
                    raiseOpen(item);
                }
            }
        });
        l.putConstraint(SpringLayout.WEST, lbl, 5, SpringLayout.WEST, pnl);
        l.putConstraint(SpringLayout.NORTH, lbl, 0, SpringLayout.NORTH, pnl);
        return pnl;
    }

    private static JLabel createNavLabel(String text) {
        var lbl = new JLabel(text);
        final var regular = lbl.getFont();
        var attributes = new HashMap<TextAttribute, Object>(regular.getAttributes());
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        final var underscored = regular.deriveFont(attributes);

        lbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (lbl.isEnabled()) {
                    lbl.setFont(underscored);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                lbl.setFont(regular);
            }
        });
        return lbl;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        lblServerStatus.setEnabled(enabled);
        lblAmendments.setEnabled(enabled);
        lblConfiguration.setEnabled(enabled);
        lblLogs.setEnabled(enabled);
        lblPeers.setEnabled(enabled);
        lblStreams.setEnabled(enabled);
        lblComnandLine.setEnabled(enabled);
    }

    public void addMenuListener(MenuItemListener l) {
        listener.add(l);
    }

    private void raiseOpen(MenuItem item) {
        for (var l : listener) {
            l.onOpen(item);
        }
    }
}
