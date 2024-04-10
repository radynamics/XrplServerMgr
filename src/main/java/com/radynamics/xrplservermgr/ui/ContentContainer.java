package com.radynamics.xrplservermgr.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.radynamics.xrplservermgr.sshapi.SshSession;
import com.radynamics.xrplservermgr.sshapi.SystemMonitor;
import com.radynamics.xrplservermgr.ui.contentview.ContentView;
import com.radynamics.xrplservermgr.xrpl.XrplBinary;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class ContentContainer extends JPanel {
    private final JTabbedPane tabbedPane;
    private final ActionOutputView actionOutput;

    public ContentContainer() {
        setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 10));

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        tabbedPane = new JTabbedPane();
        tabbedPane.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_CLOSE_TOOLTIPTEXT, "Close");
        tabbedPane.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_TYPE, FlatClientProperties.TABBED_PANE_TAB_TYPE_CARD);
        tabbedPane.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_CLOSE_CALLBACK, (BiConsumer<JTabbedPane, Integer>) (tabPane, tabIndex) -> {
            ((ContentView) tabbedPane.getComponentAt(tabIndex)).close();
            tabbedPane.removeTabAt(tabIndex);
        });
        var outputPane = new JPanel();
        outputPane.setLayout(new BoxLayout(outputPane, BoxLayout.Y_AXIS));
        {
            var header = new JLabel("Action Output");
            outputPane.add(header);
            header.putClientProperty("FlatLaf.styleClass", "small");

            actionOutput = new ActionOutputView();
            outputPane.add(actionOutput.scrollPane());
            actionOutput.scrollPane().setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        var splitPane = new JSplitPane(SwingConstants.HORIZONTAL, tabbedPane, outputPane);
        add(splitPane);
        splitPane.setResizeWeight(0.5);
        SwingUtilities.invokeLater(() -> splitPane.setDividerLocation(0.8));
    }

    public void initSession(SshSession session, SystemMonitor systemMonitor, XrplBinary xrplBinary) {
        for (var v : contentViews()) {
            v.initSession(session, systemMonitor, xrplBinary);
        }
    }

    public void show(ContentView view) {
        var indexExisting = indexOf(view);
        if (indexExisting == -1) {
            view.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_CLOSABLE, true);
            tabbedPane.addTab(view.tabText(), view);
            tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
        } else {
            tabbedPane.setSelectedIndex(indexExisting);
        }
    }

    private int indexOf(ContentView view) {
        for (var i = 0; i < tabbedPane.getTabCount(); i++) {
            var v = tabbedPane.getComponentAt(i);
            if (v.getClass().equals(view.getClass()) && ((ContentView) v).tabText().equals(view.tabText())) {
                return i;
            }
        }
        return -1;
    }

    private List<ContentView> contentViews() {
        var list = new ArrayList<ContentView>();
        for (var i = 0; i < tabbedPane.getTabCount(); i++) {
            var v = tabbedPane.getComponentAt(i);
            if (v instanceof ContentView) {
                list.add(((ContentView) v));
            }
        }
        return list;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        for (var v : contentViews()) {
            v.setEnabled(enabled);
        }
    }

    public ActionOutputView actionOutput() {
        return actionOutput;
    }
}
