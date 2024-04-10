package com.radynamics.xrplservermgr.xrpl;

public class ReleaseChannel {
    private final String name;

    public ReleaseChannel(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public boolean stable() {
        return name.equals(com.radynamics.xrplservermgr.xrpl.rippled.UpdateChannel.stable.name())
                || name.equals(com.radynamics.xrplservermgr.xrpl.xahaud.UpdateChannel.release.name());
    }

    @Override
    public String toString() {
        return name;
    }
}
