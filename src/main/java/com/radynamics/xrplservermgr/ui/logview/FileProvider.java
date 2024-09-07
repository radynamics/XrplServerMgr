package com.radynamics.xrplservermgr.ui.logview;

import com.radynamics.xrplservermgr.sshapi.ProgressListener;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class FileProvider implements LogProvider {
    private final String name;

    private final ArrayList<ProgressListener> listener = new ArrayList<>();

    public FileProvider(String name) {
        this.name = name;
    }

    @Override
    public String raw() {
        if (name == null) {
            return "";
        }

        var sb = new StringBuilder();
        try (var br = new BufferedReader(new InputStreamReader(new FileInputStream(name), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            raiseOnCompleted();
        }
        return sb.toString();
    }

    @Override
    public LogStreamProvider createStreamingProvider() {
        return null;
    }

    private void raiseOnCompleted() {
        for (var l : listener) {
            l.onCompleted();
        }
    }

    public void addProgressListener(ProgressListener l) {
        listener.add(l);
    }
}
