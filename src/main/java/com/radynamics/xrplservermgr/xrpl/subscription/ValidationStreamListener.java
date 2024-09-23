package com.radynamics.xrplservermgr.xrpl.subscription;

public interface ValidationStreamListener {
    void onReceive(ValidationStreamData data);
}
