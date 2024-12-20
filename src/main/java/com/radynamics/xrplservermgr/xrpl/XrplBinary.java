package com.radynamics.xrplservermgr.xrpl;

import com.radynamics.xrplservermgr.sshapi.ActionLogListener;
import com.radynamics.xrplservermgr.sshapi.ProgressListener;
import com.radynamics.xrplservermgr.sshapi.SshApiException;
import com.radynamics.xrplservermgr.xrpl.parser.*;
import com.radynamics.xrplservermgr.xrpl.rippled.RippledCommandException;
import com.radynamics.xrplservermgr.xrpl.rippled.Vote;

import java.io.ByteArrayOutputStream;
import java.util.List;

public interface XrplBinary {
    XrplType type();

    String packageName();

    String processName();

    String displayName();

    String configFileName();

    //JsonObject execute(String command) throws SshApiException, RippledCommandException;
    XrplCommandExecutor createCommandExecutor() throws SshApiException;

    void refresh() throws SshApiException, RippledCommandException;

    ConfigCfg config() throws SshApiException;

    void config(ConfigCfg config) throws SshApiException;

    ValidatorsTxt validatorsTxt();

    String walletDbFileName();

    ByteArrayOutputStream walletDb() throws SshApiException;

    void validatorsTxt(ValidatorsTxt validatorsTxt) throws SshApiException;

    void chownValidatorsTxt() throws SshApiException;

    ServerInfo serverInfo();

    Features features();

    List<Amendment> knownAmendments();

    void refreshFeatures() throws SshApiException, RippledCommandException;

    void vote(Feature feature, Vote vote) throws RippledCommandException, SshApiException;

    void refreshPeers() throws SshApiException, RippledCommandException;

    Peers peers();

    void addActionLogListener(ActionLogListener l);

    boolean installed() throws SshApiException;

    List<ReleaseChannel> updateChannels();

    XrplInstaller createInstaller();

    String serverLog(ProgressListener l) throws SshApiException;

    String serverLogRecent(int count) throws SshApiException;

    void deleteDatabase() throws SshApiException;

    void deleteDebugLog() throws SshApiException;

    XrplPaths remotePaths() throws SshApiException;

    KnownValidatorRepo knownValidatorRepo();

    Uris uris();
}
