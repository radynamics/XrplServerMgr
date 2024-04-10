package com.radynamics.xrplservermgr.ui;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class TitlePane extends JPanel {
    public TitlePane(String title) {
        setLayout(new BorderLayout());
        setBorder(new CompoundBorder(new EmptyBorder(4, 0, 4, 4), new MatteBorder(0, 0, 1, 0, Color.BLACK)));
        setAlignmentX(JPanel.LEFT_ALIGNMENT);
        var label = new JLabel(title);
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        add(label);
    }
}
