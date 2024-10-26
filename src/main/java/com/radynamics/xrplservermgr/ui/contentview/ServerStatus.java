package com.radynamics.xrplservermgr.ui.contentview;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.radynamics.xrplservermgr.sshapi.*;
import com.radynamics.xrplservermgr.ui.*;
import com.radynamics.xrplservermgr.xrpl.*;
import com.radynamics.xrplservermgr.xrpl.rippled.Rippled;
import com.radynamics.xrplservermgr.xrpl.xahaud.Xahaud;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ServerStatus extends ContentView implements ActionLogListener, XrplInstallerListener {
    private boolean xrplInstalled;
    private final ArrayList<ServerStatusListener> listener = new ArrayList<>();
    private final JButton cmdInstall;
    private final JButton cmdUpdate;
    private final JButton cmdStop;
    private final JButton cmdStart;
    private final JButton cmdRestart;
    private final JButton cmdDeleteServiceDb;
    private final JButton cmdDeleteDebugLog;
    private final JLabel lblIcon = new JLabel();
    private final JLabel lblRunningSince = new JLabel("unknown");
    private final JLabel lblXrplState = new JLabel("unknown");
    private final JPanel pnlServerFeatures = new JPanel();
    private final SpringLayout serverFeaturesSpringLayout = new SpringLayout();
    private final JTextField lblInstallPath = Utils.formatAsLabel(new JTextField());
    private final FilePathLabel lblConfigPath;
    private final FilePathLabel lblLogPath;
    private final JTextField lblDatabasePath = Utils.formatAsLabel(new JTextField());
    private final FilePathLabel lblValidators;

    public ServerStatus(JFrame parent, ConnectionInfo conn) {
        super(parent);

        var mainLayout = new SpringLayout();
        setLayout(mainLayout);
        final var iconOffsetEast = 20;
        {
            var pnlConnection = new JPanel();
            {
                add(pnlConnection);
                mainLayout.putConstraint(SpringLayout.NORTH, pnlConnection, 0, SpringLayout.NORTH, this);
                pnlConnection.setPreferredSize(new Dimension(500, 150));
                var sl = new SpringLayout();
                pnlConnection.setLayout(sl);
                {
                    pnlConnection.add(lblIcon);
                    sl.putConstraint(SpringLayout.WEST, lblIcon, 0, SpringLayout.WEST, pnlConnection);
                    sl.putConstraint(SpringLayout.NORTH, lblIcon, 0, SpringLayout.NORTH, pnlConnection);

                    var pnlConnectionName = new JPanel();
                    {
                        pnlConnection.add(pnlConnectionName);
                        sl.putConstraint(SpringLayout.WEST, pnlConnectionName, iconOffsetEast, SpringLayout.EAST, lblIcon);
                        sl.putConstraint(SpringLayout.NORTH, pnlConnectionName, 0, SpringLayout.NORTH, pnlConnection);
                        pnlConnectionName.setLayout(new BoxLayout(pnlConnectionName, BoxLayout.Y_AXIS));
                        {
                            pnlConnectionName.add(new JLabel("Connection Name"));
                        }
                        {
                            var lbl = new JLabel(conn.name());
                            pnlConnectionName.add(lbl);
                            lbl.putClientProperty("FlatLaf.styleClass", "h2");
                        }
                    }

                    var data = new LinkedHashMap<String, JLabel>();
                    data.put("Host", new JLabel(conn.host()));
                    data.put("Username", new JLabel(conn.username()));
                    data.put("Server start", lblRunningSince);
                    data.put("XRPL Status", lblXrplState);

                    var counter = 0;
                    for (var e : data.entrySet()) {
                        final var TOP_OFFSET = 10;
                        final var HEIGHT = 20;
                        var north = counter * HEIGHT + TOP_OFFSET;
                        var lbl = new JLabel("%s:".formatted(e.getKey()));
                        pnlConnection.add(lbl);
                        sl.putConstraint(SpringLayout.WEST, lbl, 0, SpringLayout.WEST, pnlConnectionName);
                        sl.putConstraint(SpringLayout.NORTH, lbl, north, SpringLayout.SOUTH, pnlConnectionName);

                        var value = e.getValue();
                        pnlConnection.add(value);
                        sl.putConstraint(SpringLayout.WEST, value, 100, SpringLayout.WEST, lbl);
                        sl.putConstraint(SpringLayout.NORTH, value, 0, SpringLayout.NORTH, lbl);

                        counter++;
                    }
                }
            }
            {
                add(pnlServerFeatures);
                mainLayout.putConstraint(SpringLayout.NORTH, pnlServerFeatures, 0, SpringLayout.SOUTH, pnlConnection);
                mainLayout.putConstraint(SpringLayout.WEST, pnlServerFeatures, iconOffsetEast, SpringLayout.EAST, lblIcon);
                pnlServerFeatures.setPreferredSize(new Dimension(500, 130));
                pnlServerFeatures.setLayout(serverFeaturesSpringLayout);
                {
                    var lbl = new JLabel("Server Features");
                    pnlServerFeatures.add(lbl);
                    lbl.putClientProperty("FlatLaf.styleClass", "h3");
                    serverFeaturesSpringLayout.putConstraint(SpringLayout.WEST, lbl, 0, SpringLayout.WEST, pnlServerFeatures);
                    serverFeaturesSpringLayout.putConstraint(SpringLayout.NORTH, lbl, 0, SpringLayout.NORTH, pnlServerFeatures);
                }
            }
            {
                var pnlDirectories = new JPanel();
                add(pnlDirectories);
                mainLayout.putConstraint(SpringLayout.NORTH, pnlDirectories, 0, SpringLayout.SOUTH, pnlServerFeatures);
                mainLayout.putConstraint(SpringLayout.WEST, pnlDirectories, iconOffsetEast, SpringLayout.EAST, lblIcon);
                pnlDirectories.setPreferredSize(new Dimension(500, 130));
                var sl = new SpringLayout();
                pnlDirectories.setLayout(sl);
                {
                    var lbl = new JLabel("Server Directories");
                    pnlDirectories.add(lbl);
                    lbl.putClientProperty("FlatLaf.styleClass", "h3");
                    sl.putConstraint(SpringLayout.WEST, lbl, 0, SpringLayout.WEST, pnlDirectories);
                    sl.putConstraint(SpringLayout.NORTH, lbl, 0, SpringLayout.NORTH, pnlDirectories);
                }

                var icon = new FlatSVGIcon("img/save.svg", 16, 16);
                icon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> Color.lightGray));
                lblConfigPath = new FilePathLabel(icon);
                lblLogPath = new FilePathLabel(icon);
                lblValidators = new FilePathLabel(icon);

                var data = new LinkedHashMap<String, Component>();
                data.put("Installation", lblInstallPath);
                data.put("Configuration", lblConfigPath);
                data.put("Log", lblLogPath);
                data.put("Database", lblDatabasePath);
                data.put("Validators", lblValidators);

                var counter = 0;
                for (var e : data.entrySet()) {
                    final var TOP_OFFSET = 20;
                    final var HEIGHT = 20;
                    var north = counter * HEIGHT + TOP_OFFSET;
                    var lbl = new JLabel("%s:".formatted(e.getKey()));
                    pnlDirectories.add(lbl);
                    sl.putConstraint(SpringLayout.WEST, lbl, 0, SpringLayout.WEST, pnlDirectories);
                    sl.putConstraint(SpringLayout.NORTH, lbl, north, SpringLayout.NORTH, pnlDirectories);

                    var value = e.getValue();
                    pnlDirectories.add(value);
                    sl.putConstraint(SpringLayout.WEST, value, 130, SpringLayout.WEST, lbl);
                    sl.putConstraint(SpringLayout.NORTH, value, 0, SpringLayout.NORTH, lbl);

                    counter++;
                }
            }
        }
        {
            var actions = new JPanel();
            add(actions);
            mainLayout.putConstraint(SpringLayout.NORTH, actions, 0, SpringLayout.NORTH, this);
            mainLayout.putConstraint(SpringLayout.EAST, actions, 0, SpringLayout.EAST, this);
            mainLayout.putConstraint(SpringLayout.SOUTH, actions, 0, SpringLayout.SOUTH, this);
            actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));
            actions.setPreferredSize(new Dimension(150, getHeight()));
            {
                final var smallGap = 5;
                final var largeGap = 40;
                cmdInstall = createActionButton("Install XRPL", this::installXrplBinary);
                actions.add(cmdInstall);
                actions.add(Box.createVerticalStrut(smallGap));
                cmdUpdate = createActionButton("Update XRPL", this::updateXrplBinary);
                actions.add(cmdUpdate);
                actions.add(Box.createVerticalStrut(largeGap));

                cmdStop = createActionButton("Stop XRPL Service", this::stopService);
                actions.add(cmdStop);
                actions.add(Box.createVerticalStrut(smallGap));
                cmdStart = createActionButton("Start XRPL Service", this::startService);
                actions.add(cmdStart);
                actions.add(Box.createVerticalStrut(smallGap));
                cmdRestart = createActionButton("Restart XRPL Service", this::restartService);
                actions.add(cmdRestart);
                actions.add(Box.createVerticalStrut(largeGap));

                cmdDeleteServiceDb = createActionButton("Delete XRPL database", this::deleteServiceDb);
                actions.add(cmdDeleteServiceDb);
                actions.add(Box.createVerticalStrut(smallGap));
                cmdDeleteDebugLog = createActionButton("Delete XRPL log", this::deleteDebugLog);
                actions.add(cmdDeleteDebugLog);

                actions.add(Box.createVerticalGlue());
            }
        }

        lblConfigPath.addFilePathLabelListener(() -> saveRemoteFileLocally(lblConfigPath.getText()));
        lblLogPath.addFilePathLabelListener(() -> saveRemoteFileLocally(lblLogPath.getText()));
        lblValidators.addFilePathLabelListener(() -> saveRemoteFileLocally(lblValidators.getText()));
    }

    private void createServerFeatures(LinkedHashMap<String, String> data) {
        // First element is title
        for (var i = pnlServerFeatures.getComponentCount() - 1; i > 1; i--) {
            pnlServerFeatures.remove(i);
        }

        final var TOP_OFFSET = 20;
        final var HEIGHT = 20;
        if (data.isEmpty()) {
            var north = TOP_OFFSET;
            var lbl = new JLabel("not available");
            pnlServerFeatures.add(lbl);
            serverFeaturesSpringLayout.putConstraint(SpringLayout.WEST, lbl, 0, SpringLayout.WEST, pnlServerFeatures);
            serverFeaturesSpringLayout.putConstraint(SpringLayout.NORTH, lbl, north, SpringLayout.NORTH, pnlServerFeatures);
            return;
        }

        var counter = 0;
        for (var e : data.entrySet()) {
            var north = counter * HEIGHT + TOP_OFFSET;
            var lbl = new JLabel("%s:".formatted(e.getKey()));
            pnlServerFeatures.add(lbl);
            serverFeaturesSpringLayout.putConstraint(SpringLayout.WEST, lbl, 0, SpringLayout.WEST, pnlServerFeatures);
            serverFeaturesSpringLayout.putConstraint(SpringLayout.NORTH, lbl, north, SpringLayout.NORTH, pnlServerFeatures);

            var value = new JLabel(e.getValue());
            pnlServerFeatures.add(value);
            serverFeaturesSpringLayout.putConstraint(SpringLayout.WEST, value, 130, SpringLayout.WEST, lbl);
            serverFeaturesSpringLayout.putConstraint(SpringLayout.NORTH, value, 0, SpringLayout.NORTH, lbl);

            counter++;
        }
    }

    private JButton createActionButton(String caption, Runnable action) {
        var cmd = new JButton(caption);
        cmd.setPreferredSize(new Dimension(150, 35));
        cmd.setMinimumSize(cmd.getPreferredSize());
        cmd.setMaximumSize(cmd.getPreferredSize());
        cmd.addActionListener(e -> action.run());
        return cmd;
    }

    private void saveRemoteFileLocally(String remotePath) {
        var fileName = Path.of(remotePath).getFileName().toString();

        var fc = new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.home")));
        fc.setSelectedFile(new File(fileName));
        int option = fc.showSaveDialog(this);
        if (option != JFileChooser.APPROVE_OPTION) {
            return;
        }

        var outputPath = fc.getSelectedFile().getAbsolutePath();

        try {
            var out = new ByteArrayOutputStream();
            session.get(remotePath, out);
            try (var fos = new FileOutputStream(outputPath)) {
                out.writeTo(fos);
            }

            JOptionPane.showMessageDialog(this, "File saved to %s".formatted(outputPath), "", JOptionPane.INFORMATION_MESSAGE);
        } catch (SshApiException | IOException e) {
            ExceptionDialog.show(this, e);
        }
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
                setEnabled(false);
                installer.install();

                xrplService.restart(false);
                outputInfo("xrpl binary successfully installed");
            } catch (SshApiException e) {
                outputError("Installing xrpl binary failed. " + e.getMessage());
            } finally {
                setEnabled(true);
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
                setEnabled(false);
                xrplService.stop();
                installer.install((XrplBinaryPackage) cboPackages.getSelectedItem());
                xrplService.restart();
                outputInfo("xrpl binary successfully updated");
            } catch (SshApiException e) {
                outputError("Updating xrpl binary failed. " + e.getMessage());
            } finally {
                setEnabled(true);
            }
            refreshViews();
        });
    }

    private void refreshViews() {
        statusView.refresh();
        refreshView();
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
                refreshViews();
            } catch (SshApiException e) {
                outputError(e.getMessage());
            }
        });
    }

    private void stopService() {
        Utils.runAsync(() -> {
            try {
                xrplService.stop();
                refreshViews();
            } catch (SshApiException e) {
                outputError(e.getMessage());
            }
        });
    }

    private void startService() {
        Utils.runAsync(() -> {
            try {
                xrplService.start();
                refreshViews();
            } catch (SshApiException e) {
                outputError(e.getMessage());
            }
        });
    }

    private void deleteServiceDb() {
        try {
            int result = JOptionPane.showConfirmDialog(parent, "Do you really want to stop the service and remove its database at %s? This step cannot be undone.\n\nDo you want to proceed?".formatted(xrplBinary.remotePaths().databasePath().orElse("")), "Remove database?", JOptionPane.YES_NO_CANCEL_OPTION);
            if (result != JOptionPane.YES_OPTION) {
                return;
            }
            xrplService.stop();
            xrplBinary.deleteDatabase();
            xrplService.start();
        } catch (SshApiException e) {
            outputError(e.getMessage());
        }
        refreshViews();
    }

    private void deleteDebugLog() {
        try {
            int result = JOptionPane.showConfirmDialog(parent, "Do you really want to remove the xrpl service log file %s? This step cannot be undone.\n\nDo you want to proceed?".formatted(xrplBinary.remotePaths().debugLogPath()), "Remove log?", JOptionPane.YES_NO_CANCEL_OPTION);
            if (result != JOptionPane.YES_OPTION) {
                return;
            }
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
            cmdStop.setEnabled(xrplInstalled);
            cmdStart.setEnabled(xrplInstalled);
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
        cmdStop.setEnabled(enabled && xrplInstalled);
        cmdStart.setEnabled(enabled && xrplInstalled);
        cmdRestart.setEnabled(enabled && xrplInstalled);
        try {
            cmdDeleteServiceDb.setEnabled(enabled && xrplInstalled && xrplBinary.remotePaths().databasePath().isPresent());
        } catch (SshApiException e) {
            ExceptionDialog.show(this, e);
        }
        cmdDeleteDebugLog.setEnabled(enabled && xrplInstalled);

        lblConfigPath.setEnabled(enabled && xrplInstalled);
        lblLogPath.setEnabled(enabled && xrplInstalled);
        lblValidators.setEnabled(enabled && xrplInstalled);
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

    @Override
    public void onCompleted() {
        setEnabled(true);
    }

    @Override
    protected void refresh() {
        xrplBinaryRefresh();
        refreshView();
    }

    private void refreshView() {
        try {
            if (!xrplBinary.installed()) {
                lblXrplState.setText("not installed");
                var icon = new FlatSVGIcon("img/xrpledger.svg", 120, 91);
                icon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> Color.lightGray));
                lblIcon.setIcon(icon);
                createServerFeatures(new LinkedHashMap<>());
                return;
            }

            if (xrplBinary.type() == XrplType.Xahau) {
                lblIcon.setIcon(new FlatSVGIcon("img/xahau.svg", 120, 120));
            } else {
                lblIcon.setIcon(new FlatSVGIcon("img/xrpledger.svg", 120, 91));
            }

            final var df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            lblRunningSince.setText(df.format(systemMonitor.serverStartingTime()));

            var xrplRamUsed = systemMonitor.memoryUsed(xrplBinary.processName());
            var running = xrplRamUsed != null;
            lblXrplState.setText(running ? "running" : "stopped");

            var server = xrplBinary.config().server();
            var data = new LinkedHashMap<String, String>();
            for (var s : server.all()) {
                data.put(s.name(), "%s (%s)".formatted(s.protocol(), s.port()));
            }
            createServerFeatures(data);

            var remotePath = xrplBinary.remotePaths();
            lblInstallPath.setText(remotePath.installPath());
            lblConfigPath.setText(remotePath.configPath());
            lblLogPath.setText(remotePath.debugLogPath());
            lblDatabasePath.setText(remotePath.databasePath().orElse("undefined"));
            lblValidators.setText(remotePath.validatorPath().orElse("undefined"));
        } catch (SshApiException e) {
            ExceptionDialog.show(this, e);
        }
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
