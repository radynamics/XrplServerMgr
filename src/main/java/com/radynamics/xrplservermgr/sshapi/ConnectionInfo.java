package com.radynamics.xrplservermgr.sshapi;

public class ConnectionInfo {
    private final String name;
    private final String host;
    private final int port;
    private final String username;
    private final String password;

    public final static int defaultPort = 22;

    public ConnectionInfo(String name, String host, int port, String username, String password) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public String name() {
        return name;
    }

    public String host() {
        return host;
    }

    public Integer port() {
        return port;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    @Override
    public String toString() {
        return "%s (%s):%s, u: %s".formatted(name, host, port, username);
    }
}
