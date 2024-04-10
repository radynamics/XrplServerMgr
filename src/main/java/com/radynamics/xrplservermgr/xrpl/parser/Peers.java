package com.radynamics.xrplservermgr.xrpl.parser;

import com.google.gson.JsonObject;

public class Peers {
    private final String raw;

    private Peers(String raw) {
        this.raw = raw;
    }

    public static Peers parse(JsonObject json) {
        return new Peers(json.toString());
    }

    public String raw() {
        return raw;
    }
}
