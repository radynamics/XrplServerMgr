package com.radynamics.xrplservermgr.xrpl.parser.config;

import java.util.ArrayList;
import java.util.List;

public class Servers {
    private final List<Server> servers = new ArrayList<>();

    public void add(Server server) {
        this.servers.add(server);
    }

    public List<Server> all() {
        return servers;
    }
}
