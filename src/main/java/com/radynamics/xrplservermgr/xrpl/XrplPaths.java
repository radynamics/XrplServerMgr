package com.radynamics.xrplservermgr.xrpl;

import java.util.Optional;

public class XrplPaths {
    private final String installPath;
    private final String configPath;
    private final String debugLogPath;
    private final Optional<String> databasePath;
    private final Optional<String> validatorPath;

    public XrplPaths(String installPath, String configPath, String debugLogPath, String databasePath, String validatorPath) {
        this.installPath = installPath;
        this.configPath = configPath;
        this.debugLogPath = debugLogPath;
        this.databasePath = databasePath == null ? Optional.empty() : Optional.of(databasePath);
        this.validatorPath = validatorPath == null ? Optional.empty() : Optional.of(validatorPath);
    }

    public String installPath() {
        return installPath;
    }

    public String configPath() {
        return configPath;
    }

    public String debugLogPath() {
        return debugLogPath;
    }

    public Optional<String> databasePath() {
        return databasePath;
    }

    public Optional<String> validatorPath() {
        return validatorPath;
    }
}
