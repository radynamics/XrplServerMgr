package com.radynamics.xrplservermgr.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class DescriptionButton extends JPanel {
    private final JButton cmd = new JButton();
    private final JLabel lblTitle = new JLabel();
    private final JLabel lblDescription = new JLabel();
    private final JLabel lblButtonText = new JLabel();
    private final JLabel lblButtonIcon = new JLabel();

    public DescriptionButton() {
        var l = new SpringLayout();
        setLayout(l);
        setPreferredSize(new Dimension(1000, 100));
        setMaximumSize(new Dimension(1000, 100));

        add(cmd);
        final int SIZE = 100;
        cmd.setPreferredSize(new Dimension(SIZE, SIZE));
        cmd.setMaximumSize(new Dimension(SIZE, SIZE));
        {
            var pnl = new JPanel();
            cmd.add(pnl);
            pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
            pnl.setOpaque(false);

            lblButtonIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
            pnl.add(lblButtonIcon);
            lblButtonText.setAlignmentX(Component.CENTER_ALIGNMENT);
            lblButtonText.setHorizontalAlignment(JLabel.CENTER);
            pnl.add(lblButtonText);
        }
        l.putConstraint(SpringLayout.WEST, cmd, 0, SpringLayout.WEST, this);
        l.putConstraint(SpringLayout.NORTH, cmd, 0, SpringLayout.NORTH, this);

        add(lblTitle);
        lblTitle.putClientProperty("FlatLaf.styleClass", "large");
        l.putConstraint(SpringLayout.WEST, lblTitle, 130, SpringLayout.WEST, this);
        l.putConstraint(SpringLayout.NORTH, lblTitle, 0, SpringLayout.NORTH, this);

        add(lblDescription);
        l.putConstraint(SpringLayout.WEST, lblDescription, 130, SpringLayout.WEST, this);
        l.putConstraint(SpringLayout.NORTH, lblDescription, 30, SpringLayout.NORTH, this);
    }

    public DescriptionButton buttonText(String text) {
        lblButtonText.setText("<html>%s</html>".formatted(text));
        return this;
    }

    public DescriptionButton icon(Icon icon) {
        lblButtonIcon.setIcon(icon);
        return this;
    }

    public DescriptionButton title(String text) {
        lblTitle.setText(text);
        return this;
    }

    public DescriptionButton description(String text) {
        lblDescription.setText(text);
        return this;
    }

    public DescriptionButton click(ActionListener listener) {
        cmd.addActionListener(listener);
        return this;
    }
}
