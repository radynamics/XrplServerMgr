package com.radynamics.xrplservermgr.xrpl.rippled;

import com.radynamics.xrplservermgr.sshapi.*;
import com.radynamics.xrplservermgr.xrpl.*;
import com.radynamics.xrplservermgr.xrpl.parser.*;
import com.radynamics.xrplservermgr.xrpl.rippled.portablebuild.PortableBuildInstaller;
import com.radynamics.xrplservermgr.xrpl.rippled.ripplebinaries.RippleBinaryInstaller;
import com.radynamics.xrplservermgr.xrpl.xahaud.XahaudInstaller;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Rippled implements XrplBinary {
    private final SshSession session;
    private final SystemMonitor systemMonitor;
    private final ChownRemotePath chownRemotePath;
    private ServerInfo serverInfo;
    private Features features;
    private Peers peers;
    private ConfigCfg config;
    private String configPath;
    private ValidatorsTxt validatorsTxt;
    private String validatorsTxtPath;
    private String serverLogRemotePath;
    private String serverDatabasePath;
    private String processRemotePath;
    private String packageName;
    private String processName = "rippled";
    private String configFileName = "rippled.cfg";
    // https://xrpl.org/diagnosing-problems.html#check-the-server-log
    private String serverLogDefaultRemotePath = "/var/log/rippled/debug.log";
    private String walletDbFileName = "wallet.db";
    private List<Amendment> knownAmendments;
    private final ArrayList<ActionLogListener> listener = new ArrayList<>();

    private static final String PACKAGE_NAME = "rippled";

    public Rippled(SshSession session, SystemMonitor systemMonitor, ChownRemotePath chownRemotePath) {
        this.session = session;
        this.systemMonitor = systemMonitor;
        this.chownRemotePath = chownRemotePath;
        this.packageName = PACKAGE_NAME;
        this.knownAmendments = Amendments.all();
    }

    public void refresh() throws SshApiException, RippledCommandException {
        if (!installed()) {
            raiseOnEvent(ActionLogEvent.warn("%s is not installed".formatted(packageName)));
            return;
        }
        refreshInternal();
    }

    public void refreshInternal() throws SshApiException, RippledCommandException {
        var out = new ByteArrayOutputStream();
        session.get(configRemotePath(), out);
        config = ConfigCfg.parse(out.toString());

        if (config.debugLogFile().isPresent()) {
            serverLogRemotePath = config.debugLogFile().get();
            raiseOnEvent(ActionLogEvent.info("debug log configured in %s as %s".formatted(configFileName, serverLogRemotePath)));
        } else {
            serverLogRemotePath = serverLogDefaultRemotePath;
            raiseOnEvent(ActionLogEvent.info("debug log not found in %s. Assuming default path %s.".formatted(configFileName, serverLogRemotePath)));
        }

        if (config.databasePath().isPresent()) {
            serverDatabasePath = config.databasePath().get();
            raiseOnEvent(ActionLogEvent.info("database path configured in %s as %s".formatted(configFileName, serverDatabasePath)));
        } else {
            raiseOnEvent(ActionLogEvent.warn("database path not found in %s.".formatted(configFileName)));
        }

        if (config.validatorsTxtFile().isPresent()) {
            var tmpValidatorsTxtPath = config.validatorsTxtFile().get();
            // Relative path -> same folder as rippled.cfg
            var configFolder = configRemotePath().substring(0, configRemotePath().lastIndexOf(configFileName));
            validatorsTxtPath = tmpValidatorsTxtPath.contains(remotePathSeparator())
                    ? tmpValidatorsTxtPath
                    : configFolder + tmpValidatorsTxtPath;
            raiseOnEvent(ActionLogEvent.info("validators.txt configured in %s as %s".formatted(configFileName, validatorsTxtPath)));

            var tmpOut = new ByteArrayOutputStream();
            session.get(validatorsTxtPath, tmpOut);
            validatorsTxt = ValidatorsTxt.parse(tmpOut.toString());
        } else {
            raiseOnEvent(ActionLogEvent.info("validators.txt not found in %s.".formatted(configFileName)));
        }

        serverInfo = ServerInfo.parse(createCommandExecutor().execute("server_info"));
        refreshFeatures();
        refreshPeers();
    }

    private String remotePathSeparator() {
        if (serverLogRemotePath.contains("/")) return "/";
        if (serverLogRemotePath.contains("\\")) return "\\";
        return "/";
    }

    @Override
    public String serverLog(ProgressListener l) throws SshApiException {
        var remotePath = serverLogRemotePath();
        var fileSize = systemMonitor.fileSizeBytes(remotePath);
        var out = new ByteArrayOutputStream();
        session.get(remotePath, out, fileSize, l);
        return out.toString();
    }

    @Override
    public String serverLogRecent(int count) throws SshApiException {
        var result = session.execute("tail -n %s %s".formatted(count, serverLogRemotePath()));
        return result.asString();
    }

    @Override
    public void deleteDatabase() throws SshApiException {
        if (serverDatabasePath == null) {
            throw new SshApiException("Cannot delete database due path on server is unknown.");
        }
        systemMonitor.remove(serverDatabasePath, true);
    }

    @Override
    public void deleteDebugLog() throws SshApiException {
        if (serverLogRemotePath == null) {
            throw new SshApiException("Cannot delete log file due path on server is unknown.");
        }
        systemMonitor.remove(serverLogRemotePath, false);
    }

    @Override
    public XrplPaths remotePaths() throws SshApiException {
        return new XrplPaths(processRemotePath(), configRemotePath(), serverLogRemotePath, serverDatabasePath, validatorsTxtPath);
    }

    public SshSession session() {
        return session;
    }

    public SystemMonitor systemMonitor() {
        return systemMonitor;
    }

    private String serverLogRemotePath() {
        return serverLogRemotePath;
    }

    public void serverLogDefaultRemotePath(String serverLogDefaultRemotePath) {
        this.serverLogDefaultRemotePath = serverLogDefaultRemotePath;
    }

    public void refreshFeatures() throws SshApiException, RippledCommandException {
        features = Features.parse(createCommandExecutor().execute("feature"), knownAmendments);
    }

    public ServerInfo serverInfo() {
        return serverInfo;
    }

    public Features features() {
        return features;
    }

    public void vote(Feature feature, Vote vote) throws RippledCommandException, SshApiException {
        assertHasHashOrName(feature);
        if (vote == null) throw new RippledCommandException("A specific vote must be set. null is not supported.");
        createCommandExecutor().execute("feature %s %s".formatted(feature.hashOrName(), vote.asString()));
    }

    @Override
    public void refreshPeers() throws SshApiException, RippledCommandException {
        var json = createCommandExecutor().execute("peers");
        peers = Peers.parse(json);
    }

    @Override
    public Peers peers() {
        return peers;
    }

    private static void assertHasHashOrName(Feature feature) throws RippledCommandException {
        if (StringUtils.isEmpty(feature.hashOrName())) {
            throw new RippledCommandException("Feature must have hash or name.");
        }
    }

    @Override
    public XrplType type() {
        return XrplType.XrpLedger;
    }

    public String packageName() {
        return packageName;
    }

    public void packageName(String value) {
        this.packageName = value;
    }

    @Override
    public String processName() {
        return processName;
    }

    @Override
    public String displayName() {
        return "XRP Ledger";
    }

    public void processName(String value) {
        this.processName = value;
    }

    @Override
    public String configFileName() {
        return configFileName;
    }

    public void configFileName(String value) {
        this.configFileName = value;
    }

    public List<Amendment> knownAmendments() {
        return knownAmendments;
    }

    public void knownAmendments(List<Amendment> amendments) {
        this.knownAmendments = amendments;
    }

    @Override
    public XrplCommandExecutor createCommandExecutor() throws SshApiException {
        return new XrplCommandExecutor(session, processRemotePath(), configRemotePath());
    }

    public boolean installed() throws SshApiException {
        return processRemotePath() != null;
    }

    @Override
    public List<ReleaseChannel> updateChannels() {
        var list = new ArrayList<ReleaseChannel>();
        for (var c : UpdateChannel.all()) {
            list.add(new ReleaseChannel(c.name()));
        }
        return list;
    }

    public static boolean installed(SystemMonitor systemMonitor) throws SshApiException {
        return systemMonitor.installed(PACKAGE_NAME);
    }

    @Override
    public XrplInstaller createInstaller() {
        return new RippleBinaryInstaller(session, this, systemMonitor);
        //return new PortableBuildInstaller(session, this, systemMonitor);
    }

    public void config(ConfigCfg config) throws SshApiException {
        try {
            session.put(configRemotePath(), config.asInputStream());
        } catch (FilePermissionDeniedException e) {
            if (chownRemotePath.doChown(configRemotePath(), session.user())) {
                chownConfigPath();
                session.put(configRemotePath(), config.asInputStream());
            } else {
                throw e;
            }
        }
        this.config = config;
        raiseOnEvent(ActionLogEvent.info("%s updated".formatted(configRemotePath())));
    }

    @Override
    public ValidatorsTxt validatorsTxt() {
        return validatorsTxt;
    }

    @Override
    public String walletDbFileName() {
        return walletDbFileName;
    }

    @Override
    public ByteArrayOutputStream walletDb() throws SshApiException {
        var out = new ByteArrayOutputStream();
        session.get(walletDbRemotePath(), out);
        return out;
    }

    private String walletDbRemotePath() {
        return serverDatabasePath + remotePathSeparator() + walletDbFileName();
    }

    @Override
    public void validatorsTxt(ValidatorsTxt validatorsTxt) throws SshApiException {
        if (validatorsTxtPath == null) {
            throw new SshApiException("Cannot put validators.txt to server due its location on the server is unknown.");
        }

        try {
            session.put(validatorsTxtPath, validatorsTxt.asInputStream());
        } catch (FilePermissionDeniedException e) {
            if (chownRemotePath.doChown(validatorsTxtPath, session.user())) {
                chownValidatorsTxt();
                session.put(validatorsTxtPath, validatorsTxt.asInputStream());
            } else {
                throw e;
            }
        }
        this.validatorsTxt = validatorsTxt;
        raiseOnEvent(ActionLogEvent.info("%s updated".formatted(validatorsTxtPath)));
    }

    @Override
    public void chownValidatorsTxt() throws SshApiException {
        systemMonitor.chown(validatorsTxtPath);
    }

    public void chownConfigPath() throws SshApiException {
        systemMonitor.chown(configPath);
    }

    public ConfigCfg config() {
        return config;
    }

    private String configRemotePath() throws SshApiException {
        if (configPath == null) {
            configPath = findConfigRemotePath();
            if (configPath == null) {
                throw new SshApiException("Could not find remote configuration path.");
            }
        }
        return configPath;
    }

    private String findConfigRemotePath() throws SshApiException {
        // Various paths (https://xrpl.org/connect-your-rippled-to-the-xrp-test-net.html)
        var candidates = systemMonitor.find(configFileName, List.of("/opt", "$HOME"));
        if (candidates.isEmpty()) {
            raiseOnEvent(ActionLogEvent.warn("%s not found".formatted(configFileName)));
            return null;
        }
        var c = candidates.get(0);
        raiseOnEvent(ActionLogEvent.info("%s found at %s".formatted(configFileName, c)));
        return c;
    }

    public String processRemotePath() throws SshApiException {
        if (processRemotePath == null) {
            processRemotePath = findProcessRemotePath();
            if (processRemotePath == null) {
                raiseOnEvent(ActionLogEvent.info("Could not find remote process path."));
                return null;
            }
        }
        return processRemotePath;
    }

    private String findProcessRemotePath() throws SshApiException {
        var candidates = systemMonitor.find(processName, List.of(
                // https://xrpl.org/install-rippled-on-ubuntu.html
                "/opt/ripple/bin",
                PortableBuildInstaller.installRoot,
                XahaudInstaller.installRoot
        ));
        if (candidates.isEmpty()) {
            raiseOnEvent(ActionLogEvent.warn("%s not found".formatted(processName)));
            return null;
        }
        var c = candidates.get(0);
        raiseOnEvent(ActionLogEvent.info("%s found at %s".formatted(processName, c)));
        return c;
    }

    public void addActionLogListener(ActionLogListener l) {
        listener.add(l);
    }

    private void raiseOnEvent(ActionLogEvent event) {
        for (var l : listener) {
            l.onEvent(event);
        }
    }
}
