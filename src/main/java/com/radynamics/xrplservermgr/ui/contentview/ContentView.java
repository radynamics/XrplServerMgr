package com.radynamics.xrplservermgr.ui.contentview;

import com.radynamics.xrplservermgr.sshapi.ActionLogLevel;
import com.radynamics.xrplservermgr.sshapi.SshApiException;
import com.radynamics.xrplservermgr.sshapi.SshSession;
import com.radynamics.xrplservermgr.sshapi.SystemMonitor;
import com.radynamics.xrplservermgr.ui.ActionOutputView;
import com.radynamics.xrplservermgr.ui.StatusView;
import com.radynamics.xrplservermgr.ui.TabPage;
import com.radynamics.xrplservermgr.ui.XrplService;
import com.radynamics.xrplservermgr.xrpl.XrplBinary;
import com.radynamics.xrplservermgr.xrpl.XrplUtil;
import com.radynamics.xrplservermgr.xrpl.rippled.RippledCommandException;

import javax.swing.*;

public abstract class ContentView extends JPanel implements TabPage {
    protected final JFrame parent;
    private ActionOutputView actionOutput;
    protected StatusView statusView;
    protected SshSession session;
    protected XrplBinary xrplBinary;
    protected SystemMonitor systemMonitor;
    protected XrplService xrplService;

    public ContentView(JFrame parent) {
        this.parent = parent;
    }

    public abstract String tabText();

    public abstract void close();

    public void init(ActionOutputView actionOutput, StatusView statusView, SystemMonitor systemMonitor) {
        this.actionOutput = actionOutput;
        this.statusView = statusView;
        this.systemMonitor = systemMonitor;
    }

    public void initSession(SshSession session, SystemMonitor systemMonitor, XrplBinary xrplBinary) {
        this.session = session;
        this.systemMonitor = systemMonitor;
        this.xrplBinary = xrplBinary;
        this.xrplService = new XrplService(parent, this.systemMonitor, xrplBinary);

        refresh();
    }

    protected void refresh() {
    }

    protected void xrplBinaryRefresh() {
        try {
            xrplBinary.refresh();
        } catch (SshApiException e) {
            outputError(e.getMessage());
        } catch (RippledCommandException e) {
            if (!XrplUtil.isServiceNotRunningError(e)) {
                outputError(e.getMessage());
            }
        }
    }

    protected void outputInfo(String message) {
        appendOutput(message, ActionLogLevel.Info);
    }

    protected void outputError(String message) {
        appendOutput(message, ActionLogLevel.Error);
    }

    protected void outputWarn(String message) {
        appendOutput(message, ActionLogLevel.Warning);
    }

    protected void appendOutput(String message, ActionLogLevel level) {
        SwingUtilities.invokeLater(() -> actionOutput.append(message, level));
    }
}
