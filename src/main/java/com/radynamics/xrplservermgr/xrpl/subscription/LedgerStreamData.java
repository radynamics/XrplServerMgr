package com.radynamics.xrplservermgr.xrpl.subscription;

public class LedgerStreamData {
    private String type;
    private long ledgerIndex;
    private long ledgerTime;
    private String ledgerHash;
    private int txnCount;

    public String type() {
        return type;
    }

    public void type(String type) {
        this.type = type;
    }

    public long ledgerIndex() {
        return ledgerIndex;
    }

    public void ledgerIndex(long ledgerIndex) {
        this.ledgerIndex = ledgerIndex;
    }

    public long ledgerTime() {
        return ledgerTime;
    }

    public void ledgerTime(long ledgerTime) {
        this.ledgerTime = ledgerTime;
    }

    public String ledgerHash() {
        return ledgerHash;
    }

    public void ledgerHash(String ledgerHash) {
        this.ledgerHash = ledgerHash;
    }

    public int txnCount() {
        return txnCount;
    }

    public void txnCount(int txnCount) {
        this.txnCount = txnCount;
    }

    @Override
    public String toString() {
        return "%s: %s".formatted(ledgerIndex, ledgerHash);
    }
}
