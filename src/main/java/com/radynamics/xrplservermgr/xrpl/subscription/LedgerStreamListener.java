package com.radynamics.xrplservermgr.xrpl.subscription;

public interface LedgerStreamListener {
    void onReceive(LedgerStreamData data);
}
