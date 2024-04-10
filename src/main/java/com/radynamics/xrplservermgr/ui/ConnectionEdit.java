package com.radynamics.xrplservermgr.ui;

import com.radynamics.xrplservermgr.sshapi.ConnectionInfo;
import com.radynamics.xrplservermgr.sshapi.SshSession;
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

    public ConnectionEdit() {
        pnl.setLayout(new GridBagLayout());
        pnl.setPreferredSize(new Dimension(350, 130));
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
        pnl.add(txtPassword, createGridConstraints(0.7, 1, 1, y++));
    }

    public ConnectionInfo show(Component parent, ConnectionInfo c) {
        txtName.setText(c.name());
        txtName.addAncestorListener(new RequestFocusListener());
        txtHost.setText(c.host());
        txtPort.setText(c.port().toString());
        txtUsername.setText(c.username());
        txtPassword.setText(c.password());

        int result = JOptionPane.showConfirmDialog(parent, pnl, "Connection", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        var conn = createConnection();
        if (!canConnect(conn)) {
            JOptionPane.showMessageDialog(parent, "Could not connect to %s".formatted(conn.host()), "Error", JOptionPane.ERROR_MESSAGE);
            return show(parent, conn);
        }
        return conn;
    }

    private boolean canConnect(ConnectionInfo conn) {
        try (var s = new SshSession(() -> null, conn)) {
            s.open();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private ConnectionInfo createConnection() {
        var port = toIntegerOrNull(txtPort.getText());

        return new ConnectionInfo(txtName.getText(), txtHost.getText(), port == null ? ConnectionInfo.defaultPort : port,
                txtUsername.getText(), txtPassword.getText());
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
