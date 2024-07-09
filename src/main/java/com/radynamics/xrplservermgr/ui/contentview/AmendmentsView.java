package com.radynamics.xrplservermgr.ui.contentview;

import com.radynamics.xrplservermgr.sshapi.SshApiException;
import com.radynamics.xrplservermgr.ui.Utils;
import com.radynamics.xrplservermgr.xrpl.parser.Feature;
import com.radynamics.xrplservermgr.xrpl.parser.Features;
import com.radynamics.xrplservermgr.xrpl.rippled.RippledCommandException;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;

public class AmendmentsView extends ContentView {
    private final JTextArea txt;

    public AmendmentsView(JFrame parent) {
        super(parent);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        txt = new JTextArea();
        txt.setEditable(false);
        txt.setLineWrap(true);
        add(txt);
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
}
