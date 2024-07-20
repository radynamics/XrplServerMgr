package com.radynamics.xrplservermgr.xrpl;

public interface XrplInstallerListener {
    void onFingerprintMismatch(FingerprintMismatchEvent event);

    void onCompleted();
}
