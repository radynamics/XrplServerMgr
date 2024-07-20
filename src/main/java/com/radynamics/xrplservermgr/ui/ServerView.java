package com.radynamics.xrplservermgr.ui;

import com.jcraft.jsch.JSchException;
import com.radynamics.xrplservermgr.sshapi.*;
import com.radynamics.xrplservermgr.ui.contentview.MenuItem;
import com.radynamics.xrplservermgr.ui.contentview.*;
import com.radynamics.xrplservermgr.ui.logview.RippledProvider;
import com.radynamics.xrplservermgr.utils.AppendListener;
import com.radynamics.xrplservermgr.utils.MapAppender;
import com.radynamics.xrplservermgr.xrpl.XrplBinary;
import com.radynamics.xrplservermgr.xrpl.XrplBinaryFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;

import javax.swing.*;
import java.awt.*;

public class ServerView extends JPanel implements TabPage, AppendListener, ActionLogListener, ConnectionStateListener, MenuItemListener {
    private final static Logger log = LogManager.getLogger(ServerView.class);
    private final JPanel pnlButtons;
    private final JButton cmdConnect;
    private final ContentContainer contentContainer;

    private final ConnectionInfo conn;
    private final JFrame parent;
    private final MenuView menuView;
    private final StatusView statusView;
    private SshSession session;
    private XrplBinary xrplBinary;
    private SystemMonitor systemMonitor;
    private SshSession sessionWithoutActionListener;
    private XrplBinary xrplBinaryWithoutActionListener;
    private SystemMonitor systemMonitorWithoutActionListener;
    private char[] sudoPassword;

    public ServerView(JFrame parent, ConnectionInfo conn) {
        this.parent = parent;
        this.conn = conn;

        attachLogger();

        var sp = new SpringLayout();
        setLayout(sp);

        var left = new JPanel();
        add(left);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        sp.putConstraint(SpringLayout.WEST, left, 0, SpringLayout.WEST, this);
        sp.putConstraint(SpringLayout.NORTH, left, 0, SpringLayout.NORTH, this);
        left.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        left.add(new TitlePane("MANAGEMENT"));
        menuView = new MenuView();
        left.add(menuView);
        menuView.addMenuListener(this);

        left.add(Box.createVerticalStrut(50));

        left.add(new TitlePane("INFORMATION"));
        statusView = new StatusView(conn);
        left.add(statusView);

        pnlButtons = new JPanel();
        add(pnlButtons);
        pnlButtons.setLayout(new FlowLayout());
        sp.putConstraint(SpringLayout.WEST, pnlButtons, 0, SpringLayout.EAST, left);
        sp.putConstraint(SpringLayout.NORTH, pnlButtons, 0, SpringLayout.NORTH, this);
        sp.putConstraint(SpringLayout.EAST, pnlButtons, 0, SpringLayout.EAST, this);
        pnlButtons.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        showTopButtons(false);

        cmdConnect = appendButton("Reconnect", () -> {
            try {
                openSession();
            } catch (JSchException e) {
                outputError(e.getMessage());
            }
        });

        {
            contentContainer = new ContentContainer();
            add(contentContainer);
            sp.putConstraint(SpringLayout.WEST, contentContainer, 0, SpringLayout.EAST, left);
            sp.putConstraint(SpringLayout.NORTH, contentContainer, 0, SpringLayout.SOUTH, pnlButtons);
            sp.putConstraint(SpringLayout.EAST, contentContainer, 0, SpringLayout.EAST, this);
            sp.putConstraint(SpringLayout.SOUTH, contentContainer, 0, SpringLayout.SOUTH, this);
        }
    }

    private void showTopButtons(boolean visible) {
        pnlButtons.setPreferredSize(new Dimension(Integer.MAX_VALUE, visible ? 60 : 0));
    }

    private JButton appendButton(String caption, Runnable r) {
        var cmd = new JButton(caption);
        cmd.setPreferredSize(new Dimension(150, 50));
        cmd.setMinimumSize(cmd.getPreferredSize());
        cmd.setMaximumSize(cmd.getPreferredSize());
        pnlButtons.add(cmd);
        cmd.addActionListener(e -> r.run());
        return cmd;
    }

    private char[] sudoPassword() {
        if (sudoPassword == null) {
            var input = new PasswordInput();
            var ret = input.showSudo(this);
            if (ret == JOptionPane.CANCEL_OPTION || ret == JOptionPane.CLOSED_OPTION) {
                return null;
            }

            if (input.password().length == 0 || !systemMonitor.canSudo(input.password())) {
                return sudoPassword();
            }

            sudoPassword = input.password();
        }
        return sudoPassword;
    }

    private void attachLogger() {
        ((org.apache.logging.log4j.core.Logger) log).getAppenders().values().forEach(a -> {
            if (a.getClass().equals(MapAppender.class)) {
                var o = (MapAppender) a;
                o.addAppendListener(this);
            }
        });
    }

    @Override
    public void onAppend(LogEvent event) {
        var level = java.util.List.of(Level.FATAL, Level.ERROR).contains(event.getLevel()) ? ActionLogLevel.Error : ActionLogLevel.Info;
        appendOutput(event.getMessage().getFormattedMessage(), level);
    }

    @Override
    public void onEvent(ActionLogEvent event) {
        appendOutput(event.message(), event.level());
    }

    private void outputError(String message) {
        appendOutput(message, ActionLogLevel.Error);
    }

    private void appendOutput(String message, ActionLogLevel level) {
        SwingUtilities.invokeLater(() -> contentContainer.actionOutput().append(message, level));
    }

    public void openSession() throws JSchException {
        session = new SshSession(this::sudoPassword, conn);
        session.addActionLogListener(this);
        session.addConnectionStateListener(this);
        session.open();
        systemMonitor = new SystemMonitor(session);
        systemMonitor.addActionLogListener(this);

        xrplBinary = XrplBinaryFactory.create(session, systemMonitor, new ChownMessageBox(this));
        xrplBinary.addActionLogListener(this);

        sessionWithoutActionListener = new SshSession(this::sudoPassword, conn);
        sessionWithoutActionListener.open();
        systemMonitorWithoutActionListener = new SystemMonitor(sessionWithoutActionListener);
        xrplBinaryWithoutActionListener = XrplBinaryFactory.create(sessionWithoutActionListener, systemMonitorWithoutActionListener, new ChownMessageBox(this));

        statusView.init(systemMonitorWithoutActionListener, xrplBinaryWithoutActionListener);
        statusView.refresh();

        contentContainer.initSession(session, systemMonitor, xrplBinary);
    }

    public void close() {
        statusView.init(systemMonitorWithoutActionListener, null);
        if (xrplBinary != null) {
            xrplBinary = null;
        }
        if (session != null) {
            session.close();
            session = null;
        }
        if (xrplBinaryWithoutActionListener != null) {
            xrplBinaryWithoutActionListener = null;
        }
        if (sessionWithoutActionListener != null) {
            sessionWithoutActionListener.close();
            sessionWithoutActionListener = null;
        }
    }

    @Override
    public void onConnected() {
        enableButtons(true);
        cmdConnect.setEnabled(false);
        showTopButtons(false);
    }

    private void enableButtons(boolean enabled) {
        for (var c : pnlButtons.getComponents()) {
            c.setEnabled(enabled);
        }
        menuView.setEnabled(enabled);
        statusView.setEnabled(enabled);
        contentContainer.setEnabled(enabled);
    }

    @Override
    public void onDisconnected() {
        enableButtons(false);
        cmdConnect.setEnabled(true);
        showTopButtons(true);
    }

    @Override
    public void onOpen(MenuItem item) {
        ContentView view;
        switch (item) {
            case ServerStatus -> {
                var v = new ServerStatus(parent, conn);
                v.addServerStatusListener(() -> {
                    close();
                    try {
                        openSession();
                    } catch (JSchException e) {
                        outputError("Could not reopen session. " + e.getMessage());
                    }
                });
                view = v;
            }
            case Amendments -> view = new AmendmentsView(parent);
            case Configuration -> view = new ConfigurationView(parent, conn.host());
            case Logs -> view = createLogView();
            case Peers -> view = new PeersView(parent);
            case CommandLine -> view = new CommandLineView(parent);
            default -> view = new Placeholder(parent, item.name());
        }

        view.init(contentContainer.actionOutput(), statusView, systemMonitor);
        view.initSession(session, systemMonitor, xrplBinary);
        view.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        contentContainer.show(view);
    }

    private ContentView createLogView() {
        try {
            xrplBinaryWithoutActionListener.refresh();
        } catch (Exception e) {
            outputError(e.getMessage());
        }
        return new LogsView(parent, new RippledProvider(xrplBinaryWithoutActionListener));
    }
}
