package com.radynamics.xrplservermgr.ui.contentview;

import com.radynamics.xrplservermgr.sshapi.*;
import com.radynamics.xrplservermgr.ui.ChownMessageBox;
import com.radynamics.xrplservermgr.ui.MessageBox;
import com.radynamics.xrplservermgr.ui.Utils;
import com.radynamics.xrplservermgr.xrpl.*;
import com.radynamics.xrplservermgr.xrpl.rippled.Rippled;
import com.radynamics.xrplservermgr.xrpl.xahaud.Xahaud;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ServerStatus extends ContentView implements ActionLogListener, XrplInstallerListener {
    private boolean xrplInstalled;
    private final ArrayList<ServerStatusListener> listener = new ArrayList<>();
    private final JButton cmdInstall;
    private final JButton cmdUpdate;
    private final JButton cmdRestart;
    private final JButton cmdDeleteServiceDb;
    private final JButton cmdDeleteDebugLog;

    public ServerStatus(JFrame parent) {
        super(parent);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        {
            var pnl = new JPanel();
            add(pnl);
            cmdInstall = appendButton(pnl, "install xrpl binary", this::installXrplBinary);
            cmdUpdate = appendButton(pnl, "update xrpl binary", this::updateXrplBinary);
            appendButton(pnl, "stop service", this::stopService);
            appendButton(pnl, "start service", this::startService);
            cmdRestart = appendButton(pnl, "restart service", this::restartService);
            cmdDeleteServiceDb = appendButton(pnl, "delete service databases", this::deleteServiceDb);
            cmdDeleteDebugLog = appendButton(pnl, "delete debug.log", this::deleteDebugLog);
        }
    }

    private JButton appendButton(JPanel pnl, String caption, Runnable r) {
        var cmd = new JButton(caption);
        pnl.add(cmd);
        cmd.addActionListener(e -> r.run());
        return cmd;
    }

    private void installXrplBinary() {
        Utils.runAsync(() -> {
            if (!MessageBox.showBetaFeatureWarning(this)) return;

            var pnl = new JPanel();
            pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
            pnl.add(new JLabel("Which XRPL service would you like to install?"));
            var lblInstaller = new JLabel();
            pnl.add(lblInstaller);

            var cbo = new JComboBox<XrplBinary>();
            pnl.add(cbo);
            cbo.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    return super.getListCellRendererComponent(list, ((XrplBinary) value).displayName(), index, isSelected, cellHasFocus);
                }
            });
            cbo.addItem(new Rippled(session, systemMonitor, new ChownMessageBox(this)));
            cbo.addItem(new Xahaud(session, systemMonitor, new ChownMessageBox(this)));
            cbo.setAlignmentX(Component.LEFT_ALIGNMENT);

            int result = JOptionPane.showConfirmDialog(parent, pnl, "Install service", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            var installer = createInstaller((XrplBinary) cbo.getSelectedItem());
            lblInstaller.setText("Install using %s".formatted(installer.displayName()));
            try {
                installer.install();

                xrplService.restart(false);
                outputInfo("xrpl binary successfully installed");
            } catch (SshApiException e) {
                outputError("Installing xrpl binary failed. " + e.getMessage());
            } finally {
                installer.removeActionLogListener(this);
            }

            raiseCloseAndReopenSession();
        });
    }

    private XrplInstaller createInstaller(XrplBinary binary) {
        var installer = binary.createInstaller();
        installer.addActionLogListener(this);
        installer.addInstallerListener(this);
        return installer;
    }

    private void updateXrplBinary() {
        Utils.runAsync(() -> {
            if (!MessageBox.showBetaFeatureWarning(this)) return;

            var pnl = new JPanel();
            pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
            var installer = createInstaller(xrplBinary);
            pnl.add(new JLabel("Install using %s. Select Channel/Version:".formatted(installer.displayName())));

            var cboChannels = new JComboBox<ReleaseChannel>();
            pnl.add(cboChannels);
            cboChannels.setAlignmentX(Component.LEFT_ALIGNMENT);
            cboChannels.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    var channel = ((ReleaseChannel) value);
                    var text = channel.stable() ? "%s (recommended)".formatted(channel.name()) : channel.name();
                    return super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
                }
            });
            var cboPackages = new JComboBox<XrplBinaryPackage>();
            for (var c : xrplBinary.updateChannels()) {
                cboChannels.addItem(c);
                if (c.stable()) {
                    cboChannels.setSelectedItem(c);
                    updateAvailableUpdatePackages(cboChannels, cboPackages, c);
                }
            }

            cboChannels.addItemListener(e -> {
                if (e.getItem().equals(cboChannels.getSelectedItem())) {
                    return;
                }
                updateAvailableUpdatePackages(cboChannels, cboPackages, (ReleaseChannel) cboChannels.getSelectedItem());
            });

            pnl.add(cboPackages);
            cboPackages.setAlignmentX(Component.LEFT_ALIGNMENT);
            cboPackages.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    var text = value == null ? "" : ((XrplBinaryPackage) value).versionText();
                    return super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
                }
            });

            int result = JOptionPane.showConfirmDialog(parent, pnl, "Choose version to install", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            try {
                xrplService.stop();
                installer.install((XrplBinaryPackage) cboPackages.getSelectedItem());
                xrplService.restart();
                outputInfo("xrpl binary successfully updated");
            } catch (SshApiException e) {
                outputError("Updating xrpl binary failed. " + e.getMessage());
            }
            statusView.refresh();
        });
    }

    private void updateAvailableUpdatePackages(JComboBox cboChannels, JComboBox<XrplBinaryPackage> cboPackages, ReleaseChannel channel) {
        try {
            cboChannels.setEnabled(false);
            cboPackages.removeAllItems();
            var all = xrplBinary.createInstaller().availableUpdates(channel);
            for (var pkg : all) {
                cboPackages.addItem(pkg);
            }
        } catch (Exception ex) {
            outputError("Getting available packages failed. " + ex.getMessage());
        } finally {
            cboChannels.setEnabled(true);
        }
    }

    private void restartService() {
        Utils.runAsync(() -> {
            try {
                xrplService.restart(true);
                statusView.refresh();
            } catch (SshApiException e) {
                outputError(e.getMessage());
            }
        });
    }

    private void stopService() {
        Utils.runAsync(() -> {
            try {
                xrplService.stop();
                statusView.refresh();
            } catch (SshApiException e) {
                outputError(e.getMessage());
            }
        });
    }

    private void startService() {
        Utils.runAsync(() -> {
            try {
                xrplService.start();
                statusView.refresh();
            } catch (SshApiException e) {
                outputError(e.getMessage());
            }
        });
    }

    private void deleteServiceDb() {
        int result = JOptionPane.showConfirmDialog(parent, "Do you really want to stop the service and remove its database? This step cannot be undone.\n\nDo you want to proceed?", "Remove database?", JOptionPane.YES_NO_CANCEL_OPTION);
        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            xrplService.stop();
            xrplBinary.deleteDatabase();
            xrplService.start();
        } catch (SshApiException e) {
            outputError(e.getMessage());
        }
        statusView.refresh();
    }

    private void deleteDebugLog() {
        int result = JOptionPane.showConfirmDialog(parent, "Do you really want to remove the xrpl service log file? This step cannot be undone.\n\nDo you want to proceed?", "Remove log?", JOptionPane.YES_NO_CANCEL_OPTION);
        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            xrplBinary.deleteDebugLog();
        } catch (SshApiException e) {
            outputError(e.getMessage());
        }
    }

    @Override
    public String tabText() {
        return "Server Status";
    }

    @Override
    public void close() {

    }

    @Override
    public void initSession(SshSession session, SystemMonitor systemMonitor, XrplBinary xrplBinary) {
        super.initSession(session, systemMonitor, xrplBinary);

        try {
            xrplInstalled = xrplBinary.installed();
            cmdInstall.setEnabled(!xrplInstalled);
            cmdUpdate.setEnabled(xrplInstalled);
            cmdRestart.setEnabled(xrplInstalled);
            cmdDeleteServiceDb.setEnabled(xrplInstalled);
            cmdDeleteDebugLog.setEnabled(xrplInstalled);
        } catch (SshApiException e) {
            outputError(e.getMessage());
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        cmdInstall.setEnabled(enabled && !xrplInstalled);
        cmdUpdate.setEnabled(enabled && xrplInstalled);
        cmdRestart.setEnabled(enabled && xrplInstalled);
        cmdDeleteServiceDb.setEnabled(enabled && xrplInstalled);
        cmdDeleteDebugLog.setEnabled(enabled && xrplInstalled);
    }

    @Override
    public void onEvent(ActionLogEvent event) {
        appendOutput(event.message(), event.level());
    }

    @Override
    public void onFingerprintMismatch(FingerprintMismatchEvent event) {
        var sb = new StringBuilder();
        sb.append("The fingerprint of the downloaded binary is unexpected. This could be a malicious binary or an outdated expectation in this software. Please verify fingerprint manually and continue only if you consider its signer trustworthy.");
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("Expected: %s".formatted(event.expectedFingerprint()));
        sb.append(System.lineSeparator());
        sb.append("Actual: %s".formatted(event.actualFingerprint() == null ? "<none>" : event.actualFingerprint()));
        var ret = JOptionPane.showConfirmDialog(parent, sb, "Invalid / unknown signature", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        event.accept(ret == JOptionPane.YES_OPTION);
    }

    public void addServerStatusListener(ServerStatusListener l) {
        listener.add(l);
    }

    private void raiseCloseAndReopenSession() {
        for (var l : listener) {
            l.onCloseAndReopenSession();
        }
    }
}
