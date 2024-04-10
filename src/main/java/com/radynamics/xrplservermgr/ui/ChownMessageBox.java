package com.radynamics.xrplservermgr.ui;

import com.radynamics.xrplservermgr.xrpl.ChownRemotePath;

import javax.swing.*;
import java.awt.*;

public class ChownMessageBox implements ChownRemotePath {
    private final Component parentComponent;

    public ChownMessageBox(Component parentComponent) {
        this.parentComponent = parentComponent;
    }

    @Override
    public Boolean doChown(String remotePath, String newOwner) {
        var sb = new StringBuilder();
        sb.append("Could not write to write %s".formatted(remotePath));
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("Do you want to change the ownership (chown) to %s be able to overwrite the file?".formatted(newOwner));
        var resPerm = JOptionPane.showConfirmDialog(parentComponent, sb.toString(), "Permission denied", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        return resPerm == JOptionPane.YES_OPTION;
    }
}
