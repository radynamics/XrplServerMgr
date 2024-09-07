package com.radynamics.xrplservermgr.ui.logview;

import com.radynamics.xrplservermgr.ui.FormAcceptCloseHandler;
import com.radynamics.xrplservermgr.ui.MainForm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LogViewerForm extends JDialog {
    private final LogViewerView view;

    private final FormAcceptCloseHandler formAcceptCloseHandler = new FormAcceptCloseHandler(this);

    public LogViewerForm(MainForm owner, LogProvider provider) {
        super(owner, "Log Viewer");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);

        formAcceptCloseHandler.configure();

        var l = new SpringLayout();
        setLayout(l);

        view = new LogViewerView(owner, provider);
        add(view);
        view.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        if (provider.createStreamingProvider() == null) {
            view.reload();
        } else {
            view.startStreaming();
        }
        l.putConstraint(SpringLayout.WEST, view, 0, SpringLayout.WEST, getContentPane());
        l.putConstraint(SpringLayout.NORTH, view, 0, SpringLayout.NORTH, getContentPane());
        l.putConstraint(SpringLayout.EAST, view, 0, SpringLayout.EAST, getContentPane());
        l.putConstraint(SpringLayout.SOUTH, view, -60, SpringLayout.SOUTH, getContentPane());

        getRootPane().registerKeyboardAction(e -> view.reload(), KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                view.stopStreaming();
            }
        });

        {
            var pnl = new JPanel();
            add(pnl);
            l.putConstraint(SpringLayout.EAST, pnl, -5, SpringLayout.EAST, getContentPane());
            l.putConstraint(SpringLayout.SOUTH, pnl, 0, SpringLayout.SOUTH, getContentPane());
            {
                var cmd = new JButton("OK");
                cmd.setPreferredSize(new Dimension(150, 35));
                cmd.addActionListener(e -> formAcceptCloseHandler.accept());
                pnl.add(cmd);
            }
            {
                var cmd = new JButton("Close");
                cmd.setPreferredSize(new Dimension(150, 35));
                cmd.addActionListener(e -> formAcceptCloseHandler.close());
                pnl.add(cmd);
            }
        }
    }
}
