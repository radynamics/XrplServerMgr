package com.radynamics.xrplservermgr.ui.logview;

import com.radynamics.xrplservermgr.sshapi.ProgressListener;
import com.radynamics.xrplservermgr.sshapi.SshApiException;
import com.radynamics.xrplservermgr.xrpl.XrplBinary;

import java.util.ArrayList;

public class RippledProvider implements LogProvider {
    private final XrplBinary xrplBinary;
    private String raw = new String();

    private final ArrayList<ProgressListener> listener = new ArrayList<>();

    public RippledProvider(XrplBinary xrplBinary) {
        this.xrplBinary = xrplBinary;
    }

    @Override
    public String raw() {
        try {
            raw = xrplBinary.serverLog(new ProgressListener() {
                @Override
                public void onProgress(int current, int total) {
                    raiseOnProgress(current, total);
                }

                @Override
                public void onCompleted() {
                    raiseOnCompleted();
                }
            });
            return raw;
        } catch (SshApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public LogStreamProvider createStreamingProvider() {
        var p = new RippledStreamProvider(xrplBinary);
        p.raw(raw);
        return p;
    }

    private void raiseOnProgress(int current, int total) {
        for (var l : listener) {
            l.onProgress(current, total);
        }
    }

    private void raiseOnCompleted() {
        for (var l : listener) {
            l.onCompleted();
        }
    }

    @Override
    public void addProgressListener(ProgressListener l) {
        listener.add(l);
    }
}
