package com.radynamics.xrplservermgr.xrpl.subscription;

import java.time.ZonedDateTime;

public class ValidationStreamData {
    private final ZonedDateTime dateTime = ZonedDateTime.now();
    private String ledgerIndex;
    private String validationPublicKey;
    private long signingTime;
    private String masterKey;

    public ZonedDateTime dateTime() {
        return dateTime;
    }

    public String ledgerIndex() {
        return ledgerIndex;
    }

    public void ledgerIndex(String ledgerIndex) {
        this.ledgerIndex = ledgerIndex;
    }

    public String validationPublicKey() {
        return validationPublicKey;
    }

    public void validationPublicKey(String validationPublicKey) {
        this.validationPublicKey = validationPublicKey;
    }

    public long signingTime() {
        return signingTime;
    }

    public void signingTime(long signingTime) {
        this.signingTime = signingTime;
    }

    public String masterKey() {
        return masterKey;
    }

    public void masterKey(String masterKey) {
        this.masterKey = masterKey;
    }

    @Override
    public String toString() {
        return "%s: %s".formatted(ledgerIndex, signingTime);
    }
}
