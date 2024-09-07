package com.radynamics.xrplservermgr.ui.logview;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.radynamics.xrplservermgr.ui.*;
import com.radynamics.xrplservermgr.xrpl.parser.debuglog.LogEvent;
import com.radynamics.xrplservermgr.xrpl.parser.debuglog.LogParser;
import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.TableFilterHeader;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.stream.Collectors;

public class LogViewerView extends JPanel implements TabPage {
    private final JFrame owner;
    private final JCheckBox chkStreamLogs;
    private ProgressBarDialog progressBarDialog;
    private LogProvider provider;
    private final JButton _cmdRefresh;
    private final LogEventTableModel model;
    private String datasource = "";
    private LogStreamProvider streamingProvider;

    public LogViewerView(JFrame owner, LogProvider provider) {
        this.owner = owner;
        this.provider = provider;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        var toolbar = new JToolBar();
        add(toolbar);
        toolbar.setAlignmentX(Component.LEFT_ALIGNMENT);
        {
            var cmd = new JButton("open...");
            toolbar.add(cmd);
            cmd.setIcon(new FlatSVGIcon("img/open.svg"));
            cmd.addActionListener(e -> onOpenClick());
        }
        {
            var cmd = new JButton("save...");
            toolbar.add(cmd);
            cmd.setIcon(new FlatSVGIcon("img/save.svg"));
            cmd.addActionListener(e -> onSaveClick());
        }
        {
            _cmdRefresh = new JButton("refresh");
            toolbar.add(_cmdRefresh);
            _cmdRefresh.setIcon(new FlatSVGIcon("img/refresh.svg"));
            _cmdRefresh.addActionListener(e -> refresh());
        }
        {
            chkStreamLogs = new JCheckBox("stream logs");
            toolbar.add(chkStreamLogs);
            chkStreamLogs.addItemListener(this::streamLogsChanged);
        }
        refreshEnabled();

        model = new LogEventTableModel();
        var table = new JTable(model);
        final var filterHeader = new TableFilterHeader(table, AutoChoices.ENABLED);

        table.setAutoCreateColumnsFromModel(false);
        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    var value = String.valueOf(filterHeader.getTable().getValueAt(table.getSelectedRow(), LogEventTableModel.COL_MESSAGE));
                    show(value);
                }
            }
        });
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(0).setCellRenderer(new SeverityCellRenderer());
        table.getColumnModel().getColumn(1).setPreferredWidth(130);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(500);

        var sp = new JScrollPane(table);
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(sp);
        sp.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void refreshEnabled() {
        var streamingProvider = provider.createStreamingProvider();
        _cmdRefresh.setEnabled(streamingProvider != null);
        chkStreamLogs.setEnabled(streamingProvider != null);
    }

    private void refresh() {
        var streaming = streamingProvider != null;
        // Prevent new streaming result directly override/hide reloaded data.
        if (streaming) {
            stopStreaming();
        }
        reload();
        if (streaming) {
            startStreaming();
        }
    }

    private void streamLogsChanged(ItemEvent e) {
        if (chkStreamLogs.isSelected()) {
            streamingProvider = provider.createStreamingProvider();
            streamingProvider.addChangedListener(this::load);
            streamingProvider.start();
        } else {
            if (streamingProvider != null) {
                streamingProvider.stop();
                streamingProvider = null;
            }
        }
    }

    public void startStreaming() {
        chkStreamLogs.setSelected(true);
    }

    public void stopStreaming() {
        chkStreamLogs.setSelected(false);
    }

    private void show(String value) {
        var txt = new JTextArea(value);
        txt.setColumns(30);
        txt.setRows(15);
        txt.setEditable(false);
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);
        txt.setSize(txt.getPreferredSize().width, txt.getPreferredSize().height);
        JOptionPane.showMessageDialog(this, new JScrollPane(txt), "Event detail", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onOpenClick() {
        var fc = createFileChooser();
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        stopStreaming();
        provider = new FileProvider(fc.getSelectedFile().getAbsolutePath());
        refreshEnabled();
        reload();
    }

    private static JFileChooser createFileChooser() {
        var fc = new JFileChooser();
        var logFilter = new FileTypeFilter(".log", "Log files");
        fc.addChoosableFileFilter(logFilter);
        fc.setFileFilter(logFilter);
        return fc;
    }

    private void onSaveClick() {
        var fc = createFileChooser();
        fc.setSelectedFile(new File("debug.log"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            var writer = new BufferedWriter(new FileWriter(fc.getSelectedFile().getAbsolutePath()));
            writer.write(datasource);
            writer.close();
        } catch (IOException e) {
            ExceptionDialog.show(this, e);
        }
    }

    public void reload() {
        final var instance = this;
        try (final var ignored = new WaitCursor(owner)) {
            if (progressBarDialog != null) {
                progressBarDialog.setVisible(false);
                progressBarDialog = null;
            }
            progressBarDialog = ProgressBarDialog.create(owner);
            provider.addProgressListener(progressBarDialog);

            var t = new Thread(() -> load(provider.raw()));
            t.setUncaughtExceptionHandler((t1, e) -> {
                javax.swing.SwingUtilities.invokeLater(() -> {
                    progressBarDialog.setVisible(false);
                    ExceptionDialog.show(instance, e);
                });
            });
            t.start();
            progressBarDialog.setVisible(true);
        }
    }

    private void load(String raw) {
        datasource = raw;
        var events = LogParser.parse(datasource);
        model.setData(events.stream().sorted(Comparator.comparing(LogEvent::dateTime).reversed()).collect(Collectors.toList()));
    }

    public void setRefreshEnabled(boolean enabled) {
        _cmdRefresh.setEnabled(enabled);
    }

    @Override
    public void close() {
    }
}
