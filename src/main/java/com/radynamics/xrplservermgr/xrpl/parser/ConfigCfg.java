package com.radynamics.xrplservermgr.xrpl.parser;

import com.radynamics.xrplservermgr.xrpl.parser.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class ConfigCfg {
    private final static Logger log = LogManager.getLogger(ConfigCfg.class);

    private final String raw;
    private String debugLogFile;
    private String databasePath;
    private String validatorsTxtPath;
    private String networkId;

    private ConfigCfg(String raw) {
        this.raw = raw;
    }

    public static ConfigCfg parse(String raw) {
        var parsed = new ConfigReader();
        parsed.read(raw);

        var config = new ConfigCfg(raw);
        config.debugLogFile = firstInSection(parsed, "debug_logfile").orElse(null);
        config.databasePath = firstInSection(parsed, "database_path").orElse(null);
        config.validatorsTxtPath = firstInSection(parsed, "validators_file").orElse(null);
        config.networkId = firstInSection(parsed, "network_id").orElse("main");
        return config;
    }

    private static Optional<String> firstInSection(ConfigReader parsed, String section) {
        var sections = parsed.sections(section);
        return sections.isEmpty() || sections.get(0).lines().isEmpty() ? Optional.empty() : Optional.of(sections.get(0).lines().get(0).value());
    }

    public Optional<String> debugLogFile() {
        return debugLogFile == null ? Optional.empty() : Optional.of(debugLogFile);
    }

    public Optional<String> databasePath() {
        return databasePath == null ? Optional.empty() : Optional.of(databasePath);
    }

    public Optional<String> validatorsTxtFile() {
        return validatorsTxtPath == null ? Optional.empty() : Optional.of(validatorsTxtPath);
    }

    public String networkId() {
        return networkId;
    }

    public static ConfigCfg parse(InputStream in) throws IOException {
        return parse(new String(in.readAllBytes(), StandardCharsets.UTF_8));
    }

    public InputStream asInputStream() {
        return new ByteArrayInputStream(raw.getBytes());
    }

    public void saveAs(String outputPath) throws IOException {
        try (var os = new FileOutputStream(outputPath)) {
            os.write(raw().getBytes(StandardCharsets.UTF_8));
        }
    }

    public String raw() {
        return raw;
    }

    public boolean sameAs(ConfigCfg other) {
        return raw().equals(other.raw);
    }
}
