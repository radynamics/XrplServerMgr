package com.radynamics.xrplservermgr.sshapi.parser;

import java.util.Scanner;

public class GpgVerify {
    private String fingerprint;

    public static GpgVerify parse(String lines) {
        var o = new GpgVerify();
        var scanner = new Scanner(lines);
        while (scanner.hasNextLine()) {
            var line = scanner.nextLine();
            final String prefixFingerprint = "Primary key fingerprint: ";
            if (line.startsWith(prefixFingerprint)) {
                o.fingerprint = line.substring(prefixFingerprint.length());
            }
        }
        return o;
    }

    public String fingerprint() {
        return fingerprint;
    }
}
