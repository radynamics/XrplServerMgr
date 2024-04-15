package com.radynamics.xrplservermgr.ui;

import com.radynamics.xrplservermgr.utils.RequestFocusListener;

import javax.swing.*;
import java.awt.*;

public class PasswordInput {
    private final JPasswordField txtPassword = new JPasswordField();

    public int showSudo(Component parentComponent) {
        return show(parentComponent, "Please enter root password for sudo operations.", "root required");
    }

    public int show(Component parentComponent, String label, String title) {
        var pnl = new JPanel();
        pnl.setLayout(new GridLayout(2, 1));
        pnl.add(new JLabel(label));
        pnl.add(txtPassword);
        txtPassword.addAncestorListener(new RequestFocusListener());

        return JOptionPane.showConfirmDialog(parentComponent, pnl, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
    }

    public char[] password() {
        return txtPassword.getPassword();
    }
}
