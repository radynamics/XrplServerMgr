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
    private final JButton cmdListFeatures;

    public AmendmentsView(JFrame parent) {
        super(parent);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        cmdListFeatures = appendButton("list features", this::listFeatures);

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

    private void listFeatures() {
        Utils.runAsync(() -> {
            try {
                xrplBinary.refresh();
                if (xrplBinary.features() == null) {
                    return;
                }
                var sb = new StringBuilder();
                sb.append("=== rippled feature count: %s\n".formatted(xrplBinary.features().all().size()));
                sb.append("=== Voted in favor: %s\n".formatted(toString(xrplBinary.features().votedInFavor())));
                var newest = xrplBinary.features().all().stream()
                        .filter(o -> !o.enabled() && !o.obsolete())
                        .sorted(Features.createComparator())
                        .limit(10)
                        .collect(Collectors.toList());
                sb.append("=== Newest: %s\n".formatted(toString(newest)));

                txt.setText(sb.toString());
            } catch (SshApiException | RippledCommandException e) {
                outputError(e.getMessage());
            }
        });
    }

    private static StringBuilder toString(List<Feature> features) {
        var sb = new StringBuilder();
        for (var f : features) {
            sb.append(f.name() + ", ");
        }
        return sb;
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

        cmdListFeatures.setEnabled(enabled);
    }
}
