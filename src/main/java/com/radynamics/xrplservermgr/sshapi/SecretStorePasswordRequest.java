package com.radynamics.xrplservermgr.sshapi;

import com.radynamics.xrplservermgr.db.dto.Server;
import com.radynamics.xrplservermgr.ui.PasswordInput;
import com.radynamics.xrplservermgr.utils.SecretStore;

import java.awt.*;

public class SecretStorePasswordRequest {
    private final Component parentComponent;
    private final SecretStore secretStore;
    private final Server server;

    public SecretStorePasswordRequest(Component parentComponent, SecretStore secretStore, Server server) {
        this.parentComponent = parentComponent;
        this.secretStore = secretStore;
        this.server = server;
    }

    public char[] password() {
        var password = secretStore.connectionPassword(server.uuid());
        if (password == null) {
            return PasswordInput.connectionPassword(parentComponent, server.username());
        } else {
            return password;
        }
    }
}
