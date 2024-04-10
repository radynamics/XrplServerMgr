package com.radynamics.xrplservermgr.sshapi;

import com.vdurmont.semver4j.Semver;

public class Amendment {
    private String hash;
    private String name;
    private Semver introduced;

    public static Amendment of(String hash, String name, Semver introduced) {
        var o = new Amendment();
        o.hash = hash;
        o.name = name;
        o.introduced = introduced;
        return o;
    }

    public String hash() {
        return hash;
    }

    public String name() {
        return name;
    }

    public Semver introduced() {
        return introduced;
    }

    @Override
    public String toString() {
        return "%s; %s".formatted(name, introduced);
    }
}
