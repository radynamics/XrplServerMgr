package com.radynamics.xrplservermgr.ui;

import javax.swing.*;
import java.awt.*;

public final class MessageBox {
    public static Boolean showBetaFeatureWarning(Component parentComponent) {
        var sb = new StringBuilder();
        sb.append("This function has not yet been fully tested and should be used with caution and only on test systems.");
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("Do you want to proceed?");
        var resPerm = JOptionPane.showConfirmDialog(parentComponent, sb.toString(), "Beta feature", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        return resPerm == JOptionPane.YES_OPTION;
    }
}
