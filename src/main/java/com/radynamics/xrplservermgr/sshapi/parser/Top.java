package com.radynamics.xrplservermgr.sshapi.parser;

import com.radynamics.xrplservermgr.datasize.Size;
import com.radynamics.xrplservermgr.datasize.SizeUnit;

public class Top {
    private Size virtualMemory;
    private Size residentMemory;

    public static Top parse(String line, SizeUnit sizeUnit) {
        // line eg. " 182620 rippled   20   0   16.3g  14.8g 405888 S   6.2  47.4   1380:52 rippled"
        var o = new Top();
        // 16.3
        o.virtualMemory(Size.of(parseDouble(line, 24, 31), sizeUnit));
        // 14.8
        o.residentMemory(Size.of(parseDouble(line, 32, 38), sizeUnit));
        return o;
    }

    private static Double parseDouble(String line, int beginIndex, int endIndex) {
        return Double.parseDouble(line.substring(beginIndex, endIndex).trim().replace(",", "."));
    }

    private void virtualMemory(Size virtualMemory) {
        this.virtualMemory = virtualMemory;
    }

    public Size virtualMemory() {
        return virtualMemory;
    }

    public Size residentMemory() {
        return this.residentMemory;
    }

    private void residentMemory(Size value) {
        this.residentMemory = value;
    }
}
