package com.radynamics.xrplservermgr.ui;

import com.radynamics.xrplservermgr.sshapi.ConnectionInfo;

public interface ServerPanelListener {
    void onConnect(ConnectionInfo conn);
}
