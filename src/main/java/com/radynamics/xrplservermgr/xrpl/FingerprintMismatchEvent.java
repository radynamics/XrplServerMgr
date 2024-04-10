package com.radynamics.xrplservermgr.xrpl;

public class FingerprintMismatchEvent {
    private final String actualFingerprint;
    private final String expectedFingerprint;
    private boolean accept;

    public FingerprintMismatchEvent(String actualFingerprint, String expectedFingerprint) {
        this.actualFingerprint = actualFingerprint;
        this.expectedFingerprint = expectedFingerprint;
    }

    public String actualFingerprint() {
        return actualFingerprint;
    }

    public String expectedFingerprint() {
        return expectedFingerprint;
    }

    public boolean accept() {
        return accept;
    }

    public void accept(boolean accept) {
        this.accept = accept;
    }
}
