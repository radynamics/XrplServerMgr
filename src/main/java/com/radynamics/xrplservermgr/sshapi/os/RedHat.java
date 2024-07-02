package com.radynamics.xrplservermgr.sshapi.os;

import com.radynamics.xrplservermgr.sshapi.SshApiException;
import com.radynamics.xrplservermgr.sshapi.SshSession;
import com.radynamics.xrplservermgr.sshapi.parser.Yum;
import com.radynamics.xrplservermgr.xrpl.XrplBinaryPackage;

import java.util.ArrayList;
import java.util.List;

public class RedHat implements PlatformSpecific {
    private final SshSession session;

    public RedHat(SshSession session) {
        this.session = session;
    }

    @Override
    public boolean installed(String packageName) throws SshApiException {
        var response = session.execute("rpm -qa " + packageName).asString();
        return !response.isEmpty();
    }

    @Override
    public List<XrplBinaryPackage> available(String packageName) throws SshApiException {
        var response = session.executeSudo("yum --showduplicates list %s | expand".formatted(packageName));
        var versions = Yum.parseUpdateList(response.asString());

        var list = new ArrayList<XrplBinaryPackage>();
        for (var v : versions) {
            list.add(new XrplBinaryPackage(packageName, v.version()));
        }
        return list;
    }
}
