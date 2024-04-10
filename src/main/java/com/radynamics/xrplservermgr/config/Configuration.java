package com.radynamics.xrplservermgr.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

public class Configuration {
    private static Path root() {
        var home = SystemUtils.IS_OS_WINDOWS ? System.getenv("APPDATA") : System.getProperty("user.home");
        return Paths.get(home, ".xrplServerMgr");
    }

    private static Path backup() {
        return Paths.get(root().toString(), "backup");
    }

    public static File createNewRippleCfgBackupFile(String host) throws IOException {
        return createNewBackupFile("%s_rippled".formatted(host), ".cfg");
    }

    public static File createNewValidatorsTxtBackupFile(String host) throws IOException {
        return createNewBackupFile("%s_validators".formatted(host), ".txt");
    }

    private static File createNewBackupFile(String namePart, String ext) throws IOException {
        for (var i = 0; i < 100; i++) {
            var candidate = createBackupFile("%s_%s%s".formatted(namePart, StringUtils.leftPad(String.valueOf(i), 3, "0"), ext));
            if (!candidate.exists()) {
                return candidate;
            }
        }
        throw new RuntimeException("Could not find a non existent file name %s%s".formatted(namePart, ext));
    }

    private static File createBackupFile(String name) throws IOException {
        Files.createDirectories(backup());
        return Paths.get(backup().toString(), name).toFile();
    }

    public static void removeOldBackups() {
        var dir = backup().toFile();
        var files = dir.listFiles();
        if (files == null) {
            return;
        }

        final long OUTDATED_DAYS = 30;
        for (var file : files) {
            var diff = new Date().getTime() - file.lastModified();
            if (diff > OUTDATED_DAYS * 24 * 60 * 60 * 1000) {
                file.delete();
            }
        }
    }
}
