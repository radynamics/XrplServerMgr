package com.radynamics.xrplservermgr.xrpl.parser;

import com.vdurmont.semver4j.Semver;
import org.apache.commons.lang3.StringUtils;

public class Feature {
    private final String hash;
    private final String name;
    private final boolean enabled;
    private final boolean supported;
    private Boolean vetoed;
    private boolean obsolete;
    private Semver versionIntroduced;

    private Feature(String hash, String name, boolean enabled, boolean supported) {
        this.hash = hash;
        this.name = name;
        this.enabled = enabled;
        this.supported = supported;
    }

    public static Feature of(String hash, String name, boolean enabled, boolean supported) {
        return new Feature(hash, name, enabled, supported);
    }

    public String hash() {
        return hash;
    }

    public String name() {
        return name;
    }

    public String hashOrName() {
        return StringUtils.isEmpty(hash()) ? name() : hash();
    }

    public String nameOrHash() {
        return StringUtils.isEmpty(name()) ? hash() : name();
    }

    public boolean enabled() {
        return enabled;
    }

    public boolean supported() {
        return supported;
    }

    public Boolean vetoed() {
        return vetoed;
    }

    public void vetoed(Boolean vetoed) {
        this.vetoed = vetoed;
    }

    public void obsolete(boolean obsolete) {
        this.obsolete = obsolete;
    }

    public boolean obsolete() {
        return obsolete;
    }

    public Semver versionIntroduced() {
        return versionIntroduced;
    }

    public void versionIntroduced(Semver versionIntroduced) {
        this.versionIntroduced = versionIntroduced;
    }

    public boolean votingActive() {
        return !enabled() && !obsolete();
    }

    @Override
    public String toString() {
        return "%s: vetoed: %s".formatted(name, vetoed);
    }
}
