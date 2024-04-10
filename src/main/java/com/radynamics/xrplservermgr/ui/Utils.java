package com.radynamics.xrplservermgr.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.concurrent.Executors;

public final class Utils {
    private final static Logger log = LogManager.getLogger(Utils.class);

    public static JTextField formatAsLabel(JTextField txt) {
        txt.setBorder(null);
        txt.setEditable(false);
        return txt;
    }

    public static void runAsync(Runnable r) {
        Executors.newFixedThreadPool(1).execute(r);
    }

    public static JLabel createLinkLabel(JComponent owner, String text, boolean enabled) {
        return createLinkLabel(owner, text, enabled, null);
    }

    public static JLabel createLinkLabel(JComponent owner, String text, boolean enabled, URI uri) {
        var lbl = new JLabel(text);
        lbl.setEnabled(enabled);
        lbl.setForeground(Consts.ColorAccent);

        if (!enabled) {
            return lbl;
        }

        final var regular = lbl.getFont();
        var attributes = new HashMap<TextAttribute, Object>(regular.getAttributes());
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        final var underscored = regular.deriveFont(attributes);

        lbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                owner.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                lbl.setFont(underscored);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                owner.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                lbl.setFont(regular);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                lbl.requestFocus();
            }
        });

        if (uri != null) {
            lbl.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 1) {
                        openBrowser(owner, uri);
                    }
                }
            });
        }
        return lbl;
    }

    public static JTextArea formatLabel(JTextArea lbl) {
        lbl.setEditable(false);
        lbl.setHighlighter(null);
        lbl.setLineWrap(true);
        lbl.setWrapStyleWord(true);
        lbl.setMargin(new Insets(0, 0, 0, 0));
        return lbl;
    }

    public static void openBrowser(Component parent, URI uri) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(uri);
            } catch (IOException ex) {
                ExceptionDialog.show(parent, ex);
            }
        } else {
            log.warn("No desktop or no browsing supported");
        }
    }
}
