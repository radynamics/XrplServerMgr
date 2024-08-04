package com.radynamics.xrplservermgr.xrpl.parser;

import com.radynamics.xrplservermgr.xrpl.parser.config.ConfigReader;
import com.radynamics.xrplservermgr.xrpl.parser.config.Line;
import com.radynamics.xrplservermgr.xrpl.parser.config.Server;
import com.radynamics.xrplservermgr.xrpl.parser.config.Servers;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class ConfigCfg {
    private final static Logger log = LogManager.getLogger(ConfigCfg.class);

    private final String raw;
    private String debugLogFile;
    private String databasePath;
    private String validatorsTxtPath;
    private String networkId;
    private Servers servers;

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
        config.servers = readServers(parsed);
        return config;
    }

    private static Optional<String> firstInSection(ConfigReader parsed, String section) {
        var sections = parsed.sections(section);
        return sections.isEmpty() || sections.get(0).lines().isEmpty() ? Optional.empty() : Optional.of(sections.get(0).lines().get(0).value());
    }

    private static Servers readServers(ConfigReader parsed) {
        var sections = parsed.sections("server");
        if (sections.size() != 1) {
            log.warn("Expected section [server], but found %s entries.".formatted(sections.size()));
            return null;
        }

        var serverSection = sections.get(0);
        if (serverSection.lines().isEmpty()) {
            return null;
        }

        var servers = new Servers();
        for (var l : serverSection.lines()) {
            servers.add(readServer(parsed, l.value()));
        }
        return servers;
    }

    private static Server readServer(ConfigReader parsed, String name) {
        var sections = parsed.sections(name);
        if (sections.size() != 1) {
            log.warn("Expected server section %s, but found %s entries.".formatted(name, sections.size()));
            return null;
        }

        var s = new Server(name);
        var port = readPair(sections.get(0).lines(), "port");
        if (port != null) {
            s.port(port.getValue());
        }
        var ip = readPair(sections.get(0).lines(), "ip");
        if (ip != null) {
            s.ip(ip.getValue());
        }
        var admin = readPair(sections.get(0).lines(), "admin");
        if (admin != null) {
            s.admin(admin.getValue());
        }
        var protocol = readPair(sections.get(0).lines(), "protocol");
        if (protocol != null) {
            s.protocol(protocol.getValue());
        }
        return s;
    }

    private static Pair<String, String> readPair(List<Line> lines, String name) {
        var line = lines.stream().filter(o -> o.value().startsWith(name)).findFirst();
        if (line.isEmpty()) {
            return null;
        }

        var values = line.get().value().split("=");
        if (values.length != 2) {
            log.warn("Error parsing line '%s'. Key without value.".formatted(line.get().value()));
            return null;
        }
        return new ImmutablePair<>(values[0].trim(), values[1].trim());
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

    public Servers server() {
        return servers;
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
