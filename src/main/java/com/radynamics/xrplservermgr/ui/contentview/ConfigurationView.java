package com.radynamics.xrplservermgr.ui.contentview;

import com.radynamics.xrplservermgr.config.Configuration;
import com.radynamics.xrplservermgr.sshapi.SshApiException;
import com.radynamics.xrplservermgr.ui.Utils;
import com.radynamics.xrplservermgr.xrpl.parser.ConfigCfg;
import com.radynamics.xrplservermgr.xrpl.parser.ValidatorsTxt;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ConfigurationView extends ContentView {
    private final String host;
    private final JButton cmdEdit;
    private final JButton cmdBackupCfg;
    private final JButton cmdBackupWalletDb;
    private final JButton cmdEditValidators;

    public ConfigurationView(JFrame parent, String host) {
        super(parent);
        this.host = host;

        cmdEdit = appendButton("edit .cfg file", this::editConfig);
        cmdBackupCfg = appendButton("backup .cfg file", this::localSaveConfigCfg);
        cmdBackupWalletDb = appendButton("backup wallet.db", this::localSaveWalletDb);
        cmdEditValidators = appendButton("edit validators.txt", this::editValidators);
    }

    private JButton appendButton(String caption, Runnable r) {
        var cmd = new JButton(caption);
        add(cmd);
        cmd.addActionListener(e -> r.run());
        return cmd;
    }

    private void editConfig() {
        Utils.runAsync(() -> {
            xrplBinaryRefresh();
            try {
                if (xrplBinary.config() == null) {
                    JOptionPane.showMessageDialog(this, "No configuration available.");
                    return;
                }

                final ConfigCfg previousState = xrplBinary.config();

                var txt = new JTextArea(xrplBinary.config().raw());
                txt.setColumns(100);
                txt.setRows(40);
                txt.setSize(txt.getPreferredSize().width, txt.getPreferredSize().height);
                var res = JOptionPane.showConfirmDialog(this, new JScrollPane(txt), "Edit config", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (res != JOptionPane.OK_OPTION) {
                    return;
                }

                final ConfigCfg newState = ConfigCfg.parse(txt.getText());
                if (previousState.sameAs(newState)) {
                    return;
                }

                previousState.saveAs(Configuration.createNewRippleCfgBackupFile(host).getAbsolutePath());
                xrplBinary.config(newState);

                outputInfo("Configuration file has been updated. The service must be restarted for the changes to take effect.");
            } catch (SshApiException | IOException e) {
                outputError(e.getMessage());
            }
        });
    }

    private void localSaveConfigCfg() {
        var fc = new JFileChooser();
        fc.setSelectedFile(new File(xrplBinary.configFileName()));
        int option = fc.showSaveDialog(this);
        if (option != JFileChooser.APPROVE_OPTION) {
            return;
        }

        var outputPath = fc.getSelectedFile().getAbsolutePath();

        Utils.runAsync(() -> {
            xrplBinaryRefresh();
            try {
                if (xrplBinary.config() == null) {
                    JOptionPane.showMessageDialog(this, "No configuration available.");
                    return;
                }
                xrplBinary.config().saveAs(outputPath);

                JOptionPane.showMessageDialog(this, "Exported to %s".formatted(outputPath), "", JOptionPane.INFORMATION_MESSAGE);
            } catch (SshApiException | IOException e) {
                outputError(e.getMessage());
            }
        });
    }

    private void localSaveWalletDb() {
        var fc = new JFileChooser();
        fc.setSelectedFile(new File(xrplBinary.walletDbFileName()));
        int option = fc.showSaveDialog(this);
        if (option != JFileChooser.APPROVE_OPTION) {
            return;
        }

        var outputPath = fc.getSelectedFile().getAbsolutePath();

        Utils.runAsync(() -> {
            xrplBinaryRefresh();
            try {
                var walletDb = xrplBinary.walletDb();
                if (walletDb == null) {
                    JOptionPane.showMessageDialog(this, "No %s available.".formatted(xrplBinary.walletDbFileName()));
                    return;
                }
                try (var fos = new FileOutputStream(outputPath)) {
                    walletDb.writeTo(fos);
                }

                JOptionPane.showMessageDialog(this, "Exported to %s".formatted(outputPath), "", JOptionPane.INFORMATION_MESSAGE);
            } catch (SshApiException | IOException e) {
                outputError(e.getMessage());
            }
        });
    }

    private void editValidators() {
        Utils.runAsync(() -> {
            xrplBinaryRefresh();
            ValidatorsTxt newState;
            try {
                if (xrplBinary.validatorsTxt() == null) {
                    JOptionPane.showMessageDialog(this, "No validators.txt file available.");
                    return;
                }

                final var previousState = xrplBinary.validatorsTxt();

                var txt = new JTextArea(xrplBinary.validatorsTxt().raw());
                txt.setColumns(100);
                txt.setRows(40);
                txt.setSize(txt.getPreferredSize().width, txt.getPreferredSize().height);
                var res = JOptionPane.showConfirmDialog(this, new JScrollPane(txt), "Edit validators.txt", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (res != JOptionPane.OK_OPTION) {
                    return;
                }

                newState = ValidatorsTxt.parse(txt.getText());
                if (previousState.sameAs(newState)) {
                    return;
                }

                previousState.saveAs(Configuration.createNewValidatorsTxtBackupFile(host).getAbsolutePath());

                putValidatorsTxt(newState);
            } catch (SshApiException | IOException e) {
                outputError(e.getMessage());
            }
        });
    }

    private void putValidatorsTxt(ValidatorsTxt validatorsTxt) throws SshApiException {
        try {
            xrplBinary.validatorsTxt(validatorsTxt);
            outputInfo("validators.txt file has been updated. The service must be restarted for the changes to take effect.");
        } catch (SshApiException e) {
            outputError(e.getMessage());
            throw e;
        }
    }

    @Override
    public String tabText() {
        return "Configuration";
    }

    @Override
    public void close() {
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        cmdEdit.setEnabled(enabled);
        cmdBackupCfg.setEnabled(enabled);
        cmdBackupWalletDb.setEnabled(enabled);
        cmdEditValidators.setEnabled(enabled);
    }
}
