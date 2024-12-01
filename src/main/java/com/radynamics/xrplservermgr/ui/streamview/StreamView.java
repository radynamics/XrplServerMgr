package com.radynamics.xrplservermgr.ui.streamview;

import com.radynamics.xrplservermgr.xrpl.KnownValidatorRepo;
import com.radynamics.xrplservermgr.xrpl.parser.config.Server;
import com.radynamics.xrplservermgr.xrpl.rippled.ValidatorRepo;
import com.radynamics.xrplservermgr.xrpl.subscription.*;

import javax.swing.*;
import java.awt.*;
import java.beans.ExceptionListener;
import java.util.ArrayList;

public class StreamView extends JPanel {
    private final JScrollPane scrollPane;
    private final JButton cmdStartStop;
    private final JComboBox<Stream> cbo;
    private SubscriptionStreamSession client;
    private final ArrayList<ExceptionListener> listener = new ArrayList<>();
    private final ArrayList<StreamListener> streams = new ArrayList<>();
    private String host;
    private Server publicEndpoint;
    private Server adminEndpoint;
    private KnownValidatorRepo knownValidatorRepo = new ValidatorRepo();

    public StreamView() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        var toolbar = new JToolBar();
        add(toolbar);
        toolbar.setAlignmentX(Component.LEFT_ALIGNMENT);
        {
            cbo = new JComboBox<>();
            cbo.setModel(new DefaultComboBoxModel<>(Stream.all().toArray(new Stream[0])));
            cbo.setPrototypeDisplayValue(Stream.Validations);
            cbo.setMaximumSize(cbo.getPreferredSize());
            toolbar.add(cbo);
        }
        {
            cmdStartStop = new JButton("start");
            toolbar.add(cmdStartStop);
            cmdStartStop.addActionListener(e -> onStartStopClick());
        }

        scrollPane = new JScrollPane();
        add(scrollPane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void onStartStopClick() {
        try {
            if (isRunning()) {
                stopAll();
            } else {
                startListening();
            }
            refreshInputControlStates();
        } catch (Exception e) {
            raiseOnException(e);
        }
    }

    private boolean isRunning() {
        return !streams.isEmpty();
    }

    public void refreshInputControlStates() {
        var isRunning = isRunning();
        cbo.setEnabled(!isRunning);
        cmdStartStop.setText(isRunning ? "stop" : "start");
    }

    public void startListening() {
        if (scrollPane.getViewport().getView() != null) {
            scrollPane.setViewportView(null);
        }

        Presentation v;
        StreamListener l;
        var selected = (Stream) cbo.getSelectedItem();
        var endpoint = publicEndpoint;
        switch (selected) {
            case Validations:
                var vst = new ValidationStreamTable(knownValidatorRepo);
                v = vst;
                var vs = new ValidationStream();
                vs.addListener(vst);
                l = vs;
                break;
            case Ledger:
                var lst = new LedgerStreamTable();
                v = lst;
                var ls = new LedgerStream();
                ls.addListener(lst);
                l = ls;
                break;
            case PeerStatus:
                var psst = new PeerStatusStreamTable();
                v = psst;
                var pss = new PeerStatusStream();
                pss.addListener(psst);
                l = pss;
                endpoint = adminEndpoint;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + selected);
        }

        scrollPane.add(v.view());
        scrollPane.getViewport().add(v.view());

        try {
            client = new SubscriptionStreamSession(endpoint.toURI(host), l);
            client.subscribe();
        } catch (Exception e) {
            throw new RuntimeException("%s. Ensure ip/port for [server].[%s] in xrpl config is correct.".formatted(e.getMessage(), endpoint.name()), e);
        }
        streams.add(l);
        refreshInputControlStates();
    }

    public void stopAll() {
        for (var i = streams.size() - 1; i >= 0; i--) {
            client.unsubscribe(streams.get(i));
            streams.remove(i);
        }
    }

    public void host(String host) {
        this.host = host;
    }

    public void publicEndpoint(Server publicEndpoint) {
        this.publicEndpoint = publicEndpoint;
    }

    public void adminEndpoint(Server adminEndpoint) {
        this.adminEndpoint = adminEndpoint;
    }

    public void knownValidatorRepo(KnownValidatorRepo knownValidatorRepo) {
        this.knownValidatorRepo = knownValidatorRepo;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        cmdStartStop.setEnabled(enabled);
    }

    public void addExceptionListener(ExceptionListener l) {
        listener.add(l);
    }

    private void raiseOnException(Exception e) {
        for (var l : listener) {
            l.exceptionThrown(e);
        }
    }
}
