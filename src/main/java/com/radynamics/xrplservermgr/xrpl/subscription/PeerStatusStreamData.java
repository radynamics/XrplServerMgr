package com.radynamics.xrplservermgr.xrpl.subscription;

import java.time.ZonedDateTime;

public class PeerStatusStreamData {
    private final ZonedDateTime dateTime = ZonedDateTime.now();
    private String action;
    private long date;
    private long ledgerIndex;
    private String ledgerHash;
    private long ledgerIndexMin;
    private long ledgerIndexMax;

    public ZonedDateTime dateTime() {
        return dateTime;
    }

    public String action() {
        return action;
    }

    public void action(String action) {
        this.action = action;
    }

    public long date() {
        return date;
    }

    public void date(long date) {
        this.date = date;
    }

    public long ledgerIndex() {
        return ledgerIndex;
    }

    public void ledgerIndex(long ledgerIndex) {
        this.ledgerIndex = ledgerIndex;
    }

    public String ledgerHash() {
        return ledgerHash;
    }

    public void ledgerHash(String ledgerHash) {
        this.ledgerHash = ledgerHash;
    }

    public long ledgerIndexMin() {
        return ledgerIndexMin;
    }

    public void ledgerIndexMin(long ledgerIndexMin) {
        this.ledgerIndexMin = ledgerIndexMin;
    }

    public long ledgerIndexMax() {
        return ledgerIndexMax;
    }

    public void ledgerIndexMax(long ledgerIndexMax) {
        this.ledgerIndexMax = ledgerIndexMax;
    }

    @Override
    public String toString() {
        return "%s: %s".formatted(ledgerIndex, ledgerHash);
    }
}
