package com.radynamics.xrplservermgr.sshapi;

import com.jcraft.jsch.*;
import com.radynamics.xrplservermgr.utils.Utils;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;

public class SshSession implements AutoCloseable {
    private final Supplier<String> getSudoPassword;
    private final String host;
    private final int port;
    private final String user;
    private final String password;
    private com.jcraft.jsch.Session session;
    private final Timer connectionMonitorTimer = new Timer("ConnectionMonitor");
    private boolean previouslyConnected;
    private final ArrayList<ActionLogListener> listener = new ArrayList<>();
    private final ArrayList<ConnectionStateListener> stateListener = new ArrayList<>();

    private final Duration infinite = Duration.ofHours(1);

    public SshSession(Supplier<String> getSudoPassword, ConnectionInfo conn) {
        this(getSudoPassword, conn.host(), conn.port(), conn.username(), conn.password());
    }

    public SshSession(Supplier<String> getSudoPassword, String host, int port, String user, String password) {
        this.getSudoPassword = getSudoPassword;
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    public void open() throws JSchException {
        raiseOnEvent(ActionLogEvent.info("Connecting to %s... (port %s)".formatted(host, port)));

        var config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        var jsch = new JSch();
        session = jsch.getSession(user, host, port);
        session.setPassword(password);
        session.setConfig(config);
        session.setTimeout(2000);
        session.connect();

        var task = new TimerTask() {
            public void run() {
                if (previouslyConnected && !session.isConnected()) {
                    previouslyConnected = false;
                    raiseOnEvent(ActionLogEvent.error("Connection lost"));
                    raiseOnDisconnected();
                    return;
                }
                if (!previouslyConnected && session.isConnected()) {
                    previouslyConnected = true;
                    raiseOnEvent(ActionLogEvent.info("Connected"));
                    raiseOnConnected();
                    return;
                }
            }
        };
        connectionMonitorTimer.scheduleAtFixedRate(task, 0, 1000);
    }

    @Override
    public void close() {
        connectionMonitorTimer.cancel();
        if (session != null) {
            if (session.isConnected()) {
                session.disconnect();
                raiseOnEvent(ActionLogEvent.info("Disconnected from %s".formatted(host)));
            }
            session = null;
        }
    }

    public Response execute(String command) throws SshApiException {
        return execute(command, null, infinite);
    }

    public Response executeSudo(String command) throws SshApiException {
        var sudoPassword = getSudoPassword.get();
        if (sudoPassword == null) {
            throw new SshApiException("No sudo password present or given password is wrong.");
        }
        return execute("sudo -S -p '' " + command, sudoPassword, infinite);
    }

    Response execute(String command, String sudoPassword, Duration timeout) throws SshApiException {
        ChannelExec channel = null;
        try {
            raiseOnEvent(ActionLogEvent.info(command));

            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);

            var outputBuffer = new ByteArrayOutputStream();
            var errorBuffer = new ByteArrayOutputStream();

            var in = channel.getInputStream();
            var out = channel.getOutputStream();
            var err = channel.getExtInputStream();
            var exitStatus = 0;

            channel.connect();

            if (sudoPassword != null) {
                out.write((sudoPassword + "\n").getBytes());
                out.flush();
            }

            var start = new Date();
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    outputBuffer.write(tmp, 0, i);
                }
                while (err.available() > 0) {
                    int i = err.read(tmp, 0, 1024);
                    if (i < 0) break;

                    var tmpErrorBuffer = new ByteArrayOutputStream();
                    tmpErrorBuffer.write(tmp, 0, i);
                    var errorText = tmpErrorBuffer.toString(StandardCharsets.UTF_8);
                    if (errorText.startsWith("[sudo] password for")) {
                        break;
                    }
                    if (containsWrongPassword(errorText)) {
                        return Response.error(errorText);
                    }
                    errorBuffer.write(tmp, 0, i);
                    raiseOnEvent(ActionLogEvent.info(errorText));
                }
                if (channel.isClosed()) {
                    if (in.available() > 0 || err.available() > 0) continue;
                    exitStatus = channel.getExitStatus();
                    break;
                }
                var end = new Date();
                var elapsed = end.getTime() - start.getTime();
                if (elapsed >= timeout.toMillis()) {
                    throw new SshApiException("Command timeout");
                }
                try {
                    Thread.sleep(100);
                } catch (Exception ee) {
                }
            }

            var response = outputBuffer.toString(StandardCharsets.UTF_8);
            var error = errorBuffer.toString(StandardCharsets.UTF_8);
            if (!StringUtils.isEmpty(error)) {
                // installing rippled also prints warnings to error out.
                raiseOnEvent(ActionLogEvent.warn(error));
            }

            var r = exitStatus == 0 ? Response.success(response).errorOutput(error) : Response.error(response);
            return r.exitStatus(exitStatus);
        } catch (JSchException | IOException e) {
            throw new SshApiException(e);
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
        }
    }

    private boolean containsWrongPassword(String response) {
        return response.contains("Sorry, try again");
    }

    public void get(String remotePath, OutputStream dst) throws SshApiException {
        get(remotePath, dst, null, null);
    }

    public void get(String remotePath, OutputStream dst, Long fileSizeBytes, ProgressListener l) throws SshApiException {
        ChannelSftp channel = null;
        try {
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();

            SftpProgressMonitor monitor = null;
            if (l != null && fileSizeBytes != null) {
                monitor = new SftpProgressMonitor() {
                    @Override
                    public void init(int op, String src, String dest, long max) {
                    }

                    @Override
                    public boolean count(long count) {
                        l.onProgress(Utils.bytesToKb(count).intValue(), Utils.bytesToKb(fileSizeBytes).intValue());
                        return true;
                    }

                    @Override
                    public void end() {
                        l.onCompleted();
                    }
                };
            }
            channel.get(remotePath, dst, monitor);
        } catch (JSchException | SftpException e) {
            throw new SshApiException(e);
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
        }
    }

    public void put(String remotePath, InputStream in) throws SshApiException {
        ChannelSftp channel = null;
        try {
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();
            channel.put(in, remotePath, ChannelSftp.OVERWRITE);
        } catch (JSchException | SftpException e) {
            if (e.getMessage().equalsIgnoreCase("permission denied")) {
                throw new FilePermissionDeniedException("Permission denied: put %s".formatted(remotePath), e);
            } else {
                throw new SshApiException(e);
            }
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
        }
    }

    public String user() {
        return user;
    }

    public int port() {
        return port;
    }

    public void addActionLogListener(ActionLogListener l) {
        listener.add(l);
    }

    public void addConnectionStateListener(ConnectionStateListener l) {
        stateListener.add(l);
    }

    private void raiseOnConnected() {
        for (var l : stateListener) {
            l.onConnected();
        }
    }

    private void raiseOnDisconnected() {
        for (var l : stateListener) {
            l.onDisconnected();
        }
    }

    private void raiseOnEvent(ActionLogEvent event) {
        for (var l : listener) {
            l.onEvent(event);
        }
    }
}
