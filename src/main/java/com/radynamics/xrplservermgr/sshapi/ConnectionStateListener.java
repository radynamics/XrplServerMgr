package com.radynamics.xrplservermgr.sshapi;

public interface ConnectionStateListener {
    void onConnected();

    void onDisconnected();
}
