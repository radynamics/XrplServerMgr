package com.radynamics.xrplservermgr.xrpl;

import com.radynamics.xrplservermgr.sshapi.ActionLogListener;
import com.radynamics.xrplservermgr.sshapi.SshApiException;

import java.util.List;

public interface XrplInstaller {
    void install() throws SshApiException;

    void install(XrplBinaryPackage pkg) throws SshApiException;

    List<XrplBinaryPackage> availableUpdates(ReleaseChannel channel) throws XrplApiException;

    void addActionLogListener(ActionLogListener l);

    void removeActionLogListener(ActionLogListener l);

    void addInstallerListener(XrplInstallerListener l);

    String displayName();
}
