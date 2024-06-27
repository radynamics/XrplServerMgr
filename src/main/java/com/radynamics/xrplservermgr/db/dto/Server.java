package com.radynamics.xrplservermgr.db.dto;

import java.util.UUID;

public class Server {
    private Integer id = 0;
    private UUID uuid = UUID.randomUUID();
    private String displayText;
    private String host;
    private Integer port;
    private String username;
    private String keyFile;

    public static Server create(String displayText, String host, int port, String username) {
        return create(displayText, host, port, username, null);
    }

    public static Server create(String displayText, String host, int port, String username, String keyFile) {
        var s = new Server();
        s.displayText(displayText);
        s.host(host);
        s.port(port);
        s.username(username);
        s.keyFile(keyFile);
        return s;
    }

    public Integer id() {
        return id;
    }

    public void id(Integer id) {
        this.id = id;
    }

    public UUID uuid() {
        return uuid;
    }

    public void uuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String displayText() {
        return displayText;
    }

    public void displayText(String displayText) {
        this.displayText = displayText;
    }

    public String host() {
        return host;
    }

    public void host(String host) {
        this.host = host;
    }

    public Integer port() {
        return port;
    }

    public void port(Integer port) {
        this.port = port;
    }

    public String username() {
        return username;
    }

    public void username(String username) {
        this.username = username;
    }

    public String keyFile() {
        return keyFile;
    }

    public void keyFile(String keyFile) {
        this.keyFile = keyFile;
    }

    public boolean isNew() {
        return id == 0;
    }

    @Override
    public String toString() {
        return "Id: %s, host: %s".formatted(id, host);
    }
}
