package com.radynamics.xrplservermgr.ui.contentview;

import com.radynamics.xrplservermgr.sshapi.SshApiException;
import com.radynamics.xrplservermgr.ui.streamview.StreamView;
import com.radynamics.xrplservermgr.xrpl.parser.config.Server;

import javax.swing.*;
import java.net.URI;

public class StreamsView extends ContentView {
    private final StreamView view;

    public StreamsView(JFrame parent) {
        super(parent);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        view = new StreamView();
        add(view);
        view.addExceptionListener(e -> outputError(e.getMessage()));
    }

    @Override
    protected void refresh() {
        Server wsServer = null;
        Server wsAdminServer = null;
        try {
            var servers = xrplBinary.config().server();
            wsServer = servers.all().stream()
                    .filter(o -> o.name().contains("port_ws_public"))
                    .findFirst().orElse(null);
            wsAdminServer = servers.all().stream()
                    .filter(o -> o.name().contains("port_ws_admin"))
                    .findFirst().orElse(null);
        } catch (SshApiException e) {
            outputError(e.getMessage());
        }

        if (wsServer == null) {
            outputError("There is no server with a name 'port_ws_public' defined in xrpl config in [server] section.");
            view.setEnabled(false);
            return;
        }
        if (wsAdminServer == null) {
            outputWarn("There is no server with a name 'port_ws_admin' defined in xrpl config in [server] section.");
        }

        view.stopAll();
        try {
            view.publicEndpoint(new URI("%s://%s:%s".formatted(wsServer.protocol(), session.host(), Integer.parseInt(wsServer.port()))));
            view.adminEndpoint(new URI("%s://%s:%s".formatted(wsAdminServer.protocol(), session.host(), Integer.parseInt(wsAdminServer.port()))));
            view.knownValidatorRepo(xrplBinary.knownValidatorRepo());
            view.startListening();
        } catch (Exception e) {
            outputError(e.getMessage());
        }
    }

    @Override
    public String tabText() {
        return "Streams";
    }

    @Override
    public void close() {
        view.stopAll();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        view.setEnabled(enabled);
    }
}
