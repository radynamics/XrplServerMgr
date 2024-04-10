package com.radynamics.xrplservermgr.xrpl.rippled.portablebuild;

import com.radynamics.xrplservermgr.Main;
import com.radynamics.xrplservermgr.sshapi.*;
import com.radynamics.xrplservermgr.sshapi.parser.GpgVerify;
import com.radynamics.xrplservermgr.xrpl.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PortableBuildInstaller implements XrplInstaller {
    private final SshSession session;
    private final XrplBinary binary;
    private final SystemMonitor systemMonitor;

    private final ArrayList<ActionLogListener> listener = new ArrayList<>();
    private final ArrayList<XrplInstallerListener> installerListener = new ArrayList<>();

    // TODO: should this path be dynamic?
    public final static String installRoot = "/opt/ripple/bin";
    // https://github.com/XRPLF/rippled-portable-builds
    private final static String fingerprint = "6D70 4017 0DEA 4F69 DEF5  D569 B6F9 7CF2 1A96 2003";

    public PortableBuildInstaller(SshSession session, XrplBinary binary, SystemMonitor systemMonitor) {
        this.session = session;
        this.binary = binary;
        this.systemMonitor = systemMonitor;
    }

    @Override
    public void install() throws SshApiException {
        if (binary.installed()) {
            throw new SshApiException("rippled is already installed.");
        }

        XrplBinaryPackage latest;
        try {
            latest = availableUpdates().stream().findFirst().orElseThrow();
        } catch (XrplApiException e) {
            throw new RuntimeException("Could not find latest portable build binary.", e);
        }
        install(latest, false);
    }

    @Override
    public void install(XrplBinaryPackage pkg) throws SshApiException {
        install(pkg, true);
    }

    private void install(XrplBinaryPackage pkg, boolean update) throws SshApiException {
        // Ensure sudo password is checked for validity before any action has been taken.
        session.executeSudo("echo \"hi\"");

        raiseOnEvent(ActionLogEvent.info("Installing rippled..."));

        session.executeSudo("mkdir -p %s".formatted(installRoot));
        systemMonitor.chown(installRoot);

        final var version = pkg.versionText();
        final var versionFolder = "%s/%s".formatted(installRoot, version);
        session.executeSudo("rm -R -f %s".formatted(versionFolder));
        session.executeSudo("mkdir -p %s".formatted(versionFolder));

        final var qpgPath = "%s/xrplf-binary-packages-public.gpg".formatted(versionFolder);
        // https://github.com/XRPLF/rippled-portable-builds
        session.executeSudo("wget https://raw.githubusercontent.com/XRPLF/rippled-portable-builds/main/xrplf-binary-packages-public.gpg -O %s".formatted(qpgPath));
        session.execute("gpg --import %s".formatted(qpgPath));

        final var binaryName = "rippled-portable-%s".formatted(version);
        final var sigName = "rippled-portable-%s.sig".formatted(version);
        final var versionPathBinary = "%s/%s".formatted(versionFolder, binaryName);
        final var versionPathSig = "%s/%s".formatted(versionFolder, sigName);
        session.executeSudo("wget https://github.com/XRPLF/rippled-portable-builds/raw/main/releases/%s -O %s".formatted(binaryName, versionPathBinary));
        session.executeSudo("wget https://github.com/XRPLF/rippled-portable-builds/raw/main/releases/%s -O %s".formatted(sigName, versionPathSig));

        var signatureVerificationResponse = session.execute("gpg --verify %s %s".formatted(versionPathSig, versionPathBinary));
        if (!verifyAndAskForContinuation(signatureVerificationResponse)) {
            return;
        }

        session.executeSudo("chmod +x %s".formatted(versionPathBinary));
        final var execPath = "%s/rippled".formatted(installRoot);
        session.executeSudo("rm -f %s".formatted(execPath));
        session.executeSudo("ln -snf %s %s".formatted(versionPathBinary, execPath));

        final var serviceName = "rippled.service";
        if (!update) {
            final var configPath = "/opt/ripple/rippled.cfg";
            createServiceFile("rippled", serviceName, execPath, configPath, session.user());

            put(configPath, Main.class.getClassLoader().getResourceAsStream("rippled/rippled-tiny.cfg"));
            final var validatorsTxtPath = "/opt/ripple/validators.txt";
            put(validatorsTxtPath, Main.class.getClassLoader().getResourceAsStream("rippled/validators.txt"));

            session.executeSudo("mkdir -p /var/lib/rippled");
            systemMonitor.chown("/var/lib/rippled");
        }

        session.executeSudo("systemctl restart %s".formatted(serviceName));

        raiseOnEvent(ActionLogEvent.info("Installing rippled completed"));
    }

    private boolean verifyAndAskForContinuation(Response response) {
        var gpgVerify = GpgVerify.parse(response.errorOutput());
        if (fingerprint.equals(gpgVerify.fingerprint())) {
            return true;
        }

        var e = new FingerprintMismatchEvent(gpgVerify.fingerprint(), fingerprint);
        raiseOnFingerprintMismatch(e);
        if (e.accept()) {
            return true;
        }

        return false;
    }

    private void put(String remotePath, InputStream is) throws SshApiException {
        session.executeSudo("touch %s".formatted(remotePath));
        systemMonitor.chown(remotePath);
        session.put(remotePath, is);
    }

    private void createServiceFile(String programName, String serviceName, String execPath, String configPath, String user) throws SshApiException {
        var sb = new StringBuilder();
        sb.append("[Unit]\n" +
                "Description=" + programName + " Daemon\n" +
                "After=network-online.target\n" +
                "Wants=network-online.target\n" +
                "\n" +
                "[Service]\n" +
                "Type=simple\n" +
                "ExecStartPre=+chown -R " + user + " " + execPath + "\n" +
                "ExecStart=" + execPath + " --silent --conf " + configPath + "\n" +
                "Restart=on-failure\n" +
                "User=" + user + "\n" +
                "Group=root\n" +
                "LimitNOFILE=65536\n" +
                "\n" +
                "[Install]\n" +
                "WantedBy=multi-user.target");

        final var serviceFilePath = "/etc/systemd/system/%s".formatted(serviceName);
        session.executeSudo("bash -c \"echo '%s' > %s\"".formatted(sb, serviceFilePath));
    }

    @Override
    public List<XrplBinaryPackage> availableUpdates(ReleaseChannel channel) throws XrplApiException {
        return availableUpdates();
    }

    private List<XrplBinaryPackage> availableUpdates() throws XrplApiException {
        try {
            var d = new UpdateDiscovery();
            return d.list();
        } catch (Exception e) {
            throw new XrplApiException(e);
        }
    }

    public void addActionLogListener(ActionLogListener l) {
        listener.add(l);
    }

    @Override
    public void removeActionLogListener(ActionLogListener l) {
        listener.remove(l);
    }

    private void raiseOnEvent(ActionLogEvent event) {
        for (var l : listener) {
            l.onEvent(event);
        }
    }

    public void addInstallerListener(XrplInstallerListener l) {
        installerListener.add(l);
    }

    @Override
    public String displayName() {
        return "Rippled portable installer";
    }

    private void raiseOnFingerprintMismatch(FingerprintMismatchEvent event) {
        for (var l : installerListener) {
            l.onFingerprintMismatch(event);
        }
    }
}
