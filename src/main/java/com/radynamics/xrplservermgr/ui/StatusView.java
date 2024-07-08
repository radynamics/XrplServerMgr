package com.radynamics.xrplservermgr.ui;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.FlatButton;
import com.radynamics.xrplservermgr.datasize.Size;
import com.radynamics.xrplservermgr.datasize.SizeConverter;
import com.radynamics.xrplservermgr.datasize.SizeFormatter;
import com.radynamics.xrplservermgr.sshapi.ConnectionInfo;
import com.radynamics.xrplservermgr.sshapi.SshApiException;
import com.radynamics.xrplservermgr.sshapi.SystemMonitor;
import com.radynamics.xrplservermgr.sshapi.parser.DiskUsage;
import com.radynamics.xrplservermgr.sshapi.parser.Memory;
import com.radynamics.xrplservermgr.sshapi.parser.Uptime;
import com.radynamics.xrplservermgr.xrpl.XrplBinary;
import com.radynamics.xrplservermgr.xrpl.XrplUtil;
import com.radynamics.xrplservermgr.xrpl.parser.ConfigCfg;
import com.radynamics.xrplservermgr.xrpl.parser.ServerInfo;
import com.radynamics.xrplservermgr.xrpl.rippled.RippledCommandException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class StatusView extends JPanel {
    private final static Logger log = LogManager.getLogger(StatusView.class);

    private SystemMonitor systemMonitor;
    private XrplBinary xrplBinary;
    private Timer timer;

    private final FlatButton cmdRefresh = new FlatButton();
    private final JLabel lblConnectionStatus = new JLabel();
    private final JLabel lblXrplStatus = new JLabel();
    private final JTextField lblHostId = Utils.formatAsLabel(new JTextField());
    private final JTextField lblServerState = Utils.formatAsLabel(new JTextField());
    private final JTextField lblBuildVersion = Utils.formatAsLabel(new JTextField());
    private final JTextField lblNetworkId = Utils.formatAsLabel(new JTextField());
    private final JTextField lblNodeSize = Utils.formatAsLabel(new JTextField());
    private final JTextField lblPublicKey = Utils.formatAsLabel(new JTextField());
    private final JTextField lblXrplRam = Utils.formatAsLabel(new JTextField());
    private final JTextField lblSystemRam = Utils.formatAsLabel(new JTextField());
    private final JTextField lblSystemDisk = Utils.formatAsLabel(new JTextField());
    private final JTextField lblSystemLoad = Utils.formatAsLabel(new JTextField());

    public StatusView(ConnectionInfo conn) {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(createHeader("Connection", lblConnectionStatus));
        JTextField lblHost = Utils.formatAsLabel(new JTextField());
        add(createRow("Host:", lblHost));
        JTextField lblPort = Utils.formatAsLabel(new JTextField());
        add(createRow("Port:", lblPort));

        add(Box.createRigidArea(new Dimension(0, 5)));
        add(createXrplHeader());
        add(createRow("Host Id:", lblHostId));
        add(createRow("Status:", lblServerState));
        add(createRow("Build:", lblBuildVersion));
        add(createRow("Network:", lblNetworkId));
        add(createRow("Node size:", lblNodeSize));
        add(createRow("Public Key:", lblPublicKey));
        add(createRow("RAM:", lblXrplRam));

        add(Box.createRigidArea(new Dimension(0, 5)));
        add(createHeader("System"));
        add(createRow("RAM:", lblSystemRam));
        add(createRow("Disk:", lblSystemDisk));
        add(createRow("Load:", lblSystemLoad));

        lblHost.setText(conn.host());
        lblPort.setText(conn.port().toString());
    }

    private JPanel createXrplHeader() {
        var pnl = createHeader("XRPL", lblXrplStatus);
        pnl.add(cmdRefresh);
        var l = (SpringLayout) pnl.getLayout();
        l.putConstraint(SpringLayout.EAST, cmdRefresh, 0, SpringLayout.EAST, pnl);
        l.putConstraint(SpringLayout.NORTH, cmdRefresh, 0, SpringLayout.NORTH, pnl);
        cmdRefresh.setToolTipText("Refresh");
        cmdRefresh.setIcon(new FlatSVGIcon("img/refresh.svg", 12, 12));
        cmdRefresh.setButtonType(FlatButton.ButtonType.toolBarButton);
        cmdRefresh.addActionListener(e -> refresh());
        return pnl;
    }

    private JPanel createHeader(String text) {
        return createHeader(text, null);
    }

    private JPanel createHeader(String text, Component c) {
        var l = new SpringLayout();
        var pnl = new JPanel();
        pnl.setLayout(l);
        pnl.setPreferredSize(new Dimension(210, 18));

        var lbl = new JLabel(text);
        pnl.add(lbl);
        lbl.putClientProperty("FlatLaf.style", "font: 100% $semibold.font");
        l.putConstraint(SpringLayout.WEST, lbl, 0, SpringLayout.WEST, pnl);
        l.putConstraint(SpringLayout.NORTH, lbl, 0, SpringLayout.NORTH, pnl);

        if (c != null) {
            pnl.add(c);
            l.putConstraint(SpringLayout.WEST, c, 70, SpringLayout.WEST, pnl);
            l.putConstraint(SpringLayout.NORTH, c, 0, SpringLayout.NORTH, pnl);
        }
        return pnl;
    }

    private static JPanel createRow(String text, Component c) {
        var l = new SpringLayout();
        var pnl = new JPanel();
        pnl.setLayout(l);
        pnl.setPreferredSize(new Dimension(200, 18));

        var lbl = new JLabel(text);
        pnl.add(lbl);
        l.putConstraint(SpringLayout.WEST, lbl, 5, SpringLayout.WEST, pnl);
        l.putConstraint(SpringLayout.NORTH, lbl, 0, SpringLayout.NORTH, pnl);

        pnl.add(c);
        l.putConstraint(SpringLayout.WEST, c, 70, SpringLayout.WEST, pnl);
        l.putConstraint(SpringLayout.NORTH, c, 0, SpringLayout.NORTH, pnl);
        return pnl;
    }

    public void init(SystemMonitor systemMonitor, XrplBinary xrplBinary) {
        this.systemMonitor = systemMonitor;
        this.xrplBinary = xrplBinary;

        if (xrplBinary == null) {
            stopRefreshingTask();
        } else {
            startRefreshingTask();
        }
    }

    private void startRefreshingTask() {
        stopRefreshingTask();
        timer = new java.util.Timer("statusRefreshing");
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                refresh();
            }
        }, 1000L, 5000);
    }

    private void stopRefreshingTask() {
        if (timer != null) {
            timer.cancel();
        }
        timer = null;
    }

    public void refresh() {
        var r = new Runnable() {
            public synchronized void run() {
                try {
                    var memory = systemMonitor.memory().stream().filter(o -> o.path().equals("Mem")).findFirst().orElse(null);
                    refresh(systemMonitor.diskUsage(), memory, systemMonitor.loadAverage());

                    if (xrplBinary == null) {
                        refresh((ServerInfo) null, null, null);
                    } else {
                        xrplBinary.refresh();
                        refresh(xrplBinary.serverInfo(), xrplBinary.config(), systemMonitor.virtualMemory(xrplBinary.processName()));
                    }
                } catch (SshApiException e) {
                    log.error(e.getMessage(), e);
                } catch (RippledCommandException e) {
                    if (XrplUtil.isServiceNotRunningError(e)) {
                        lblXrplStatus.setText("(not running)");
                        clearXrplStatus();
                    } else {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        };
        new Thread(r).start();
    }

    private void clearXrplStatus() {
        lblHostId.setText("");
        lblServerState.setText("");
        lblBuildVersion.setText("");
        lblNetworkId.setText("");
        lblNodeSize.setText("");
        lblPublicKey.setText("");
        lblXrplRam.setText("");
    }

    private void refresh(ServerInfo serverInfo, ConfigCfg config, Size xrplRamUsed) {
        lblXrplStatus.setForeground(lblHostId.getForeground());
        if (serverInfo == null) {
            lblXrplStatus.setText("(not installed)");
            return;
        }

        if (serverInfo.amendmentBlocked()) {
            lblXrplStatus.setForeground(Consts.ColorLightRed);
            lblXrplStatus.setText("(Amendment Blocked)");
        } else {
            var running = xrplRamUsed != null;
            if (running) {
                lblXrplStatus.setText("");
            } else {
                lblXrplStatus.setText("(stopped)");
            }
        }

        lblHostId.setText(serverInfo.hostId());
        lblServerState.setText(serverInfo.serverState());
        lblBuildVersion.setText(serverInfo.buildVersion());
        lblNetworkId.setText(config.networkId());
        lblNodeSize.setText(serverInfo.nodeSize());
        var publicKey = "none";
        if (!StringUtils.isEmpty(serverInfo.pubKeyNode())) {
            publicKey = serverInfo.pubKeyNode();
        } else if (!StringUtils.isEmpty(serverInfo.pubKeyValidator())) {
            publicKey = serverInfo.pubKeyValidator();
        }
        lblPublicKey.setText(publicKey);

        lblXrplRam.setText(xrplRamUsed == null ? "" : SizeFormatter.format(SizeConverter.toGb(xrplRamUsed)));
    }

    private void refresh(List<DiskUsage> diskUsages, Memory memory, Uptime load) {
        if (memory == null) {
            lblSystemRam.setText("unknown");
        } else {
            lblSystemRam.setText("%s / %s GB".formatted(com.radynamics.xrplservermgr.utils.Utils.kibToGbText(memory.used()), com.radynamics.xrplservermgr.utils.Utils.kibToGbText(memory.total())));
            lblSystemRam.setToolTipText(lblSystemRam.getText());
        }

        var sb = new StringBuilder();
        for (var du : diskUsages) {
            sb.append("%s%%: %s".formatted(du.usedPercent(), du.path()));
        }
        lblSystemDisk.setText(sb.toString());
        lblSystemDisk.setToolTipText(lblSystemDisk.getText());

        lblSystemLoad.setText("%s / %s / %s".formatted(load.past1min(), load.past5min(), load.past15min()));
        lblSystemLoad.setToolTipText(lblSystemLoad.getText());
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        stopRefreshingTask();
        lblConnectionStatus.setText(enabled ? "" : "(disconnected)");
        cmdRefresh.setEnabled(enabled);
    }
}
