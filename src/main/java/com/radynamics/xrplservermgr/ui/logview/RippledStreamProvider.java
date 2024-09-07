package com.radynamics.xrplservermgr.ui.logview;

import com.radynamics.xrplservermgr.sshapi.SshApiException;
import com.radynamics.xrplservermgr.xrpl.XrplBinary;

import java.time.Duration;
import java.util.*;

public class RippledStreamProvider implements LogStreamProvider {
    private final XrplBinary xrplBinary;
    private final ArrayList<String> history = new ArrayList<>();
    private final Timer refreshTimer = new Timer("logRefresh");

    private final ArrayList<ChangedListener> listener = new ArrayList<>();

    public RippledStreamProvider(XrplBinary xrplBinary) {
        this.xrplBinary = xrplBinary;
    }

    public void start() {
        var refresh = new TimerTask() {
            public void run() {
                // Supporting 50 new lines per second.
                load(50);
            }
        };
        refreshTimer.scheduleAtFixedRate(refresh, 0, Duration.ofSeconds(1).toMillis());
    }

    public void stop() {
        refreshTimer.cancel();
    }

    private void load(int count) {
        try {
            var recent = xrplBinary.serverLogRecent(count);
            var newEntries = filterKnown(convertToArray(recent));
            if (newEntries.isEmpty()) {
                return;
            }

            history.addAll(newEntries);
            raiseOnChanged(raw());
        } catch (SshApiException e) {
            throw new RuntimeException(e);
        }
    }

    private static String[] convertToArray(String raw) {
        return raw.split("\n");
    }

    private static String convertToRaw(List<String> list) {
        return String.join("\n", list);
    }

    private List<String> filterKnown(String[] recent) {
        if (recent.length == 0) {
            return new ArrayList<>();
        }

        // Oldest entries are first.
        for (var i = 0; i < recent.length; i++) {
            // Line unknown -> everything from here is new
            if (!history.contains(recent[i])) {
                return List.of(Arrays.copyOfRange(recent, i, recent.length));
            }
        }
        // Every line was found -> nothing new
        return new ArrayList<>();
    }

    private void raiseOnChanged(String raw) {
        for (var l : listener) {
            l.onChanged(raw);
        }
    }

    public void addChangedListener(ChangedListener l) {
        listener.add(l);
    }

    public String raw() {
        return convertToRaw(history);
    }

    public void raw(String raw) {
        history.clear();
        history.addAll(List.of(convertToArray(raw)));
    }
}
