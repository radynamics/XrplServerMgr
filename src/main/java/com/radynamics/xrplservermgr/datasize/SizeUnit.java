package com.radynamics.xrplservermgr.datasize;

public enum SizeUnit {
    KILOBYTES("KB", "kilobytes"),
    MEGABYTES("MB", "megabytes"),
    GIGABYTES("GB", "gigabytes"),

    KIBIBYTES("KiB", "kibibytes"),
    MEBIBYTES("MiB", "mebibytes"),
    GIBIBYTES("GiB", "gibibytes");

    private final String shortName;
    private final String longName;

    private SizeUnit(String shortName, String longName) {
        this.shortName = shortName;
        this.longName = longName;
    }

    public String shortName() {
        return shortName;
    }

    public String longName() {
        return longName;
    }

    @Override
    public String toString() {
        return "%s (%s)".formatted(shortName, longName);
    }
}
