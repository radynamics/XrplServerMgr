package com.radynamics.xrplservermgr.sshapi.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AptCacheTest {
    @Test
    public void parse() {
        {
            var raw = "rippled:\n" +
                    "  Installed: 2.0.0-1\n" +
                    "  Candidate: 2.0.0-1\n" +
                    "  Version table:\n" +
                    "  *** 2.0.0-1 500\n" +
                    "         500 https://repos.ripple.com/repos/rippled-deb jammy/stable amd64 Packages\n" +
                    "         100 /var/lib/dpkg/status\n" +
                    "      1.12.0-1 500\n" +
                    "         500 https://repos.ripple.com/repos/rippled-deb jammy/stable amd64 Packages\n" +
                    "      1.11.0-1 500\n" +
                    "         500 https://repos.ripple.com/repos/rippled-deb jammy/stable amd64 Packages";
            var result = AptCache.parse(raw);
            Assertions.assertEquals(3, result.size());
            Assertions.assertEquals("2.0.0-1", result.get(0).version());
            Assertions.assertEquals("1.12.0-1", result.get(1).version());
            Assertions.assertEquals("1.11.0-1", result.get(2).version());
        }
    }
}
