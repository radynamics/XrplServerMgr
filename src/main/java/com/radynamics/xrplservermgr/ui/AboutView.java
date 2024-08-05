package com.radynamics.xrplservermgr.ui;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.radynamics.xrplservermgr.VersionController;
import com.radynamics.xrplservermgr.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.net.URI;

public class AboutView extends JPanel implements TabPage {
    private final static Logger log = LogManager.getLogger(AboutView.class);

    public AboutView() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(30, 30, 10, 10));

        var lbl = new JLabel("About");
        add(lbl);
        lbl.putClientProperty("FlatLaf.styleClass", "h0");
        add(Box.createVerticalStrut(20));

        var vc = new VersionController();
        add(createLabelAndValue("Version:", vc.getVersion()));
        add(createLabelAndValue("Website:", Utils.createLinkLabel(this, URI.create("https://github.com/radynamics/XrplServerMgr"))));
        add(createLabelAndValue("Backups:", Utils.createLinkLabel(this, Configuration.backup())));
        add(createLabelAndValue("Error logs:", Utils.createLinkLabel(this, new File("").getAbsoluteFile().toPath())));
        add(Box.createVerticalStrut(20));

        var pnl = new JPanel();
        pnl.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 20));
        add(pnl);

        pnl.add(new DescriptionButton()
                .buttonText("Licenses")
                .title("Infos about %s".formatted(MainForm.appTitle))
                .description("Show more information about used libraries and licences.")
                .icon(new FlatSVGIcon("img/info.svg", 32, 32))
                .click(e -> showLicenses()));
    }

    private void showLicenses() {
        var sb = new StringBuilder();
        sb.append("FlatLaf, Apache License 2.0, https://github.com/JFormDesigner/FlatLaf" + System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("Apache Commons, Apache License 2.0, https://commons.apache.org" + System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("Apache Log4j 2, Apache License 2.0, https://logging.apache.org/log4j/2.x/index.html" + System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("JUnit, Eclipse Public License - v 2.0, https://junit.org" + System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("JSch, LGPL, https://github.com/mwiede/jsch" + System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("Gson, Apache License 2.0, https://github.com/google/gson" + System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("Semver4j, MIT License, https://github.com/vdurmont/semver4j" + System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("TableFilter, MIT License, https://github.com/coderazzi/tablefilter-swing" + System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("SQLite JDBC Driver, Apache License 2.0, https://github.com/xerial/sqlite-jdbc" + System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("Credential Secure Storage for Java, MIT License, https://github.com/microsoft/credential-secure-storage-for-java" + System.lineSeparator());
        sb.append(System.lineSeparator());

        var txt = new JTextArea(sb.toString());
        txt.setColumns(30);
        txt.setRows(15);
        txt.setEditable(false);
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);
        txt.setSize(txt.getPreferredSize().width, txt.getPreferredSize().height);
        JOptionPane.showMessageDialog(this, new JScrollPane(txt), "Used libraries", JOptionPane.INFORMATION_MESSAGE);
    }

    private static Component createLabelAndValue(String caption, String value) {
        return createLabelAndValue(caption, new JLabel(value));
    }

    private static Component createLabelAndValue(String caption, JComponent valueComponent) {
        var l = new SpringLayout();
        var pnl = new JPanel();
        pnl.setLayout(l);
        pnl.setPreferredSize(new Dimension(2000, 20));
        pnl.setMaximumSize(new Dimension(2000, 20));

        var lblCaption = new JLabel(caption);
        pnl.add(lblCaption);
        l.putConstraint(SpringLayout.WEST, lblCaption, 0, SpringLayout.WEST, pnl);
        l.putConstraint(SpringLayout.NORTH, lblCaption, 0, SpringLayout.NORTH, pnl);

        pnl.add(valueComponent);
        l.putConstraint(SpringLayout.WEST, valueComponent, 130, SpringLayout.WEST, pnl);
        l.putConstraint(SpringLayout.NORTH, valueComponent, 0, SpringLayout.NORTH, pnl);

        return pnl;
    }

    @Override
    public void close() {
    }
}
