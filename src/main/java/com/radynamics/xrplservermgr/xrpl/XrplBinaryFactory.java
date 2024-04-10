package com.radynamics.xrplservermgr.xrpl;

import com.radynamics.xrplservermgr.sshapi.SshApiException;
import com.radynamics.xrplservermgr.sshapi.SshSession;
import com.radynamics.xrplservermgr.sshapi.SystemMonitor;
import com.radynamics.xrplservermgr.xrpl.rippled.Rippled;
import com.radynamics.xrplservermgr.xrpl.xahaud.Xahaud;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class XrplBinaryFactory {
    private final static Logger log = LogManager.getLogger(XrplBinaryFactory.class);

    public static XrplBinary create(SshSession session, SystemMonitor systemMonitor, ChownRemotePath chownRemotePath) {
        LedgerId ledgerId;
        try {
            ledgerId = detect(systemMonitor);
        } catch (SshApiException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        if (ledgerId == null) {
            log.warn("Could not detect ledgerId. Assuming Rippled");
            ledgerId = LedgerId.Rippled;
        }

        return switch (ledgerId) {
            case Rippled -> new Rippled(session, systemMonitor, chownRemotePath);
            case Xahaud -> new Xahaud(session, systemMonitor, chownRemotePath);
        };
    }

    public static LedgerId detect(SystemMonitor systemMonitor) throws SshApiException {
        if (Rippled.installed(systemMonitor)) {
            return LedgerId.Rippled;
        }
        if (Xahaud.existsService(systemMonitor)) {
            return LedgerId.Xahaud;
        }
        return null;
    }
}
