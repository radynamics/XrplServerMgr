package com.radynamics.xrplservermgr.ui;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.FlatButton;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class RemoteFilePathLabel extends JPanel {
    private final JTextField lbl = Utils.formatAsLabel(new JTextField());

    private final ArrayList<RemoteFilePathLabelListener> listener = new ArrayList<>();

    public RemoteFilePathLabel() {
        this("");
    }

    public RemoteFilePathLabel(String text) {
        setLayout(new BorderLayout());

        add(lbl, BorderLayout.CENTER);
        lbl.setText(text);
        lbl.setBackground(null);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        var cmd = new FlatButton();
        add(cmd, BorderLayout.EAST);
        cmd.setBackground(null);
        cmd.setButtonType(FlatButton.ButtonType.toolBarButton);
        var icon = new FlatSVGIcon("img/save.svg", 16, 16);
        icon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> Color.lightGray));
        cmd.setIcon(icon);
        cmd.setPreferredSize(new Dimension(21, 21));
        cmd.setBorderPainted(false);
        cmd.addActionListener(e -> raiseSaveClicked());
    }

    public void setText(String text) {
        lbl.setText(text);
    }

    public String getText() {
        return lbl.getText();
    }

    public void addRemoteFilePathLabelListener(RemoteFilePathLabelListener l) {
        listener.add(l);
    }

    private void raiseSaveClicked() {
        for (var l : listener) {
            l.onSaveClicked();
        }
    }
}
