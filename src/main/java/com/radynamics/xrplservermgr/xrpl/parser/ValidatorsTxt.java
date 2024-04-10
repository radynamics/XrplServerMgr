package com.radynamics.xrplservermgr.xrpl.parser;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ValidatorsTxt {
    private final String raw;

    private ValidatorsTxt(String raw) {
        this.raw = raw;
    }

    public static ValidatorsTxt parse(String raw) {
        return new ValidatorsTxt(raw);
    }

    public void saveAs(String outputPath) throws IOException {
        try (var os = new FileOutputStream(outputPath)) {
            os.write(raw().getBytes(StandardCharsets.UTF_8));
        }
    }

    public String raw() {
        return raw;
    }

    public InputStream asInputStream() {
        return new ByteArrayInputStream(raw.getBytes());
    }

    public boolean sameAs(ValidatorsTxt other) {
        return raw().equals(other.raw);
    }
}
