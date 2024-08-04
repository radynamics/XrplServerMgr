package com.radynamics.xrplservermgr.xrpl.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConfigCfgTest {
    @Test
    public void read() {
        var cfg = ConfigCfg.parse("# This needs to be an absolute directory reference, not a relative one.\n" +
                "# Modify this value as required.\n" +
                "[debug_logfile]\n" +
                "/var/log/rippled/debug.log\n" +
                "\n" +
                "[database_path]\n" +
                "/var/lib/rippled/db\n" +
                "\n" +
                "# File containing trusted validator keys or validator list publishers.\n" +
                "# Unless an absolute path is specified, it will be considered relative to the\n" +
                "# folder in which the rippled.cfg file is located.\n" +
                "[validators_file]\n" +
                "validators.txt\n" +
                "\n" +
                "[server]\n" +
                "port_rpc_admin_local\n" +
                "#port_peer\n" +
                "port_ws_admin_local\n" +
                "\n" +
                "[port_rpc_admin_local]\n" +
                "port = 6005\n" +
                "ip = 127.0.0.1\n" +
                "admin = 127.0.0.1\n" +
                "protocol = http\n" +
                "\n" +
                "[port_ws_admin_local]\n" +
                "port = 6006\n" +
                "ip = 127.0.0.1\n" +
                "#admin = 127.0.0.2\n" +
                "admin = 127.0.0.3\n" +
                "protocol = ws");

        Assertions.assertEquals("/var/log/rippled/debug.log", cfg.debugLogFile().orElseThrow());
        Assertions.assertEquals("/var/lib/rippled/db", cfg.databasePath().orElseThrow());
        Assertions.assertEquals("validators.txt", cfg.validatorsTxtFile().orElseThrow());
        Assertions.assertEquals("main", cfg.networkId());

        Assertions.assertNotEquals(null, cfg.server());
        var servers = cfg.server().all();
        Assertions.assertEquals(2, servers.size());
        {
            var s = servers.get(0);
            Assertions.assertEquals("port_rpc_admin_local", s.name());
            Assertions.assertEquals("6005", s.port());
            Assertions.assertEquals("127.0.0.1", s.ip());
            Assertions.assertEquals("127.0.0.1", s.admin());
            Assertions.assertEquals("http", s.protocol());
        }
        {
            var s = servers.get(1);
            Assertions.assertEquals("port_ws_admin_local", s.name());
            Assertions.assertEquals("6006", s.port());
            Assertions.assertEquals("127.0.0.1", s.ip());
            Assertions.assertEquals("127.0.0.3", s.admin());
            Assertions.assertEquals("ws", s.protocol());
        }
    }

    @Test
    public void sameAs() {
        var base = ConfigCfg.parse("# random comment\n[debug_logfile]\n/var/log/rippled/debug.log\n");

        Assertions.assertTrue(base.sameAs(ConfigCfg.parse("# random comment\n[debug_logfile]\n/var/log/rippled/debug.log\n")));

        Assertions.assertFalse(base.sameAs(ConfigCfg.parse("# random comment1\n[debug_logfile]\n/var/log/rippled/debug.log\n"))); // comment
        Assertions.assertFalse(base.sameAs(ConfigCfg.parse("# random comment\n[debug_logfile1]\n/var/log/rippled/debug.log\n"))); // group
        Assertions.assertFalse(base.sameAs(ConfigCfg.parse("# random comment\n[debug_logfile]\n/var/log/rippled/debug.log1\n"))); // value
    }
}