package com.radynamics.xrplservermgr.xrpl.rippled.ripplebinaries;

import com.radynamics.xrplservermgr.sshapi.SshApiException;
import com.radynamics.xrplservermgr.xrpl.XrplBinaryPackage;
import com.radynamics.xrplservermgr.xrpl.rippled.UpdateChannel;

import java.util.Optional;

public interface RippledPlatformInstaller {

    void install(Optional<String> version) throws SshApiException;

    void changeChannel(UpdateChannel channel) throws SshApiException;

    void update(XrplBinaryPackage pkg) throws SshApiException;
}
