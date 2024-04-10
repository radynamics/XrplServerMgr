package com.radynamics.xrplservermgr.sshapi;

public interface ProgressListener {
    void onProgress(int current, int total);

    void onCompleted();
}
