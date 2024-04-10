package com.radynamics.xrplservermgr;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.radynamics.xrplservermgr.config.Configuration;
import com.radynamics.xrplservermgr.ui.Consts;
import com.radynamics.xrplservermgr.ui.MainForm;
import com.radynamics.xrplservermgr.utils.Utils;

import javax.swing.*;
import java.util.Collections;

public class Main {
    public static void main(String[] args) {
        Configuration.removeOldBackups();

        javax.swing.SwingUtilities.invokeLater(() -> {
            FlatLaf.setGlobalExtraDefaults(Collections.singletonMap("@accentColor", Utils.toHexString(Consts.ColorAccent)));
            FlatLightLaf.setup();

            var frm = new MainForm();
            frm.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frm.setSize(1450, 900);
            frm.setLocationByPlatform(true);
            frm.setVisible(true);
        });
    }
}
