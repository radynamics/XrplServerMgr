package com.radynamics.xrplservermgr.xrpl;

public class XrplBinaryPackage {
    private final String name;
    private final String versionText;

    public XrplBinaryPackage(String name, String versionText) {
        this.name = name;
        this.versionText = versionText;
    }

    public String versionText() {
        return versionText;
    }

    @Override
    public String toString() {
        return "version: %s".formatted(versionText);
    }
}
