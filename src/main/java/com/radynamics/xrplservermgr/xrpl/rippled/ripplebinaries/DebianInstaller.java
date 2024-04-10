package com.radynamics.xrplservermgr.xrpl.rippled.ripplebinaries;

import com.radynamics.xrplservermgr.sshapi.SshApiException;
import com.radynamics.xrplservermgr.sshapi.SshSession;
import com.radynamics.xrplservermgr.sshapi.SystemMonitor;
import com.radynamics.xrplservermgr.xrpl.XrplBinaryPackage;
import com.radynamics.xrplservermgr.xrpl.rippled.UpdateChannel;

import java.util.Arrays;
import java.util.Optional;

public class DebianInstaller implements RippledPlatformInstaller {
    private final SshSession session;
    private final SystemMonitor systemMonitor;
    private String systemCodename;

    public DebianInstaller(SshSession session, SystemMonitor systemMonitor) {
        this.session = session;
        this.systemMonitor = systemMonitor;
    }

    public void install(Optional<String> version) throws SshApiException {
        // https://xrpl.org/install-rippled-on-ubuntu.html
        session.executeSudo("apt-get update"); // apt -y update
        session.executeSudo("sudo apt -y install apt-transport-https ca-certificates wget gnupg");

        session.executeSudo("mkdir /usr/local/share/keyrings/");
        session.execute("wget -q -O - \"https://repos.ripple.com/repos/api/gpg/key/public\" | gpg --dearmor > ripple-key.gpg");
        session.executeSudo("mv ripple-key.gpg /usr/local/share/keyrings/");

        session.execute("gpg --import --import-options show-only /usr/local/share/keyrings/ripple-key.gpg");
        changeChannel(UpdateChannel.stable);

        session.executeSudo("apt-get update");
        var versionSuffix = version.map(s -> "=" + s).orElse("");
        session.executeSudo("apt -y install rippled" + versionSuffix);

        session.executeSudo("systemctl start rippled.service");
    }

    private String systemCodenameOrThrow() throws SshApiException {
        if (systemCodename == null) {
            systemCodename = systemMonitor.systemCodename();
            if (!osSupported(systemCodename)) {
                throw new SshApiException("OS %s is not supported.".formatted(systemCodename));
            }
        }
        return systemCodename;
    }

    public void changeChannel(UpdateChannel channel) throws SshApiException {
        var systemCodename = systemCodenameOrThrow();

        // Eg. "bash -c \"echo 'deb [signed-by=/usr/local/share/keyrings/ripple-key.gpg] https://repos.ripple.com/repos/rippled-deb " + systemCodename + " stable' > /etc/apt/sources.list.d/ripple.list\"");
        var sb = new StringBuilder();
        sb.append("bash -c \"echo 'deb [signed-by=/usr/local/share/keyrings/ripple-key.gpg] https://repos.ripple.com/repos/rippled-deb");
        sb.append(" %s".formatted(systemCodename));
        sb.append(" %s".formatted(channel.asString()));
        sb.append("' > /etc/apt/sources.list.d/ripple.list\"");
        session.executeSudo(sb.toString());

        session.executeSudo("apt -y update");
    }

    @Override
    public void update(XrplBinaryPackage pkg) throws SshApiException {
        // "sudo apt-get install rippled=1.12.0-1"
        // Prevent input request "Configuration file /opt/ripple/.../rippled.cfg has changed. What would you like to do about it". Keep currently used.
        update("apt-get --yes --force-yes -o Dpkg::Options::=\"--force-confdef\" -o Dpkg::Options::=\"--force-confold\" install rippled=%s".formatted(pkg.versionText()));
    }

    private void update(String command) throws SshApiException {
        session.executeSudo("apt-get update");
        session.executeSudo(command);
    }

    private boolean osSupported(String systemCodename) {
        // https://xrpl.org/install-rippled-on-ubuntu.html
        return Arrays.asList("bionic", "buster", "bullseye", "jammy").contains(systemCodename);
    }
}
