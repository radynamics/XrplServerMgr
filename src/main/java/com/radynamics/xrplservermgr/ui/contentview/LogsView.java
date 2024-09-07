package com.radynamics.xrplservermgr.ui.contentview;

import com.radynamics.xrplservermgr.ui.logview.LogProvider;
import com.radynamics.xrplservermgr.ui.logview.LogViewerView;

import javax.swing.*;

public class LogsView extends ContentView {
    private final LogViewerView _view;

    public LogsView(JFrame parent, LogProvider provider) {
        super(parent);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        _view = new LogViewerView(parent, provider);
        add(_view);
        if (provider.createStreamingProvider() == null) {
            _view.reload();
        } else {
            _view.startStreaming();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        _view.setRefreshEnabled(enabled);
    }

    @Override
    public String tabText() {
        return "Logs";
    }

    @Override
    public void close() {
    }
}
