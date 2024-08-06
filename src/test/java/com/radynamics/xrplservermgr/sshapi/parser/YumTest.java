package com.radynamics.xrplservermgr.sshapi.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class YumTest {
    @Test
    public void parseUpdateList() {
        {
            var raw = "Not root, Subscription Management repositories not updated\n" +
                    "Last metadata expiration check: 0:08:09 ago on Sun 21 Jan 2024 10:00:23 AM CET.\n" +
                    "Installed Packages\n" +
                    "rippled.x86_64                    2.0.0-1.el7                     @ripple-stable\n" +
                    "Available Packages\n" +
                    "rippled.x86_64                    1.2.0-1.el7                     ripple-stable\n" +
                    "rippled.x86_64                    1.3.0-1.el7                     ripple-stable\n" +
                    "rippled.x86_64                    1.3.1-1.el7                     ripple-stable";
            var result = Yum.parseUpdateList(raw);
            Assertions.assertEquals(3, result.size());
            Assertions.assertEquals("1.3.1-1.el7", result.get(0).version());
            Assertions.assertEquals("1.3.0-1.el7", result.get(1).version());
            Assertions.assertEquals("1.2.0-1.el7", result.get(2).version());
        }
    }
}
