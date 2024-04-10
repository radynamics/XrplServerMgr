package com.radynamics.xrplservermgr.ui;

import com.radynamics.xrplservermgr.sshapi.ActionLogLevel;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ActionOutputView extends JPanel {
    private final JTextPane txt;
    private final JScrollPane scrollPane;

    private final Style warning;
    private final Style error;

    public ActionOutputView() {
        txt = new JTextPane();
        txt.setBackground(Color.lightGray);

        var doc = (StyledDocument) txt.getDocument();
        error = doc.addStyle("regular", null);
        StyleConstants.setForeground(error, Color.RED);
        warning = doc.addStyle("regular", null);
        StyleConstants.setForeground(warning, Consts.ColorWarning);

        scrollPane = new JScrollPane(txt);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getViewport().add(txt);
        scrollPane.getViewport().setPreferredSize(txt.getPreferredSize());
    }

    public void append(String message, ActionLogLevel level) {
        var now = Instant.now();
        var formatter = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault());

        final String msg = "%s: %s\n".formatted(formatter.format(now), message);

        var style = getStyle(level);
        var doc = (StyledDocument) txt.getDocument();
        try {
            doc.insertString(doc.getLength(), msg, style);
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
        txt.setCaretPosition(doc.getLength());
    }

    private Style getStyle(ActionLogLevel level) {
        switch (level) {
            case Warning -> {
                return warning;
            }
            case Error -> {
                return error;
            }
            default -> {
                return null;
            }
        }
    }

    public JScrollPane scrollPane() {
        return scrollPane;
    }
}
