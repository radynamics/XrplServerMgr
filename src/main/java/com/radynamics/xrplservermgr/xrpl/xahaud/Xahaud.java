package com.radynamics.xrplservermgr.xrpl.xahaud;

import com.radynamics.xrplservermgr.sshapi.*;
import com.radynamics.xrplservermgr.xrpl.*;
import com.radynamics.xrplservermgr.xrpl.parser.*;
import com.radynamics.xrplservermgr.xrpl.rippled.Rippled;
import com.radynamics.xrplservermgr.xrpl.rippled.RippledCommandException;
import com.radynamics.xrplservermgr.xrpl.rippled.Vote;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class Xahaud implements XrplBinary {
    private final Rippled rippled;
    private final ArrayList<ActionLogListener> listener = new ArrayList<>();

    private static final String PROCESS_NAME = "xahaud";

    public Xahaud(SshSession session, SystemMonitor systemMonitor, ChownRemotePath chownRemotePath) {
        this.rippled = new Rippled(session, systemMonitor, chownRemotePath);
        this.rippled.packageName("xahaud");
        this.rippled.processName(PROCESS_NAME);
        this.rippled.serverLogDefaultRemotePath("/opt/xahaud/log/debug.log");
        this.rippled.configFileName("xahaud.cfg");
        this.rippled.knownAmendments(Amendments.all());
        this.rippled.addActionLogListener(this::raiseOnEvent);
    }

    public XrplInstaller createInstaller() {
        return new XahaudInstaller(rippled.session(), this, rippled.systemMonitor());
    }

    @Override
    public String serverLog(ProgressListener l) throws SshApiException {
        return rippled.serverLog(l);
    }

    @Override
    public String serverLogRecent(int count) throws SshApiException {
        return rippled.serverLogRecent(count);
    }

    @Override
    public void deleteDatabase() throws SshApiException {
        rippled.deleteDatabase();
    }

    @Override
    public void deleteDebugLog() throws SshApiException {
        rippled.deleteDebugLog();
    }

    public XrplPaths remotePaths() throws SshApiException {
        return rippled.remotePaths();
    }

    @Override
    public KnownValidatorRepo knownValidatorRepo() {
        switch (config().networkId()) {
            case "main":
            case "21337":
                return ValidatorRepo.main();
            case "testnet":
            case "21338":
                return ValidatorRepo.test();
            default:
                return new ValidatorRepo();
        }
    }


    @Override
    public Uris uris() {
        var uris = new Uris();
        uris.amendmentsOverview(URI.create("https://xahscan.com/amendments"));
        return uris;
    }

    @Override
    public XrplType type() {
        return XrplType.Xahau;
    }

    @Override
    public String packageName() {
        return this.rippled.packageName();
    }

    @Override
    public String processName() {
        return rippled.processName();
    }

    @Override
    public String displayName() {
        return "Xahau";
    }

    @Override
    public String configFileName() {
        return rippled.configFileName();
    }

    @Override
    public XrplCommandExecutor createCommandExecutor() throws SshApiException {
        return rippled.createCommandExecutor();
    }

    @Override
    public void refresh() throws SshApiException, RippledCommandException {
        if (!installed()) {
            raiseOnEvent(ActionLogEvent.warn("%s is not installed".formatted(rippled.packageName())));
            return;
        }
        rippled.refreshInternal();
    }

    @Override
    public ConfigCfg config() {
        return rippled.config();
    }

    @Override
    public void config(ConfigCfg config) throws SshApiException {
        rippled.config(config);
    }

    @Override
    public ValidatorsTxt validatorsTxt() {
        return rippled.validatorsTxt();
    }

    @Override
    public String walletDbFileName() {
        return rippled.walletDbFileName();
    }

    @Override
    public ByteArrayOutputStream walletDb() throws SshApiException {
        return rippled.walletDb();
    }

    @Override
    public void validatorsTxt(ValidatorsTxt validatorsTxt) throws SshApiException {
        rippled.validatorsTxt(validatorsTxt);
    }

    @Override
    public void chownValidatorsTxt() throws SshApiException {
        rippled.chownValidatorsTxt();
    }

    @Override
    public ServerInfo serverInfo() {
        return rippled.serverInfo();
    }

    @Override
    public Features features() {
        return rippled.features();
    }

    @Override
    public List<Amendment> knownAmendments() {
        return rippled.knownAmendments();
    }

    @Override
    public void refreshFeatures() throws SshApiException, RippledCommandException {
        rippled.refreshFeatures();
    }

    @Override
    public void vote(Feature feature, Vote vote) throws RippledCommandException, SshApiException {
        rippled.vote(feature, vote);
    }

    @Override
    public void refreshPeers() throws SshApiException, RippledCommandException {
        rippled.refreshPeers();
    }

    @Override
    public Peers peers() {
        return rippled.peers();
    }

    @Override
    public boolean installed() throws SshApiException {
        return rippled.installed();
    }

    @Override
    public List<ReleaseChannel> updateChannels() {
        var list = new ArrayList<ReleaseChannel>();
        for (var c : UpdateChannel.all()) {
            list.add(new ReleaseChannel(c.name()));
        }
        return list;
    }

    public static boolean existsService(SystemMonitor systemMonitor) throws SshApiException {
        return systemMonitor.existsService(PROCESS_NAME);
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
