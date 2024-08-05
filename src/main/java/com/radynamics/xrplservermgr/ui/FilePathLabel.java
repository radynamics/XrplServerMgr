package com.radynamics.xrplservermgr.ui;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.FlatButton;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class FilePathLabel extends JPanel {
    private final JTextField lbl = Utils.formatAsLabel(new JTextField());

    private final ArrayList<FilePathLabelListener> listener = new ArrayList<>();

    public FilePathLabel(FlatSVGIcon icon) {
        setLayout(new BorderLayout());

        add(lbl, BorderLayout.CENTER);
        lbl.setText("");
        lbl.setBackground(null);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        var cmd = new FlatButton();
        add(cmd, BorderLayout.EAST);
        cmd.setBackground(null);
        cmd.setButtonType(FlatButton.ButtonType.toolBarButton);
        cmd.setIcon(icon);
        cmd.setPreferredSize(new Dimension(21, 21));
        cmd.setBorderPainted(false);
        cmd.addActionListener(e -> raiseButtonClicked());
    }

    public void setText(String text) {
        lbl.setText(text);
    }

    public String getText() {
        return lbl.getText();
    }

    public void addRemoteFilePathLabelListener(FilePathLabelListener l) {
        listener.add(l);
    }

    private void raiseButtonClicked() {
        for (var l : listener) {
            l.onButtonClicked();
        }
    }
}
