package com.radynamics.xrplservermgr.ui.logview;

public interface LogStreamProvider {
    void start();

    void stop();

    void addChangedListener(ChangedListener l);
}
