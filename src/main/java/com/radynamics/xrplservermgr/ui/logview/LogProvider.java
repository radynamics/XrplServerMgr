package com.radynamics.xrplservermgr.ui.logview;

import com.radynamics.xrplservermgr.sshapi.ProgressListener;

public interface LogProvider {
    String raw();

    LogStreamProvider createStreamingProvider();

    void addProgressListener(ProgressListener l);
}
