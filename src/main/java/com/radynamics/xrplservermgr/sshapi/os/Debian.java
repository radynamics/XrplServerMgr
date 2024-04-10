package com.radynamics.xrplservermgr.sshapi.os;

import com.radynamics.xrplservermgr.sshapi.SshApiException;
import com.radynamics.xrplservermgr.sshapi.SshSession;
import com.radynamics.xrplservermgr.sshapi.parser.AptCache;
import com.radynamics.xrplservermgr.xrpl.XrplBinaryPackage;

import java.util.ArrayList;
import java.util.List;

public class Debian implements PlatformSpecific {
    private final SshSession session;

    public Debian(SshSession session) {
        this.session = session;
    }

    @Override
    public boolean installed(String packageName) throws SshApiException {
        var response = session.execute("dpkg -l | grep " + packageName).asString();
        return !response.isEmpty();
    }

    @Override
    public List<XrplBinaryPackage> available(String packageName) throws SshApiException {
        var response = session.execute("apt-cache policy %s".formatted(packageName));
        var versions = AptCache.parse(response.asString());

        var list = new ArrayList<XrplBinaryPackage>();
        for (var v : versions) {
            list.add(new XrplBinaryPackage(packageName, v.version()));
        }
        return list;
    }
}
