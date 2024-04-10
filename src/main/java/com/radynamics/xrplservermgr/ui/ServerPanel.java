package com.radynamics.xrplservermgr.ui;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.radynamics.xrplservermgr.newsfeed.NewsfeedEntry;
import com.radynamics.xrplservermgr.sshapi.ConnectionInfo;
import com.radynamics.xrplservermgr.ui.backgroundimage.BackgroundImageListener;
import com.radynamics.xrplservermgr.ui.backgroundimage.BackgroundImageProvider;
import com.radynamics.xrplservermgr.ui.backgroundimage.LittleLedgers;
import com.radynamics.xrplservermgr.utils.RequestFocusListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URI;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ServerPanel extends BackgroundImagePanel implements BackgroundImageListener {
    private final static Logger log = LogManager.getLogger(ServerPanel.class);
    private final ArrayList<ServerPanelListener> listener = new ArrayList<>();
    private final BackgroundImageProvider backgroundImageProvider = new LittleLedgers(); //new XahauMonsters();;

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
                var lbl = new JLabel("XRPL Server Manager is a graphical user interface (GUI) toolset for XRPL (rippled) and Xahau (xahaud) servers. With its various tools it facilitates installing and maintaining your nodes.");
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

                var news = java.util.List.of(
                        NewsfeedEntry.create(Instant.parse("2024-03-28T08:00:00Z"), "rippled 2.1.1 released", "Fixing a critical AMM bug.", URI.create("https://github.com/XRPLF/rippled/pull/4968")),
                        NewsfeedEntry.create(Instant.parse("2024-02-21T07:00:00Z"), "rippled 2.1.0 released", "Fixing a minor AMM bug.", URI.create("https://xrpl.org/blog/2024/rippled-2.1.0")),
                        NewsfeedEntry.create(Instant.parse("2024-01-30T14:00:00Z"), "rippled 2.0.1 released", "Fixing various bugs.", URI.create("https://xrpl.org/blog/2024/rippled-2.0.1"))
                );
                for (var n : news) {
                    newsfeed.contentPanel().add(createNewsfeedEntry(n, containerWidth));
                    newsfeed.contentPanel().add(Box.createVerticalStrut(10));
                }
                newsfeed.contentPanel().add(Box.createVerticalGlue());
            }
            {
                var pnl = new SectionContentPanel("Server Connections");
                p.add(pnl);
                l.putConstraint(SpringLayout.WEST, pnl, 0, SpringLayout.WEST, p);
                l.putConstraint(SpringLayout.NORTH, pnl, 0, SpringLayout.NORTH, p);
                l.putConstraint(SpringLayout.EAST, pnl, -20, SpringLayout.WEST, newsfeed);
                l.putConstraint(SpringLayout.SOUTH, pnl, 0, SpringLayout.SOUTH, p);

                final var gap = 20;
                pnl.contentPanel().setBorder(new EmptyBorder(0, gap * -1, 0, 0));
                pnl.contentPanel().setLayout(new FlowLayout(FlowLayout.LEFT, gap, gap));

                {
                    var cmd = createConnectionButton("connect VM (Ubuntu 22)", e -> onConnectClick("VM Ubuntu 22", "192.168.1.108", "vboxuser", "changeme"));
                    cmd.addAncestorListener(new RequestFocusListener());
                    pnl.contentPanel().add(cmd);
                }
                {
                    var cmd = createConnectionButton("connect VM (Ubuntu Server)", e -> onConnectClick("VM Ubuntu Server", "192.168.1.116", "rs", "changeme"));
                    pnl.contentPanel().add(cmd);
                }
                pnl.contentPanel().add(createConnectionButton("connect VM (RedHat)", e -> onConnectClick("VM RedHat", "192.168.1.114", "vboxuser", "changeme")));
                pnl.contentPanel().add(createConnectionButton("connect ubuntu01", e -> onConnectClick("PROD ubuntu01", "192.168.0.234", "rsteimen", "")));
                {
                    var cmd = createConnectionButton("", e -> onConnectClick("", "", "", ""));
                    pnl.contentPanel().add(cmd);
                    cmd.setBackground(null);

                    var icon = new FlatSVGIcon("img/plus.svg", 64, 64);
                    icon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> Color.lightGray));
                    cmd.setIcon(icon);
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
            lbl.setMaximumSize(new Dimension(containerWidth, 80));
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        {
            var lbl = Utils.createLinkLabel(pnl, "more...", true, news.more());
            pnl.add(lbl);
            lbl.setOpaque(false);
        }
        return pnl;
    }

    private static JButton createConnectionButton(String text, ActionListener onClick) {
        var cmd = new JButton(text);
        cmd.setPreferredSize(new Dimension(250, 100));
        cmd.addActionListener(onClick);
        return cmd;
    }

    private void onConnectClick(String name, String host, String user, String password) {
        onConnectClick(new ConnectionInfo(name, host, 22, user, password));
    }

    private void onConnectClick(ConnectionInfo ci) {
        var ce = new ConnectionEdit();
        var conn = ce.show(null, ci);
        if (conn == null) {
            return;
        }

        raiseConnect(conn);
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

            add(contentPanel);
            contentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPanel.setOpaque(false);
        }

        public JPanel contentPanel() {
            return contentPanel;
        }
    }
}