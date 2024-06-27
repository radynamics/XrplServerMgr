package com.radynamics.xrplservermgr.utils;

import com.microsoft.credentialstorage.StorageProvider;
import com.microsoft.credentialstorage.model.StoredCredential;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class SecretStore {
    private final static Logger log = LogManager.getLogger(SecretStore.class);

    private com.microsoft.credentialstorage.SecretStore<StoredCredential> store;
    private static final String prefix = "XrplServerMgr";

    public char[] connectionPassword(UUID uuid) {
        return get(connectionKey(uuid));
    }

    public void connectionPassword(UUID uuid, char[] password) {
        set(connectionKey(uuid), password);
    }

    public void connectionPasswordDelete(UUID uuid) {
        delete(connectionKey(uuid));
    }

    private static String connectionKey(UUID uuid) {
        return "%s_conn_%s".formatted(prefix, uuid.toString());
    }

    private char[] get(String key) {
        if (!initStore()) {
            return null;
        }

        var credentials = store.get(key);
        return credentials == null ? null : credentials.getPassword();
    }

    private void set(String key, char[] value) {
        if (!initStore()) {
            return;
        }

        // Workaround: An empty username is read as null, causing a null reference exception. (https://github.com/microsoft/credential-secure-storage-for-java/issues/10)
        store.add(key, new StoredCredential("dummy", value));
    }

    private void delete(String key) {
        if (!initStore()) {
            return;
        }

        store.delete(key);
    }

    private boolean initStore() {
        if (store != null) {
            return true;
        }

        store = StorageProvider.getCredentialStorage(true, StorageProvider.SecureOption.REQUIRED);
        if (store == null) {
            log.error("No secure credential storage available.");
            return false;
        }
        return true;
    }
}
