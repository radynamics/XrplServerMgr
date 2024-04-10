package com.radynamics.xrplservermgr.xrpl.xahaud;

import com.radynamics.xrplservermgr.sshapi.*;
import com.radynamics.xrplservermgr.xrpl.*;

import java.util.ArrayList;
import java.util.List;

public class XahaudInstaller implements XrplInstaller {
    private final SshSession session;
    private final XrplBinary binary;
    private final SystemMonitor systemMonitor;

    private final ArrayList<ActionLogListener> listener = new ArrayList<>();

    private final static String baseDirectory = "/opt/xahaud";
    public final static String installRoot = "%s/bin".formatted(baseDirectory);

    public XahaudInstaller(SshSession session, XrplBinary binary, SystemMonitor systemMonitor) {
        this.session = session;
        this.binary = binary;
        this.systemMonitor = systemMonitor;
    }

    public void install() throws SshApiException {
        if (binary.installed()) {
            throw new SshApiException("xahaud is already installed.");
        }

        raiseOnEvent(ActionLogEvent.info("Installing xahaud..."));

        session.executeSudo("apt-get update");
        session.executeSudo("apt install curl -y");
        session.executeSudo("mkdir -p %s".formatted(baseDirectory));
        installLatestUpdateScript();
        session.executeSudo("%s/xahaud-install-update.sh".formatted(baseDirectory));
        session.executeSudo("systemctl restart xahaud");

        chownXahaudCfg();

        raiseOnEvent(ActionLogEvent.info("Installing rippled completed"));
    }

    private void installLatestUpdateScript() throws SshApiException {
        session.executeSudo("wget --inet4-only https://raw.githubusercontent.com/Xahau/mainnet-docker/main/xahaud-install-update.sh -O %s/xahaud-install-update.sh".formatted(baseDirectory));
        session.executeSudo("chmod +x %s/xahaud-install-update.sh".formatted(baseDirectory));
    }

    @Override
    public void install(XrplBinaryPackage pkg) throws SshApiException {
        if (!binary.installed()) {
            throw new SshApiException("xahaud is not installed.");
        }

        installLatestUpdateScript();

        raiseOnEvent(ActionLogEvent.info("This may take several minutes... Please wait."));
        var response = session.executeSudo("%s/xahaud-install-update.sh -v %s".formatted(baseDirectory, pkg.versionText()));
        if (!response.success()) {
            raiseOnEvent(ActionLogEvent.error("Update failed with exit code %s. %s".formatted(response.exitStatus(), response.asString())));
            return;
        }
        raiseOnEvent(ActionLogEvent.info("Update completed"));
    }

    @Override
    public List<XrplBinaryPackage> availableUpdates(ReleaseChannel channel) throws XrplApiException {
        var d = new UpdateDiscovery();
        return d.list(UpdateChannel.of(channel.name()).orElseThrow());
    }

    public void chownXahaudCfg() throws SshApiException {
        systemMonitor.chown("%s/etc/xahaud.cfg".formatted(baseDirectory));
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
        return "Xahaud GitHub Script";
    }

    private void raiseOnEvent(ActionLogEvent event) {
        for (var l : listener) {
            l.onEvent(event);
        }
    }
}
