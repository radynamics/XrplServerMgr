package com.radynamics.xrplservermgr.ui.contentview;

import com.radynamics.xrplservermgr.ui.logview.LogProvider;
import com.radynamics.xrplservermgr.ui.logview.LogViewerView;

import javax.swing.*;

public class LogsView extends ContentView {
    public LogsView(JFrame parent, LogProvider provider) {
        super(parent);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        var view = new LogViewerView(parent, provider);
        add(view);
        view.reload();
    }

    @Override
    public String tabText() {
        return "Logs";
    }

    @Override
    public void close() {
    }
}
