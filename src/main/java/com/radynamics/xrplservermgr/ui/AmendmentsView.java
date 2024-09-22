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
            var v = new AmendmentRowView(a, f);
            pnl.add(v.view());
        }
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

    private class AmendmentRowView {
        private final JPanel view;
        private final JToggleButton yea = new JToggleButton("Yea");
        private final JToggleButton nay = new JToggleButton("Nay");

        public AmendmentRowView(Amendment a, Feature f) {
            var l = new SpringLayout();
            view = new JPanel();
            view.setPreferredSize(new Dimension(820, 25));
            view.setLayout(l);

            final var LEFT_NAME = 450;
            final var LEFT_INTRODUCED = LEFT_NAME + 200;
            final var LEFT_YEA = LEFT_INTRODUCED + 70;
            final var LEFT_NAY = LEFT_YEA + 50;
            {
                var lbl = Utils.formatAsLabel(new JTextField(f.hash()));
                view.add(lbl);
                l.putConstraint(SpringLayout.WEST, lbl, 0, SpringLayout.WEST, view);
                l.putConstraint(SpringLayout.NORTH, lbl, 0, SpringLayout.NORTH, view);
            }
            {
                var lbl = Utils.formatAsLabel(new JTextField(f.name()));
                view.add(lbl);
                l.putConstraint(SpringLayout.WEST, lbl, LEFT_NAME, SpringLayout.WEST, view);
                l.putConstraint(SpringLayout.NORTH, lbl, 0, SpringLayout.NORTH, view);
            }
            {
                var lbl = Utils.formatAsLabel(new JTextField(a == null || a.introduced() == null ? "" : a.introduced().toString()));
                view.add(lbl);
                l.putConstraint(SpringLayout.WEST, lbl, LEFT_INTRODUCED, SpringLayout.WEST, view);
                l.putConstraint(SpringLayout.NORTH, lbl, 0, SpringLayout.NORTH, view);
            }

            if (!f.votingActive()) {
                var lbl = new JLabel(f.enabled() ? "enabled" : "obsolete");
                view.add(lbl);
                l.putConstraint(SpringLayout.WEST, lbl, LEFT_YEA, SpringLayout.WEST, view);
                l.putConstraint(SpringLayout.NORTH, lbl, 0, SpringLayout.NORTH, view);
            }

            var vs = new VoteState(f);
            state.add(vs);

            {
                view.add(yea);
                yea.setVisible(f.votingActive());
                yea.addActionListener(e -> {
                    // Untoggling is not supported due there must be a vote (a persisted vote cannot be unset).
                    yea.setSelected(true);
                    vs.current(Vote.accept);
                });
                yea.addActionListener(createVoteActionListener(yea, nay));
                yea.setSelected(f != null && f.vetoed() != null && !f.vetoed());
                formatButton(yea, Color.GREEN, Consts.ColorLightGreen);
                l.putConstraint(SpringLayout.WEST, yea, LEFT_YEA, SpringLayout.WEST, view);
                l.putConstraint(SpringLayout.NORTH, yea, 0, SpringLayout.NORTH, view);
            }
            {
                view.add(nay);
                nay.setVisible(f.votingActive());
                nay.addActionListener(e -> {
                    // Untoggling is not supported due there must be a vote (a persisted vote cannot be unset).
                    nay.setSelected(true);
                    vs.current(Vote.reject);
                });
                nay.addActionListener(createVoteActionListener(nay, yea));
                nay.setSelected(f != null && f.vetoed() != null && f.vetoed());
                formatButton(nay, Color.RED, Consts.ColorLightRed);
                l.putConstraint(SpringLayout.WEST, nay, LEFT_NAY, SpringLayout.WEST, view);
                l.putConstraint(SpringLayout.NORTH, nay, 0, SpringLayout.NORTH, view);
            }
        }

        public Component view() {
            return view;
        }

        public void setEnabled(boolean enabled) {
            yea.setEnabled(enabled);
            nay.setEnabled(enabled);
        }
    }
}
