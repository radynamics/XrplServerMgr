package com.radynamics.xrplservermgr.xrpl.parser.config;

import java.net.URI;
import java.net.URISyntaxException;

public class Server {
    private final String name;
    private String port;
    private String ip;
    private String admin;
    private String protocol;

    public Server(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public String port() {
        return port;
    }

    public void port(String port) {
        this.port = port;
    }

    public String ip() {
        return ip;
    }

    public void ip(String ip) {
        this.ip = ip;
    }

    public String admin() {
        return admin;
    }

    public void admin(String admin) {
        this.admin = admin;
    }

    public String protocol() {
        return protocol;
    }

    public void protocol(String protocol) {
        this.protocol = protocol;
    }

    public URI toURI(String host) throws URISyntaxException {
        return new URI("%s://%s:%s".formatted(protocol, host, Integer.parseInt(port)));
    }

    @Override
    public String toString() {
        return "%s, %s".formatted(name, port);
    }
}
