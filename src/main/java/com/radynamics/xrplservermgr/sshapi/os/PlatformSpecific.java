package com.radynamics.xrplservermgr.sshapi.os;

import com.radynamics.xrplservermgr.sshapi.SshApiException;
import com.radynamics.xrplservermgr.xrpl.XrplBinaryPackage;

import java.util.List;

public interface PlatformSpecific {
    boolean installed(String packageName) throws SshApiException;

    List<XrplBinaryPackage> available(String packageName) throws SshApiException;
}
