package com.radynamics.xrplservermgr.ui;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.radynamics.xrplservermgr.db.ServerRepo;
import com.radynamics.xrplservermgr.db.dto.Server;
import com.radynamics.xrplservermgr.newsfeed.NewsfeedEntry;
import com.radynamics.xrplservermgr.newsfeed.NewsfeedException;
import com.radynamics.xrplservermgr.newsfeed.NewsfeedJsonProvider;
import com.radynamics.xrplservermgr.sshapi.ConnectionInfo;
import com.radynamics.xrplservermgr.sshapi.PasswordRequest;
import com.radynamics.xrplservermgr.sshapi.SecretStorePasswordRequest;
import com.radynamics.xrplservermgr.ui.backgroundimage.BackgroundImageListener;
import com.radynamics.xrplservermgr.ui.backgroundimage.BackgroundImageProvider;
import com.radynamics.xrplservermgr.ui.backgroundimage.LittleLedgers;
import com.radynamics.xrplservermgr.utils.RequestFocusListener;
import com.radynamics.xrplservermgr.utils.SecretStore;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URI;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ServerPanel extends BackgroundImagePanel implements BackgroundImageListener {
    private final static Logger log = LogManager.getLogger(ServerPanel.class);
    private final ArrayList<ServerPanelListener> listener = new ArrayList<>();
    private final BackgroundImageProvider backgroundImageProvider = new LittleLedgers(); //new XahauMonsters();;
    private final SectionContentPanel serverConnections;
    private final SecretStore secretStore = new SecretStore();

    public ServerPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        {
            var pnl = new JPanel();
            add(pnl);
            pnl.setOpaque(false);
            pnl.setAlignmentX(Component.LEFT_ALIGNMENT);
            pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
            pnl.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));

            {
                var lbl = new JLabel("Welcome to XRPL Server Manager");
                pnl.add(lbl);
                lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
                lbl.putClientProperty("FlatLaf.styleClass", "h00");
            }
            pnl.add(Box.createVerticalStrut(30));
            {
                var lbl = new JLabel("XRPL Server Manager is a graphical user interface (GUI) tool for XRPL (rippled) and Xahau (xahaud) servers. With its various tools it facilitates installing and maintaining your nodes.");
                pnl.add(lbl);
                lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            }
            pnl.add(Box.createVerticalStrut(50));
            {
                var links = new JPanel();
                pnl.add(links);
                links.setOpaque(false);
                links.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
                links.setMaximumSize(new Dimension(pnl.getMaximumSize().width, 30));

                links.add(Utils.createLinkLabel(this, "Browse XRPL Documentation...", true, URI.create("https://xrpl.org/docs/infrastructure/")));
                links.add(Utils.createLinkLabel(this, "Browse Xahau Documentation...", true, URI.create("https://docs.xahau.network/")));
                links.add(Utils.createLinkLabel(this, "View project GitHub...", true, URI.create("https://github.com/radynamics/XrplServerMgr")));
            }
        }

        {
            var l = new SpringLayout();
            var p = new JPanel(l);
            add(p);
            p.setAlignmentX(Component.LEFT_ALIGNMENT);
            p.setOpaque(false);

            var newsfeed = new SectionContentPanel("Newsfeed");
            {
                p.add(newsfeed);
                l.putConstraint(SpringLayout.NORTH, newsfeed, 0, SpringLayout.NORTH, p);
                l.putConstraint(SpringLayout.EAST, newsfeed, 0, SpringLayout.EAST, p);
                l.putConstraint(SpringLayout.SOUTH, newsfeed, 0, SpringLayout.SOUTH, p);

                newsfeed.contentPanel().setLayout(new BoxLayout(newsfeed.contentPanel(), BoxLayout.Y_AXIS));
                final var containerWidth = 200;
                newsfeed.setPreferredSize(new Dimension(containerWidth, newsfeed.getPreferredSize().height));

                var newsProvider = new NewsfeedJsonProvider();
                try {
                    var news = newsProvider.list();
                    for (var n : news) {
                        newsfeed.contentPanel().add(createNewsfeedEntry(n, containerWidth));
                        newsfeed.contentPanel().add(Box.createVerticalStrut(10));
                    }
                } catch (NewsfeedException e) {
                    log.warn(e.getMessage(), e);
                    newsfeed.contentPanel().add(new JLabel("Could not load newsfeed."));
                }
                newsfeed.contentPanel().add(Box.createVerticalGlue());
            }
            {
                serverConnections = new SectionContentPanel("Server Connections");
                p.add(serverConnections);
                l.putConstraint(SpringLayout.WEST, serverConnections, 0, SpringLayout.WEST, p);
                l.putConstraint(SpringLayout.NORTH, serverConnections, 0, SpringLayout.NORTH, p);
                l.putConstraint(SpringLayout.EAST, serverConnections, -20, SpringLayout.WEST, newsfeed);
                l.putConstraint(SpringLayout.SOUTH, serverConnections, 0, SpringLayout.SOUTH, p);

                final var gap = 20;
                serverConnections.contentPanel().setBorder(new EmptyBorder(0, gap * -1, 0, 0));
                serverConnections.contentPanel().setLayout(new WrapLayout(FlowLayout.LEFT, gap, gap));

                {
                    var cmd = new ConnectionButton("");
                    cmd.addActionListener(e -> addConnection());
                    serverConnections.contentPanel().add(cmd);
                    cmd.setBackground(null);

                    var icon = new FlatSVGIcon("img/plus.svg", 64, 64);
                    icon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> Color.lightGray));
                    cmd.setIcon(icon);
                }

                var connections = new ArrayList<ConnectionInfo>();
                java.util.List<Server> servers = new ArrayList<>();
                try (var repo = new ServerRepo()) {
                    servers = repo.list();
                } catch (Exception e) {
                    ExceptionDialog.show(this, e);
                }

                for (var s : servers) {
                    ConnectionInfo conn;
                    if (StringUtils.isEmpty(s.keyFile())) {
                        conn = new ConnectionInfo(s, new PasswordRequest(() -> new SecretStorePasswordRequest(this, secretStore, s).password()));
                    } else {
                        conn = new ConnectionInfo(s, new PasswordRequest(() -> PasswordInput.privateKeyFile(this)));
                    }
                    connections.add(conn);
                }

                for (var i = 0; i < connections.size(); i++) {
                    var cmd = addConnectionButton(connections.get(i));
                    if (i == 0) {
                        cmd.addAncestorListener(new RequestFocusListener());
                    }
                }
            }
        }

        backgroundImageOffset(new Dimension(0, 20));
        backgroundImageProvider.addImageChangedListener(this);
        backgroundImageProvider.start();
    }

    private static JPanel createNewsfeedEntry(NewsfeedEntry news, int containerWidth) {
        var pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setOpaque(false);
        {
            var lbl = new JLabel(news.title());
            pnl.add(lbl);
            lbl.putClientProperty("FlatLaf.styleClass", "h4");
            lbl.setOpaque(false);
        }
        {
            var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());
            var lbl = new JLabel(formatter.format(news.dateTime()));
            pnl.add(lbl);
            lbl.putClientProperty("FlatLaf.styleClass", "small");
            lbl.setForeground(Consts.ColorSmallInfo);
            lbl.setOpaque(false);
        }
        {
            var lbl = Utils.formatLabel(new JTextArea());
            pnl.add(lbl);
            lbl.setText(news.text());
            lbl.setToolTipText(news.text());
            lbl.setOpaque(false);
            lbl.setMaximumSize(new Dimension(containerWidth - 10, 80));
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        {
            var lbl = Utils.createLinkLabel(pnl, "more...", true, news.more());
            pnl.add(lbl);
            lbl.setOpaque(false);
        }
        return pnl;
    }

    private JButton addConnectionButton(ConnectionInfo conn) {
        var cmd = new ConnectionButton(conn);
        var index = serverConnections.contentPanel().getComponentCount() - 1;
        serverConnections.contentPanel().add(cmd, index);
        return cmd;
    }

    private void addConnection() {
        var conn = new ConnectionInfo(Server.create("", "", 22, ""), new PasswordRequest(() -> PasswordInput.connectionPassword(this, "")));
        if (!editConnection(conn)) {
            return;
        }
        addConnectionButton(conn);
    }

    private void removeConnection(ConnectionButton button, ConnectionInfo conn) {
        var res = JOptionPane.showConfirmDialog(this, "Do you really want to delete connection %s".formatted(conn.name()), MainForm.appTitle, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (res != JOptionPane.YES_OPTION) {
            return;
        }

        try (var repo = new ServerRepo()) {
            repo.delete(conn.server());
            repo.commit();
            secretStore.connectionPasswordDelete(conn.server().uuid());
        } catch (Exception ex) {
            ExceptionDialog.show(this, ex);
        }
        serverConnections.contentPanel.remove(button);
    }

    private boolean editConnection(ConnectionInfo conn) {
        var ce = new ConnectionEdit(secretStore, conn);
        if (!ce.show(this)) {
            return false;
        }

        try (var repo = new ServerRepo()) {
            repo.saveOrUpdate(conn.server());
            repo.commit();
        } catch (Exception e) {
            ExceptionDialog.show(this, e);
        }

        return true;
    }

    private void onConnectClick(ConnectionInfo conn) {
        if (!canConnect(conn)) {
            return;
        }
        raiseConnect(conn);
    }

    private boolean canConnect(ConnectionInfo conn) {
        if (conn.canConnect()) {
            return true;
        }

        JOptionPane.showMessageDialog(this, "Could not connect to %s".formatted(conn.host()), "Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    @Override
    public void onImageChanged(Image image) {
        backgroundImage(image);
        repaint();
    }

    public void setActive(boolean active) {
        if (active) {
            this.backgroundImageProvider.start();
        } else {
            this.backgroundImageProvider.stop();
        }
    }

    public void addServerPanelListener(ServerPanelListener l) {
        listener.add(l);
    }

    private void raiseConnect(ConnectionInfo conn) {
        for (var l : listener) {
            l.onConnect(conn);
        }
    }

    private static class SectionContentPanel extends JPanel {
        private final JPanel contentPanel = new JPanel();

        public SectionContentPanel(String title) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setOpaque(false);

            var lbl = new JLabel(title);
            add(lbl);
            lbl.putClientProperty("FlatLaf.styleClass", "h3");

            var sp = new JScrollPane(contentPanel);
            add(sp);
            sp.setBorder(BorderFactory.createEmptyBorder());
            sp.getVerticalScrollBar().setUnitIncrement(16);
            sp.setAlignmentX(Component.LEFT_ALIGNMENT);
            sp.setOpaque(false);
            sp.getViewport().setOpaque(false);

            contentPanel.setOpaque(false);
        }

        public JPanel contentPanel() {
            return contentPanel;
        }
    }

    private class ConnectionButton extends JButton {
        private ConnectionInfo conn;

        public ConnectionButton(String text) {
            setText(text);
            setPreferredSize(new Dimension(250, 100));
        }

        public ConnectionButton(ConnectionInfo conn) {
            this(conn.name());
            this.conn = conn;

            addActionListener(e -> onConnectClick(this.conn));

            setMargin(new Insets(0, 0, 0, 0));
            setLayout(new FlowLayout(FlowLayout.RIGHT));

            var remove = createActionButton("img/trash.svg");
            add((remove));
            remove.addActionListener(e -> removeConnection(this, this.conn));

            var edit = createActionButton("img/pen.svg");
            add(edit);
            edit.addActionListener(e -> {
                if (!editConnection(this.conn)) {
                    return;
                }
                setText(this.conn.name());
            });
        }

        private JButton createActionButton(String name) {
            var cmd = new JButton();
            var icon = new FlatSVGIcon(name, 16, 16);
            icon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> Color.lightGray));
            cmd.setIcon(icon);
            cmd.setPreferredSize(new Dimension(20, 20));
            cmd.setFocusable(false);
            cmd.setBackground(null);
            return cmd;
        }
    }
}
