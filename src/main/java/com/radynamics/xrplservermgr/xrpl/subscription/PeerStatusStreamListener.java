package com.radynamics.xrplservermgr.xrpl.subscription;

public interface PeerStatusStreamListener {
    void onReceive(PeerStatusStreamData data);
}
