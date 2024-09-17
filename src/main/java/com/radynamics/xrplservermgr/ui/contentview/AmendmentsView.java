package com.radynamics.xrplservermgr.ui.contentview;

import com.radynamics.xrplservermgr.config.Configuration;
import com.radynamics.xrplservermgr.sshapi.SshApiException;
import com.radynamics.xrplservermgr.ui.AmendmentsForm;
import com.radynamics.xrplservermgr.ui.Utils;
import com.radynamics.xrplservermgr.ui.VoteState;
import com.radynamics.xrplservermgr.xrpl.parser.Feature;
import com.radynamics.xrplservermgr.xrpl.parser.Features;
import com.radynamics.xrplservermgr.xrpl.rippled.RippledCommandException;

import javax.swing.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class AmendmentsView extends ContentView {
    private final String host;
    private final JTextArea txt;
    private final JButton cmdShow;

    public AmendmentsView(JFrame parent, String host) {
        super(parent);
        this.host = host;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        cmdShow = appendButton("show", this::showAmendments);

        txt = new JTextArea();
        txt.setEditable(false);
        txt.setLineWrap(true);
        add(txt);
    }

    private JButton appendButton(String caption, Runnable r) {
        var cmd = new JButton(caption);
        add(cmd);
        cmd.addActionListener(e -> r.run());
        return cmd;
    }

    private void showAmendments() {
        Utils.runAsync(() -> {
            try {
                xrplBinary.refresh();
                if (xrplBinary.features() == null) {
                    JOptionPane.showMessageDialog(this, "No configured amendments available.");
                    return;
                }

                var frm = new AmendmentsForm(parent, xrplBinary.features(), xrplBinary.knownAmendments());
                frm.setSize(900, 600);
                frm.setLocationRelativeTo(parent);
                frm.setVisible(true);
                if (!frm.accepted()) {
                    return;
                }

                System.out.println("ACCEPTED: update votes");
                var changed = frm.changes();
                if (!changed.isEmpty()) {
                    changeVotes(changed);
                }
            } catch (SshApiException | RippledCommandException | IOException e) {
                outputError(e.getMessage());
            }
        });
    }

    private void changeVotes(List<VoteState> votes) throws IOException, SshApiException, RippledCommandException {
        xrplBinary.config().saveAs(Configuration.createNewRippleCfgBackupFile(host).getAbsolutePath());

        for (var v : votes) {
            xrplBinary.vote(v.feature(), v.current());
        }

        outputInfo("%s votes updated.".formatted(votes.size()));
        xrplBinary.refreshFeatures();
    }

    private void listFeatures() {
        Utils.runAsync(() -> {
            try {
                xrplBinary.refresh();
                if (xrplBinary.features() == null) {
                    return;
                }
                var sb = new StringBuilder();
                var votedYea = xrplBinary.features().votedInFavor().stream()
                        .filter(o -> !o.enabled() && !o.obsolete())
                        .sorted(Features.createComparator())
                        .limit(10)
                        .collect(Collectors.toList());
                sb.append("=== Voted in favor:\n");
                append(sb, votedYea);

                var newest = xrplBinary.features().all().stream()
                        .sorted(Features.createComparator())
                        .limit(10)
                        .collect(Collectors.toList());
                sb.append("\n");
                sb.append("=== Newest:\n");
                append(sb, newest);

                txt.setText(sb.toString());
            } catch (SshApiException | RippledCommandException e) {
                outputError(e.getMessage());
            }
        });
    }

    private void append(StringBuilder sb, List<Feature> list) {
        for (var e : list) {
            var versionIntroducedText = e.versionIntroduced() == null ? "unk" : e.versionIntroduced().toString();
            sb.append("- [%s] %s\n".formatted(versionIntroducedText, e.nameOrHash()));
        }
    }

    @Override
    protected void refresh() {
        super.refresh();

        listFeatures();
    }

    @Override
    public String tabText() {
        return "Amendments";
    }

    @Override
    public void close() {

    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        cmdShow.setEnabled(enabled);
    }
}
