package com.radynamics.xrplservermgr.xrpl.xahaud;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public enum UpdateChannel {
    release("release", new String[]{"release"}),
    dev("dev", new String[]{"dev", "candidate"}),
    head("head", new String[]{"head"});

    private final String name;
    private final String[] releaseSuffixes;

    UpdateChannel(String name, String[] releaseSuffixes) {
        this.name = name;
        this.releaseSuffixes = releaseSuffixes;
    }

    public boolean includes(String buildName) {
        return Arrays.stream(releaseSuffixes).anyMatch(o -> buildName.toLowerCase(Locale.ROOT).contains("-%s".formatted(o)));
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
        return List.of(release, dev, head);
    }
}
