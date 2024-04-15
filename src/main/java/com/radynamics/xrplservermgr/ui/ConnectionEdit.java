package com.radynamics.xrplservermgr.ui;

import com.radynamics.xrplservermgr.sshapi.ConnectionInfo;
import com.radynamics.xrplservermgr.utils.RequestFocusListener;

import javax.swing.*;
import java.awt.*;

public class ConnectionEdit {
    private final JPanel pnl = new JPanel();
    private final JTextField txtName = new JTextField();
    private final JTextField txtHost = new JTextField();
    private final JTextField txtPort = new JTextField();
    private final JTextField txtUsername = new JTextField();
    private final JPasswordField txtPassword = new JPasswordField();
    private final JLabel lblPasswordKnown = new JLabel("Password known. Enter a new one or leave empty to keep current.");

    public ConnectionEdit() {
        pnl.setLayout(new GridBagLayout());
        pnl.setPreferredSize(new Dimension(450, 150));
        var y = 0;
        pnl.add(new JLabel("Connection Name:"), createGridConstraints(0.3, 1, 0, y));
        pnl.add(txtName, createGridConstraints(0.7, 1, 1, y++));
        pnl.add(new JLabel("Host:"), createGridConstraints(0.3, 1, 0, y));
        pnl.add(txtHost, createGridConstraints(0.7, 1, 1, y++));
        pnl.add(new JLabel("Port: "), createGridConstraints(0.3, 1, 0, y));
        pnl.add(txtPort, createGridConstraints(0.7, 1, 1, y++));
        pnl.add(new JLabel("Username:"), createGridConstraints(0.3, 1, 0, y));
        pnl.add(txtUsername, createGridConstraints(0.7, 1, 1, y++));
        pnl.add(new JLabel("Password:"), createGridConstraints(0.3, 1, 0, y));
        {
            var pwrPanel = new JPanel();
            pwrPanel.setLayout(new BorderLayout());
            pwrPanel.add(txtPassword, BorderLayout.NORTH);
            pwrPanel.add(lblPasswordKnown, BorderLayout.SOUTH);
            pnl.add(pwrPanel, createGridConstraints(0.7, 1, 1, y++));
        }
    }

    public ConnectionInfo show(Component parent, ConnectionInfo c) {
        txtName.setText(c.name());
        txtName.addAncestorListener(new RequestFocusListener());
        txtHost.setText(c.host());
        txtPort.setText(c.port().toString());
        txtUsername.setText(c.username());
        lblPasswordKnown.setVisible(c.password().length > 0);

        int result = JOptionPane.showConfirmDialog(parent, pnl, "Connection", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        var port = toIntegerOrNull(txtPort.getText());
        var password = txtPassword.getPassword().length > 0 ? txtPassword.getPassword() : c.password();
        return new ConnectionInfo(txtName.getText(), txtHost.getText(), port == null ? ConnectionInfo.defaultPort : port, txtUsername.getText(), password);
    }

    private static Integer toIntegerOrNull(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return null;
        }
    }

    private static GridBagConstraints createGridConstraints(double weightx, double weighty, int x, int y) {
        var c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = weightx;
        c.weighty = weighty;
        c.gridx = x;
        c.gridy = y;
        return c;
    }
}
