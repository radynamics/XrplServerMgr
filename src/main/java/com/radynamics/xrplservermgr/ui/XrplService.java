package com.radynamics.xrplservermgr.ui;

import com.radynamics.xrplservermgr.sshapi.SshApiException;
import com.radynamics.xrplservermgr.sshapi.SystemMonitor;
import com.radynamics.xrplservermgr.xrpl.XrplBinary;

import javax.swing.*;

public class XrplService {
    private final JFrame parent;
    private final SystemMonitor systemMonitor;
    private final XrplBinary xrplBinary;

    public XrplService(JFrame parent, SystemMonitor systemMonitor, XrplBinary xrplBinary) {
        this.parent = parent;
        this.systemMonitor = systemMonitor;
        this.xrplBinary = xrplBinary;
    }

    public void start() throws SshApiException {
        systemMonitor.startService(xrplBinary.processName());
    }

    public void stop() throws SshApiException {
        systemMonitor.stopService(xrplBinary.processName());
    }

    public void restart() throws SshApiException {
        restart(true);
    }

    public void restart(boolean askRestart) throws SshApiException {
        systemMonitor.restartService(xrplBinary.processName(), askRestart ? this::askRestart : () -> true);
    }

    private Boolean askRestart() {
        var ret = JOptionPane.showConfirmDialog(parent, "rippled needs to be restarted. This will cause a service downtime. Do you want to restart rippled now?", "Service restart required", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return ret == JOptionPane.YES_OPTION;
    }
}
