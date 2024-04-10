package com.radynamics.xrplservermgr.xrpl.rippled;

import java.util.List;
import java.util.Optional;

public enum UpdateChannel {
    stable("stable"),
    unstable("unstable"),
    nightly("nightly");

    private final String name;

    UpdateChannel(String name) {
        this.name = name;
    }

    public String asString() {
        return name;
    }

    public static Optional<UpdateChannel> of(String name) {
        for (var c : all()) {
            if (c.name.equals(name)) {
                return Optional.of(c);
            }
        }
        return Optional.empty();
    }

    public static List<UpdateChannel> all() {
        return List.of(stable, unstable, nightly);
    }
}
