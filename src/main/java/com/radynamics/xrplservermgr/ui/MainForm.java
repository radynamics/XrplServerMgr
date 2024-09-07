package com.radynamics.xrplservermgr.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.jcraft.jsch.JSchException;
import com.radynamics.xrplservermgr.VersionController;
import com.radynamics.xrplservermgr.sshapi.ConnectionInfo;
import com.radynamics.xrplservermgr.ui.contentview.MenuItem;
import com.radynamics.xrplservermgr.ui.logview.FileProvider;
import com.radynamics.xrplservermgr.ui.logview.LogViewerView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.List;
import java.util.function.BiConsumer;

public class MainForm extends JFrame {
    private final static Logger log = LogManager.getLogger(MainForm.class);

    private final JTabbedPane tabbedPane;
    private final ServerPanel serverPanel = new ServerPanel();

    public final static String appTitle = "XRPL Server Manager";

    public MainForm() {
        var vc = new VersionController();
        setTitle(String.format("%s [%s]", appTitle, vc.getVersion()));

        var mainContentBorder = new EmptyBorder(0, 10, 10, 10);

        setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
        tabbedPane = new JTabbedPane();
        add(tabbedPane);
        tabbedPane.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_CLOSE_TOOLTIPTEXT, "Close");
        tabbedPane.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_CLOSE_CALLBACK, (BiConsumer<JTabbedPane, Integer>) (tabPane, tabIndex) -> {
            ((TabPage) tabbedPane.getComponentAt(tabIndex)).close();
            tabbedPane.removeTabAt(tabIndex);
        });
        tabbedPane.getModel().addChangeListener(e -> serverPanel.setActive(serverPanel.equals(tabbedPane.getSelectedComponent())));
        {
            serverPanel.setBorder(mainContentBorder);
            serverPanel.addServerPanelListener(this::onServerConnect);
            tabbedPane.addTab("", new FlatSVGIcon("img/home.svg", 16, 16), serverPanel);

            var toolBar = new JToolBar();
            toolBar.setFloatable(false);
            toolBar.setBorder(null);
            toolBar.add(Box.createHorizontalGlue());
            {
                var cmd = new JButton(new FlatSVGIcon("img/info.svg", 16, 16));
                toolBar.add(cmd);
                cmd.addActionListener(e -> onShowInfoClick());
            }
            tabbedPane.putClientProperty(FlatClientProperties.TABBED_PANE_TRAILING_COMPONENT, toolBar);
        }

        setDropTarget(new DropTarget() {
            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    var droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    if (droppedFiles.size() == 0) {
                        return;
                    }
                    onFileDropped(droppedFiles.get(0));
                } catch (Exception e) {
                    ExceptionDialog.show(getComponent(), e);
                }
            }
        });
    }

    private void onServerConnect(ConnectionInfo conn) {
        var v = new ServerView(this, conn);
        addAndShowTab(conn.name(), v);
        try {
            v.openSession();
            v.onOpen(MenuItem.ServerStatus);
        } catch (JSchException e) {
            ExceptionDialog.show(this, e);
        }
    }

    private void onFileDropped(File file) {
        openLogViewer(file);
    }

    public void openLogViewer(File file) {
        var v = new LogViewerView(this, new FileProvider(file.getAbsolutePath()));
        addAndShowTab(file.getName(), v);
        v.reload();
    }

    private void onShowInfoClick() {
        addAndShowTab("About", new AboutView());
    }

    private void addAndShowTab(String title, JPanel view) {
        view.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_CLOSABLE, true);
        tabbedPane.addTab(title, view);
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
    }
}
