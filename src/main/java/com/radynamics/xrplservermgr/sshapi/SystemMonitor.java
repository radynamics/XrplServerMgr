package com.radynamics.xrplservermgr.sshapi;

import com.radynamics.xrplservermgr.datasize.Size;
import com.radynamics.xrplservermgr.datasize.SizeUnit;
import com.radynamics.xrplservermgr.sshapi.os.Debian;
import com.radynamics.xrplservermgr.sshapi.os.PlatformSpecific;
import com.radynamics.xrplservermgr.sshapi.os.RedHat;
import com.radynamics.xrplservermgr.sshapi.parser.*;
import com.radynamics.xrplservermgr.xrpl.XrplBinaryPackage;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Supplier;

public class SystemMonitor {
    private final static Logger log = LogManager.getLogger(SystemMonitor.class);

    private final SshSession session;
    private final ArrayList<ActionLogListener> listener = new ArrayList<>();
    private Platform platform;

    public SystemMonitor(SshSession session) {
        this.session = session;
    }

    public Size virtualMemory(String processName) throws SshApiException {
        // -eg tries to return usage in gibibytes
        var response = session.execute("top -bn1 -eg | grep " + processName).asString();
        if (StringUtils.isEmpty(response)) {
            // True, if service could not be found.
            return null;
        }
        return Top.parse(response, SizeUnit.GIBIBYTES).virtualMemory();
    }

    public List<DiskUsage> diskUsage() throws SshApiException {
        return DiskUsage.parse(session.execute("df -kP / | awk '{print $1\",\"$2\",\"$3\",\"$4\",\"$5\",\"$6\",\"$7}'").asString(), ',');
    }

    public List<Memory> memory() throws SshApiException {
        return Memory.parse(session.execute("free | awk '{print $1\",\"$2\",\"$3\",\"$4\",\"$5\",\"$6\",\"$7}'").asString(), ',');
    }

    public Uptime loadAverage() throws SshApiException {
        return Uptime.parse(session.execute("uptime").asString());
    }

    public boolean installed(String packageName) throws SshApiException {
        return platformSpecific().installed(packageName);
    }

    public List<XrplBinaryPackage> availableUpdates(String packageName) throws SshApiException {
        return platformSpecific().available(packageName);
    }

    private PlatformSpecific platformSpecific() throws SshApiException {
        switch (platform()) {
            case Debian:
                return new Debian(session);
            case Fedora:
                return new RedHat(session);
            default:
                throw new SshApiException("Platform is not supported.");
        }
    }

    public boolean installerRunning() throws SshApiException {
        var response = session.execute("ps -C apt-get,dpkg >/dev/null && echo \"true\" || echo \"false\"");
        return response.asString().contains("true");
    }

    public void startService(String processName) throws SshApiException {
        var response = session.executeSudo("systemctl start %s".formatted(processName));
        if (!systemctlSuccess(response)) {
            return;
        }
        if (waitUntilRunningOrThrow(processName)) {
            raiseOnEvent(ActionLogEvent.info("%s started".formatted(processName)));
        }
    }

    private static boolean systemctlSuccess(Response response) {
        return response.success() && response.exitStatus() == 0;
    }

    public void stopService(String processName) throws SshApiException {
        var response = session.executeSudo("systemctl stop %s".formatted(processName));
        if (!systemctlSuccess(response)) {
            return;
        }
        raiseOnEvent(ActionLogEvent.info("%s stopped".formatted(processName)));
    }

    public void restartService(String processName, Supplier<Boolean> restart) throws SshApiException {
        if (!restart.get()) {
            return;
        }

        var response = session.executeSudo("systemctl restart %s".formatted(processName));
        if (!systemctlSuccess(response)) {
            return;
        }
        if (waitUntilRunningOrThrow(processName)) {
            raiseOnEvent(ActionLogEvent.info("%s restarted".formatted(processName)));
        }
    }

    public Boolean existsService(String processName) throws SshApiException {
        var response = session.execute("systemctl list-unit-files %s.service &>/dev/null && echo \"true\" || echo \"false\"".formatted(processName));
        return response.asString().contains("true");
    }

    private boolean waitUntilRunningOrThrow(String processName) throws SshApiException {
        var timeout = 1000 * 10;
        while (timeout > 0) {
            try {
                var wait = 500;
                Thread.sleep(wait);
                timeout -= wait;
            } catch (InterruptedException ignored) {
            }
            if (running(processName)) {
                return true;
            }
        }

        throw new SshApiException("Service %s restart timeout.".formatted(processName));
    }

    private boolean running(String processName) throws SshApiException {
        return virtualMemory(processName) == null;
    }

    public String systemCodename() throws SshApiException {
        final String prefix = "Codename:";
        var response = session.execute("lsb_release -a | grep \"%s\"".formatted(prefix)).asString();
        return response.substring(prefix.length()).trim();
    }

    public boolean existsPath(String path) throws SshApiException {
        var response = session.execute("test -e %s && echo 1 || echo 0".formatted(path)).asString();
        return "1".equals(response.substring(0, 1));
    }

    public List<String> find(String name, List<String> locations) throws SshApiException {
        var paramLocations = String.join(" ", locations);
        var response = session.execute("find %s -name \"%s\"".formatted(paramLocations, name)).asString();

        var result = new ArrayList<String>();
        var scanner = new Scanner(response);
        while (scanner.hasNextLine()) {
            result.add(scanner.nextLine());
        }
        return result;
    }

    public void remove(String path, boolean recursive) throws SshApiException {
        final var command = "rm %s %s".formatted(recursive ? "-Rf" : "", path);
        var response = session.executeSudo(command);
        if (!response.success()) {
            throw new SshApiException("Error executing %s".formatted(command));
        }
    }

    public synchronized Platform platform() throws SshApiException {
        if (platform == null) {
            var response = session.execute("grep -E '^(ID_LIKE)=' /etc/os-release");
            platform = PlatformParser.parse(response.asString());
        }
        return platform;
    }

    public boolean canSudo(char[] sudoPassword) {
        try {
            var response = session.execute("sudo -S -v", sudoPassword, Duration.ofSeconds(2));
            return response.success() && StringUtils.isEmpty(response.asString());
        } catch (SshApiException e) {
            raiseOnEvent(ActionLogEvent.error(e.getMessage()));
            return false;
        }
    }

    public void chown(String path) throws SshApiException {
        // TODO: Changes the system. IF this is really needed, ask user if that's ok.
        session.executeSudo("chown -R %s:%s %s".formatted(session.user(), session.user(), path));
    }

    public Long fileSizeBytes(String remotePath) throws SshApiException {
        var response = session.execute("du -b %s".formatted(remotePath));
        // Eg. "164      /opt/xahaud/log/debug.log"
        var value = response.asString().replace(remotePath, "").trim();
        return Long.parseLong(value);
    }

    public LocalDateTime serverStartingTime() throws SshApiException {
        var response = session.execute("uptime -s");
        // Eg. "2019-05-31 11:49:17"
        return LocalDateTime.parse(response.asString().replace("\n", ""), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public void addActionLogListener(ActionLogListener l) {
        listener.add(l);
    }

    private void raiseOnEvent(ActionLogEvent event) {
        for (var l : listener) {
            l.onEvent(event);
        }
    }
}
