package com.radynamics.xrplservermgr.ui;

import com.radynamics.xrplservermgr.xrpl.subscription.*;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.beans.ExceptionListener;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class StreamView extends JPanel implements ValidationStreamListener, LedgerStreamListener {
    private final JTextArea txt = new JTextArea();
    private final JButton cmdStartStop;
    private final JComboBox<Stream> cbo;
    private SubscriptionStreamSession client;
    private final ArrayList<ExceptionListener> listener = new ArrayList<>();
    private final ArrayList<StreamListener> streams = new ArrayList<>();
    private URI endpoint;

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

        txt.setEditable(false);
        txt.setFont(new Font("monospaced", Font.PLAIN, 12));

        var sp = new JScrollPane(txt);
        add(sp);
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        sp.getViewport().add(txt);
        sp.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Always scroll to last record.
        sp.getVerticalScrollBar().addAdjustmentListener(e -> e.getAdjustable().setValue(e.getAdjustable().getMaximum()));
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
        txt.setText("");

        StreamListener l;
        var selected = (Stream) cbo.getSelectedItem();
        switch (selected) {
            case Validations:
                var vs = new ValidationStream();
                vs.addListener(this);
                l = vs;
                break;
            case Ledger:
                var ls = new LedgerStream();
                ls.addListener(this);
                l = ls;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + selected);
        }

        client = new SubscriptionStreamSession(endpoint, l);
        client.subscribe();
        streams.add(l);
        refreshInputControlStates();
    }

    public void stopAll() {
        for (var i = streams.size() - 1; i >= 0; i--) {
            client.unsubscribe(streams.get(i));
            streams.remove(i);
        }
    }

    @Override
    public void onReceive(ValidationStreamData data) {
        var ledgerIndexText = StringUtils.leftPad(data.ledgerIndex(), 10, ' ');
        var signingTimeText = StringUtils.leftPad(String.valueOf(data.signingTime()), 10, ' ');
        var validatorValueText = data.masterKey() == null ? data.validationPublicKey() : data.masterKey();
        var validatorText = StringUtils.leftPad(validatorValueText, 55, ' ');
        appendLog("%s %s %s".formatted(ledgerIndexText, signingTimeText, validatorText));
    }

    @Override
    public void onReceive(LedgerStreamData data) {
        var typeText = StringUtils.leftPad(data.type(), 10, ' ');
        var ledgerIndexText = StringUtils.leftPad(String.valueOf(data.ledgerIndex()), 10, ' ');
        var txCountText = StringUtils.leftPad(String.valueOf(data.txnCount()), 6, ' ');
        var ledgerHashText = StringUtils.leftPad(data.ledgerHash(), 60, ' ');
        appendLog("%s %s %s %s".formatted(typeText, ledgerIndexText, txCountText, ledgerHashText));
    }

    private void appendLog(String text) {
        var sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        var timeText = sdf.format(new Date());
        txt.append("%s: %s\n".formatted(timeText, text));
    }

    public void endpoint(URI endpoint) {
        this.endpoint = endpoint;
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
