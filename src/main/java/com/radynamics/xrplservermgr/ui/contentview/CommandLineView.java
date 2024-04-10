package com.radynamics.xrplservermgr.ui.contentview;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.google.gson.GsonBuilder;
import com.radynamics.xrplservermgr.sshapi.SshApiException;
import com.radynamics.xrplservermgr.utils.RequestFocusListener;
import com.radynamics.xrplservermgr.xrpl.rippled.RippledCommandException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class CommandLineView extends ContentView {
    private final JTextField txtInput = new JTextField();
    private final JButton cmdExecute;
    private final JTextArea txtResult;

    public CommandLineView(JFrame parent) {
        super(parent);

        var layout = new SpringLayout();
        setLayout(layout);

        cmdExecute = new JButton("execute");
        add(cmdExecute);
        layout.putConstraint(SpringLayout.NORTH, cmdExecute, 0, SpringLayout.NORTH, this);
        layout.putConstraint(SpringLayout.EAST, cmdExecute, 0, SpringLayout.EAST, this);
        cmdExecute.setIcon(new FlatSVGIcon("img/refresh.svg", 12, 12));
        cmdExecute.setPreferredSize(new Dimension(100, 30));
        cmdExecute.addActionListener(e -> execute());

        add(txtInput);
        layout.putConstraint(SpringLayout.WEST, txtInput, 0, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.NORTH, txtInput, 0, SpringLayout.NORTH, this);
        layout.putConstraint(SpringLayout.EAST, txtInput, -10, SpringLayout.WEST, cmdExecute);
        txtInput.setPreferredSize(new Dimension(10, 30));
        txtInput.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "ledger {\"ledger_index\": \"validated\"}");
        txtInput.addAncestorListener(new RequestFocusListener());
        txtInput.registerKeyboardAction(e -> execute(), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_FOCUSED);

        txtResult = new JTextArea();
        txtResult.setEditable(false);

        var sp = new JScrollPane(txtResult);
        add(sp);
        layout.putConstraint(SpringLayout.WEST, sp, 0, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.NORTH, sp, 10, SpringLayout.SOUTH, txtInput);
        layout.putConstraint(SpringLayout.EAST, sp, 0, SpringLayout.EAST, this);
        layout.putConstraint(SpringLayout.SOUTH, sp, 0, SpringLayout.SOUTH, this);

        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        sp.getViewport().add(txtResult);
        sp.getViewport().setPreferredSize(txtResult.getPreferredSize());

        registerKeyboardAction(e -> execute(), KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void execute() {
        var gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            var text = txtInput.getText().trim();
            if (text.isEmpty()) {
                return;
            }

            var exec = xrplBinary.createCommandExecutor();
            var response = exec.execute(text);
            txtResult.setText(gson.toJson(response));
        } catch (SshApiException e) {
            outputError(e.getMessage());
        } catch (RippledCommandException e) {
            outputError(e.getMessage());
            if (e.json() != null) {
                txtResult.setText(gson.toJson(e.json()));
            }
        }
    }

    @Override
    public String tabText() {
        return "Console";
    }

    @Override
    public void close() {
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        txtInput.setEnabled(enabled);
        cmdExecute.setEnabled(enabled);
    }
}
