package com.radynamics.xrplservermgr.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class FilePathField extends JPanel {
    private File currentDirectory;

    private final JTextField txt;
    private JToggleButton cmdOpen;

    public FilePathField() {
        setLayout(new GridBagLayout());

        {
            txt = new JTextField();
            txt.setColumns(52);
            txt.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT, createToolbar());
            txt.setInputVerifier(new InputVerifier() {
                @Override
                public boolean verify(JComponent input) {
                    var text = ((JTextField) input).getText().trim();
                    var file = new File(text);
                    if (text.isEmpty() || (file.isFile() && file.exists())) {
                        txt.putClientProperty("JComponent.outline", null);
                        return true;
                    } else {
                        txt.putClientProperty("JComponent.outline", "error");
                        return false;
                    }
                }
            });
            txt.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    var file = new File(txt.getText().trim());
                    if (file.exists()) {
                        currentDirectory = file.getParentFile();
                    }
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    // do nothing
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    // do nothing
                }
            });
            var c = new GridBagConstraints();
            c.fill = GridBagConstraints.BOTH;
            c.weightx = 1.0;
            c.weighty = 0.5;
            c.gridx = 0;
            c.gridy = 0;
            add(txt, c);
        }
    }

    private Object createToolbar() {
        var toolbar = new JToolBar();
        {
            var icon = new FlatSVGIcon("img/open.svg", 16, 16);
            icon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> Color.lightGray));
            cmdOpen = new JToggleButton(icon);
            toolbar.add(cmdOpen);
            Utils.setRolloverIcon(cmdOpen);
            cmdOpen.setToolTipText("Browse file...");
            cmdOpen.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (cmdOpen.isEnabled()) {
                        showFileChooser();
                    }
                }
            });
        }
        return toolbar;
    }

    private void showFileChooser() {
        var fc = new JFileChooser();
        if (!getText().isEmpty()) {
            fc.setCurrentDirectory(new File(getText()).getParentFile());
        } else if (currentDirectory != null) {
            fc.setCurrentDirectory(currentDirectory);
        }
        int option = fc.showOpenDialog(this);
        if (option != JFileChooser.APPROVE_OPTION) {
            return;
        }
        setText(fc.getSelectedFile().getAbsolutePath());
    }

    public void setText(String value) {
        txt.setText(value);
        txt.getInputVerifier().verify(txt);
    }

    public String getText() {
        return txt.getText().trim();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        txt.setEditable(enabled);
        cmdOpen.setEnabled(enabled);
    }
}
