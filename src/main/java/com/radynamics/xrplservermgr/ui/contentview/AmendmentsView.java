package com.radynamics.xrplservermgr.ui.contentview;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.radynamics.xrplservermgr.config.Configuration;
import com.radynamics.xrplservermgr.sshapi.SshApiException;
import com.radynamics.xrplservermgr.ui.Utils;
import com.radynamics.xrplservermgr.ui.VoteState;
import com.radynamics.xrplservermgr.xrpl.rippled.RippledCommandException;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class AmendmentsView extends ContentView {
    private final JButton cmdSave;
    private final JButton cmdRefresh;
    private final com.radynamics.xrplservermgr.ui.AmendmentsView view;
    private final String host;

    public AmendmentsView(JFrame parent, String host) {
        super(parent);
        this.host = host;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        var toolbar = new JToolBar();
        add(toolbar);
        toolbar.setAlignmentX(Component.LEFT_ALIGNMENT);
        {
            cmdSave = new JButton("save...");
            toolbar.add(cmdSave);
            cmdSave.setIcon(new FlatSVGIcon("img/save.svg"));
            cmdSave.addActionListener(e -> onSaveClick());
        }
        {
            cmdRefresh = new JButton("refresh");
            toolbar.add(cmdRefresh);
            cmdRefresh.setIcon(new FlatSVGIcon("img/refresh.svg"));
            cmdRefresh.addActionListener(e -> refresh());
        }
        toolbar.addSeparator(new Dimension(10, 0));
        {
            var lbl = Utils.createLinkLabel(this, "Show consensus of amendments...", true, () -> {
                Utils.openBrowser(this, xrplBinary.uris().amendmentsOverview());
                return null;
            });
            toolbar.add(lbl);
        }

        view = new com.radynamics.xrplservermgr.ui.AmendmentsView();
        add(view);
    }

    private void changeVotes(List<VoteState> votes) throws IOException, SshApiException, RippledCommandException {
        xrplBinary.config().saveAs(Configuration.createNewRippleCfgBackupFile(host).getAbsolutePath());

        for (var v : votes) {
            xrplBinary.vote(v.feature(), v.current());
        }

        outputInfo("%s votes updated.".formatted(votes.size()));
        xrplBinary.refreshFeatures();
    }

    private void onSaveClick() {
        var changed = view.changes();
        if (changed.isEmpty()) {
            return;
        }

        try {
            changeVotes(changed);
        } catch (SshApiException | RippledCommandException | IOException e) {
            outputError(e.getMessage());
        }
    }

    @Override
    protected void refresh() {
        super.refresh();

        try {
            xrplBinary.refresh();
            if (xrplBinary.features() == null) {
                JOptionPane.showMessageDialog(this, "No configured amendments available.");
                return;
            }
            view.refresh(xrplBinary.features(), xrplBinary.knownAmendments());
        } catch (SshApiException | RippledCommandException e) {
            outputError(e.getMessage());
        }
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

        cmdSave.setEnabled(enabled);
        cmdRefresh.setEnabled(enabled);
        view.setEnabled(enabled);
    }
}
