package com.radynamics.xrplservermgr.sshapi.parser;

public class Top {
    private String virtualMemory;

    public static Top parse(String line) {
        // line eg. " 182620 rippled   20   0   16.3g  14.8g 405888 S   6.2  47.4   1380:52 rippled"
        var o = new Top();
        o.virtualMemory(line.substring(24, 32).trim());
        return o;
    }

    private void virtualMemory(String virtualMemory) {
        this.virtualMemory = virtualMemory;
    }

    public String virtualMemory() {
        return virtualMemory;
    }
}
