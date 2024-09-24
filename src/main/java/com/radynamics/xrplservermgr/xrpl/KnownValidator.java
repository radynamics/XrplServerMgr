package com.radynamics.xrplservermgr.xrpl;

public class KnownValidator {
    private final String publicKey;
    private final String domain;

    private KnownValidator(String publicKey, String domain) {
        this.publicKey = publicKey;
        this.domain = domain;
    }

    public static KnownValidator of(String publicKey, String domain) {
        return new KnownValidator(publicKey, domain);
    }

    public String publicKey() {
        return publicKey;
    }

    public String domain() {
        return domain;
    }

    @Override
    public String toString() {
        return "%s: %s".formatted(publicKey, domain);
    }
}
