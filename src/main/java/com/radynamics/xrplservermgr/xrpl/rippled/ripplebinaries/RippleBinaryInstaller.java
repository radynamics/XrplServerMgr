package com.radynamics.xrplservermgr.xrpl.rippled.ripplebinaries;

import com.radynamics.xrplservermgr.Main;
import com.radynamics.xrplservermgr.sshapi.*;
import com.radynamics.xrplservermgr.xrpl.*;
import com.radynamics.xrplservermgr.xrpl.parser.ConfigCfg;
import com.radynamics.xrplservermgr.xrpl.rippled.UpdateChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.Duration;
import java.util.*;

public class RippleBinaryInstaller implements XrplInstaller {
    private final static Logger log = LogManager.getLogger(RippleBinaryInstaller.class);
    private final SshSession session;
    private final XrplBinary binary;
    private final SystemMonitor systemMonitor;
    private final Timer installerMonitorTimer = new Timer("InstallerMonitor");

    private final ArrayList<ActionLogListener> listener = new ArrayList<>();

    public RippleBinaryInstaller(SshSession session, XrplBinary binary, SystemMonitor systemMonitor) {
        this.session = session;
        this.binary = binary;
        this.systemMonitor = systemMonitor;
    }

    public void install() throws SshApiException {
        install(Optional.empty());
    }

    private void install(Optional<String> version) throws SshApiException {
        if (binary.installed()) {
            throw new SshApiException("rippled is already installed.");
        }

        var installer = createInstaller();

        raiseOnEvent(ActionLogEvent.info("Installing rippled..."));

        installer.install(version);
        chownRippledCfg();

        try {
            binary.config(ConfigCfg.parse(Main.class.getClassLoader().getResourceAsStream("rippled/rippled-tiny.cfg")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        raiseOnEvent(ActionLogEvent.info("Installing rippled completed"));
    }

    @Override
    public void install(XrplBinaryPackage pkg) throws SshApiException {
        if (!binary.installed()) {
            throw new SshApiException("rippled is not installed.");
        }
        if (systemMonitor.installerRunning()) {
            throw new SshApiException("Cannot start update due installer is already running.");
        }

        var installer = createInstaller();

        raiseOnEvent(ActionLogEvent.info("This may take several minutes... Please wait."));

        var stillRunning = new TimerTask() {
            public void run() {
                raiseOnEvent(ActionLogEvent.info("Update still ongoing. Please wait..."));
            }
        };
        try {
            installerMonitorTimer.scheduleAtFixedRate(stillRunning, Duration.ofSeconds(10).toMillis(), Duration.ofSeconds(60).toMillis());
            installer.update(pkg);
            installerMonitorTimer.cancel();

            session.executeSudo("systemctl daemon-reload");
            session.executeSudo("service rippled restart");

            chownRippledCfg();
            raiseOnEvent(ActionLogEvent.info("Update completed"));
        } finally {
            installerMonitorTimer.cancel();
        }
    }

    @Override
    public List<XrplBinaryPackage> availableUpdates(ReleaseChannel channel) throws XrplApiException {
        // TODO: On Ubuntu this changes /etc/apt/sources.list.d/ripple.list and isn't optimal. Viewing available updates shouldn't change anything.
        try {
            changeChannel(UpdateChannel.of(channel.name()).orElseThrow());
            return systemMonitor.availableUpdates(binary.packageName());
        } catch (Exception e) {
            throw new XrplApiException(e);
        }
    }

    public void changeChannel(UpdateChannel channel) throws SshApiException {
        createInstaller().changeChannel(channel);
    }

    public void chownRippledCfg() throws SshApiException {
        systemMonitor.chown("/opt/ripple/etc/rippled.cfg");
    }

    private RippledPlatformInstaller createInstaller() throws SshApiException {
        switch (systemMonitor.platform()) {
            case Debian:
                return new DebianInstaller(session, systemMonitor);
            default:
                throw new SshApiException("Platform is not supported by installer. Please install manually.");
        }
    }

    public void addActionLogListener(ActionLogListener l) {
        listener.add(l);
    }

    @Override
    public void removeActionLogListener(ActionLogListener l) {
        listener.remove(l);
    }

    @Override
    public void addInstallerListener(XrplInstallerListener l) {
        // do nothing
    }

    @Override
    public String displayName() {
        return "Ripple binary installer";
    }

    private void raiseOnEvent(ActionLogEvent event) {
        for (var l : listener) {
            l.onEvent(event);
        }
    }
}
