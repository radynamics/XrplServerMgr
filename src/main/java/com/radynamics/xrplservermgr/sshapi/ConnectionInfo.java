package com.radynamics.xrplservermgr.sshapi;

import com.radynamics.xrplservermgr.db.dto.Server;
import org.apache.commons.lang3.StringUtils;

public class ConnectionInfo {
    private final Server server;
    private PasswordRequest passwordRequest;

    public final static int defaultPort = 22;

    public ConnectionInfo(Server server, PasswordRequest passwordRequest) {
        this.server = server;
        this.passwordRequest = passwordRequest;
    }

    public boolean canConnect() {
        try (var s = new SshSession(() -> null, this)) {
            s.open();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String name() {
        return server.displayText();
    }

    public String host() {
        return server.host();
    }

    public Integer port() {
        return server().port();
    }

    public String username() {
        return server.username();
    }

    public String privateKeyFilePath() {
        return server.keyFile();
    }

    public PasswordRequest passwordRequest() {
        return passwordRequest;
    }

    public void passwordRequest(PasswordRequest passwordRequest) {
        this.passwordRequest = passwordRequest;
    }

    public boolean usesUsernameAndPassword() {
        return StringUtils.isEmpty(privateKeyFilePath());
    }

    public Server server() {
        return server;
    }

    @Override
    public String toString() {
        return "%s (%s):%s, u: %s".formatted(name(), host(), port(), username());
    }
}
