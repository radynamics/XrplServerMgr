package com.radynamics.xrplservermgr.ui;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.FlatButton;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class FilePathLabel extends JPanel {
    private final JTextField lbl = Utils.formatAsLabel(new JTextField());
    private final FlatButton cmd;

    private final ArrayList<FilePathLabelListener> listener = new ArrayList<>();

    public FilePathLabel(FlatSVGIcon icon) {
        setLayout(new BorderLayout());

        add(lbl, BorderLayout.CENTER);
        lbl.setText("");
        lbl.setBackground(null);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        cmd = new FlatButton();
        add(cmd, BorderLayout.EAST);
        cmd.setBackground(null);
        cmd.setButtonType(FlatButton.ButtonType.toolBarButton);
        cmd.setIcon(icon);
        cmd.setPreferredSize(new Dimension(21, 21));
        cmd.setBorderPainted(false);
        cmd.setVisible(false);
        cmd.addActionListener(e -> raiseButtonClicked());
    }

    public void setText(String text) {
        lbl.setText(text);
        cmd.setVisible(!StringUtils.isEmpty(text));
    }

    public String getText() {
        return lbl.getText();
    }

    public void addFilePathLabelListener(FilePathLabelListener l) {
        listener.add(l);
    }

    @Override
    public void setEnabled(boolean enabled) {
        cmd.setEnabled(enabled);
    }

    private void raiseButtonClicked() {
        for (var l : listener) {
            l.onButtonClicked();
        }
    }
}
