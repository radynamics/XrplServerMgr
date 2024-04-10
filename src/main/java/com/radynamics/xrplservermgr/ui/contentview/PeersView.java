package com.radynamics.xrplservermgr.ui.contentview;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.radynamics.xrplservermgr.sshapi.SshApiException;
import com.radynamics.xrplservermgr.xrpl.rippled.RippledCommandException;

import javax.swing.*;
import java.awt.*;

public class PeersView extends ContentView {
    private final JButton cmdRefresh;
    private final JTextArea txt;

    public PeersView(JFrame parent) {
        super(parent);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        cmdRefresh = new JButton("refresh");
        add(cmdRefresh);
        cmdRefresh.setIcon(new FlatSVGIcon("img/refresh.svg", 12, 12));
        cmdRefresh.addActionListener(e -> refresh());

        txt = new JTextArea();
        txt.setEditable(false);

        var sp = new JScrollPane(txt);
        add(sp);
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        sp.getViewport().add(txt);
        sp.getViewport().setPreferredSize(txt.getPreferredSize());
        sp.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    @Override
    protected void refresh() {
        try {
            xrplBinary.refreshPeers();
            var gson = new GsonBuilder().setPrettyPrinting().create();
            txt.setText(gson.toJson(JsonParser.parseString(xrplBinary.peers().raw()).getAsJsonObject()));
        } catch (SshApiException | RippledCommandException e) {
            outputError(e.getMessage());
        }
    }

    @Override
    public String tabText() {
        return "Peers";
    }

    @Override
    public void close() {
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        cmdRefresh.setEnabled(enabled);
    }
}
