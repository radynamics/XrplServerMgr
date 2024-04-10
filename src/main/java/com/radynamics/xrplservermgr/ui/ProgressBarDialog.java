package com.radynamics.xrplservermgr.ui;

import com.radynamics.xrplservermgr.sshapi.ProgressListener;

import javax.swing.*;
import java.awt.*;

public final class ProgressBarDialog extends JDialog implements ProgressListener {
    private final JProgressBar progressBar = new JProgressBar(0, 100);

    private ProgressBarDialog(Frame owner, String title) {
        super(owner, title, true);

        var pnl = new JPanel();
        setContentPane(pnl);
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        var lbl = new JLabel("Loading, please wait...");
        pnl.add(lbl);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnl.add(Box.createVerticalStrut(5));

        pnl.add(progressBar);
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);

        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setSize(300, 100);
        setLocationRelativeTo(owner);
    }

    public static ProgressBarDialog create(JFrame parentFrame) {
        return new ProgressBarDialog(parentFrame, "Loading");
    }

    @Override
    public void onProgress(int current, int total) {
        progressBar.setMaximum(total);
        progressBar.setValue(current);
    }

    @Override
    public void onCompleted() {
        setVisible(false);
    }
}
