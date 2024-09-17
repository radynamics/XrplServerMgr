package com.radynamics.xrplservermgr.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.radynamics.xrplservermgr.xrpl.Amendment;
import com.radynamics.xrplservermgr.xrpl.parser.Feature;
import com.radynamics.xrplservermgr.xrpl.parser.Features;
import com.radynamics.xrplservermgr.xrpl.rippled.Vote;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AmendmentsView extends JPanel {
    private final ArrayList<VoteState> state = new ArrayList<>();

    private final JPanel pnl = new JPanel();

    public AmendmentsView() {
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));

        var ml = new SpringLayout();
        setLayout(ml);

        var sp = new JScrollPane(pnl);
        add(sp);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        ml.putConstraint(SpringLayout.WEST, sp, 0, SpringLayout.WEST, this);
        ml.putConstraint(SpringLayout.NORTH, sp, 0, SpringLayout.NORTH, this);
        ml.putConstraint(SpringLayout.EAST, sp, 0, SpringLayout.EAST, this);
        ml.putConstraint(SpringLayout.SOUTH, sp, 0, SpringLayout.SOUTH, this);
    }

    public void init(Features features, List<Amendment> knownAmendments) {
        pnl.removeAll();
        var sorted = features.all().stream()
                .sorted(Features.createComparator())
                .collect(Collectors.toList());
        for (var f : sorted) {
            var a = knownAmendments.stream().filter(o -> o.hash().equals(f.hash())).findFirst().orElse(null);
            pnl.add(createRow(a, f));
        }
    }

    private Component createRow(Amendment a, Feature f) {
        var l = new SpringLayout();
        var p = new JPanel();
        p.setPreferredSize(new Dimension(820, 25));
        p.setLayout(l);

        final var LEFT_NAME = 450;
        final var LEFT_INTRODUCED = LEFT_NAME + 200;
        final var LEFT_YEA = LEFT_INTRODUCED + 70;
        final var LEFT_NAY = LEFT_YEA + 50;
        {
            var lbl = Utils.formatAsLabel(new JTextField(f.hash()));
            p.add(lbl);
            l.putConstraint(SpringLayout.WEST, lbl, 0, SpringLayout.WEST, p);
            l.putConstraint(SpringLayout.NORTH, lbl, 0, SpringLayout.NORTH, p);
        }
        {
            var lbl = Utils.formatAsLabel(new JTextField(f.name()));
            p.add(lbl);
            l.putConstraint(SpringLayout.WEST, lbl, LEFT_NAME, SpringLayout.WEST, p);
            l.putConstraint(SpringLayout.NORTH, lbl, 0, SpringLayout.NORTH, p);
        }
        {
            var lbl = Utils.formatAsLabel(new JTextField(a == null || a.introduced() == null ? "" : a.introduced().toString()));
            p.add(lbl);
            l.putConstraint(SpringLayout.WEST, lbl, LEFT_INTRODUCED, SpringLayout.WEST, p);
            l.putConstraint(SpringLayout.NORTH, lbl, 0, SpringLayout.NORTH, p);
        }

        if (!f.votingActive()) {
            var lbl = new JLabel(f.enabled() ? "enabled" : "obsolete");
            p.add(lbl);
            l.putConstraint(SpringLayout.WEST, lbl, LEFT_YEA, SpringLayout.WEST, p);
            l.putConstraint(SpringLayout.NORTH, lbl, 0, SpringLayout.NORTH, p);
            return p;
        }

        var vs = new VoteState(f);
        state.add(vs);

        var yea = new JToggleButton("Yea");
        var nay = new JToggleButton("Nay");
        {
            p.add(yea);
            yea.addActionListener(e -> {
                // Untoggling is not supported due there must be a vote (a persisted vote cannot be unset).
                yea.setSelected(true);
                vs.current(Vote.accept);
            });
            yea.addActionListener(createVoteActionListener(yea, nay));
            yea.setSelected(f != null && f.vetoed() != null && !f.vetoed());
            formatButton(yea, Color.GREEN, Consts.ColorLightGreen);
            l.putConstraint(SpringLayout.WEST, yea, LEFT_YEA, SpringLayout.WEST, p);
            l.putConstraint(SpringLayout.NORTH, yea, 0, SpringLayout.NORTH, p);
        }
        {
            p.add(nay);
            nay.addActionListener(e -> {
                // Untoggling is not supported due there must be a vote (a persisted vote cannot be unset).
                nay.setSelected(true);
                vs.current(Vote.reject);
            });
            nay.addActionListener(createVoteActionListener(nay, yea));
            nay.setSelected(f != null && f.vetoed() != null && f.vetoed());
            formatButton(nay, Color.RED, Consts.ColorLightRed);
            l.putConstraint(SpringLayout.WEST, nay, LEFT_NAY, SpringLayout.WEST, p);
            l.putConstraint(SpringLayout.NORTH, nay, 0, SpringLayout.NORTH, p);
        }
        return p;
    }

    private static ActionListener createVoteActionListener(JToggleButton source, JToggleButton other) {
        return e -> {
            if (source.isSelected()) {
                other.setSelected(false);
            }
        };
    }

    private static void formatButton(JToggleButton cmd, Color border, Color background) {
        cmd.putClientProperty(FlatClientProperties.STYLE, "borderColor: %s; selectedBackground: %s".formatted(Utils.toHex(border), Utils.toHex(background)));
    }

    public List<VoteState> changes() {
        return state.stream().filter(VoteState::changed).collect(Collectors.toList());
    }
}
