package com.radynamics.xrplservermgr.ui;

import com.radynamics.xrplservermgr.sshapi.ConnectionInfo;
import com.radynamics.xrplservermgr.sshapi.PasswordRequest;
import com.radynamics.xrplservermgr.sshapi.SecretStorePasswordRequest;
import com.radynamics.xrplservermgr.utils.RequestFocusListener;
import com.radynamics.xrplservermgr.utils.SecretStore;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Optional;

public class ConnectionEdit {
    private final SecretStore secretStore;
    private final ConnectionInfo conn;

    private final JPanel pnl = new JPanel();
    private final JTextField txtName = new JTextField();
    private final JTextField txtHost = new JTextField();
    private final JTextField txtPort = new JTextField();
    private final JTextField txtUsername = new JTextField();
    private final FilePathField txtPrivateKeyFile = new FilePathField();
    private final JButton cmdPwrClear;

    public ConnectionEdit(SecretStore secretStore, ConnectionInfo conn) {
        this.secretStore = secretStore;
        this.conn = conn;

        pnl.setLayout(new GridBagLayout());
        pnl.setPreferredSize(new Dimension(450, 170));
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
            final var gap = 5;
            pwrPanel.setBorder(new EmptyBorder(0, gap * -1, 0, 0));
            pwrPanel.setLayout(new FlowLayout(FlowLayout.LEFT, gap, 0));
            var cmdPwrStore = new JButton("Store in Vault...");
            pwrPanel.add(cmdPwrStore);
            cmdPwrStore.addActionListener(this::onStorePwrClick);
            cmdPwrClear = new JButton("Clear");
            cmdPwrClear.addActionListener(this::onPwrClearClick);
            pwrPanel.add(cmdPwrClear);

            var c = createGridConstraints(0.7, 1, 1, y++);
            c.insets = new Insets(10, 0, 10, 0);
            pnl.add(pwrPanel, c);
        }
        pnl.add(new JLabel("Private key file:"), createGridConstraints(0.3, 1, 0, y));
        pnl.add(txtPrivateKeyFile, createGridConstraints(0.7, 1, 1, y++));

        txtName.setText(conn.name());
        txtName.addAncestorListener(new RequestFocusListener());
        txtHost.setText(conn.host());
        txtPort.setText(conn.port().toString());
        txtUsername.setText(conn.username());
        cmdPwrClear.setEnabled(secretStore.connectionPassword(conn.server().uuid()) != null);
        txtPrivateKeyFile.setText(conn.privateKeyFilePath());
    }

    private void onStorePwrClick(ActionEvent actionEvent) {
        if (!txtPrivateKeyFile.getText().isEmpty()) {
            JOptionPane.showMessageDialog(pnl, "No password needs to be saved when using a key file. The password is requested when required.", MainForm.appTitle, JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        var pwr = PasswordInput.connectionPassword(pnl, txtUsername.getText());
        if (pwr == null || pwr.length == 0) {
            return;
        }

        secretStore.connectionPassword(conn.server().uuid(), pwr);
        cmdPwrClear.setEnabled(true);
    }

    private void onPwrClearClick(ActionEvent actionEvent) {
        secretStore.connectionPasswordDelete(conn.server().uuid());
        cmdPwrClear.setEnabled(false);
    }

    public boolean show(Component parent) {
        int result = JOptionPane.showConfirmDialog(parent, pnl, "Connection", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return false;
        }

        conn.server().displayText(txtName.getText());
        conn.server().host(txtHost.getText());
        conn.server().port(toInteger(txtPort.getText()).orElse(ConnectionInfo.defaultPort));
        conn.server().username(txtUsername.getText());
        if (txtPrivateKeyFile.getText().isEmpty()) {
            conn.server().keyFile(null);
            conn.passwordRequest(new PasswordRequest(() -> new SecretStorePasswordRequest(parent, secretStore, conn.server()).password()));
        } else {
            conn.server().keyFile(txtPrivateKeyFile.getText());
            conn.passwordRequest(new PasswordRequest(() -> PasswordInput.privateKeyFile(parent)));
        }
        return true;
    }

    private static Optional<Integer> toInteger(String value) {
        try {
            return Optional.of(Integer.parseInt(value));
        } catch (Exception e) {
            return Optional.empty();
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
