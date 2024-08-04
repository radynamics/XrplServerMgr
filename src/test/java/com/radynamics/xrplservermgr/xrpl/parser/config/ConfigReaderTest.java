package com.radynamics.xrplservermgr.xrpl.parser.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConfigReaderTest {
    @Test
    public void read() {
        var reader = new ConfigReader();
        reader.read("# Comment line1\n" +
                "# Comment line2\n" +
                "\n" +
                "[debug_logfile]\n" +
                "/var/log/rippled/debug.log\n" +
                "\n" +
                "[rpc]\n" +
                "port = 5005");

        Assertions.assertEquals(1, reader.sections("debug_logfile").size());
        Assertions.assertEquals(1, reader.sections("rpc").size());
    }
}
