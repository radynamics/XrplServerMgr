package com.radynamics.xrplservermgr.sshapi.parser;

import com.radynamics.xrplservermgr.datasize.Size;
import com.radynamics.xrplservermgr.datasize.SizeUnit;

public class Top {
    private Size virtualMemory;

    public static Top parse(String line, SizeUnit sizeUnit) {
        // line eg. " 182620 rippled   20   0   16.3g  14.8g 405888 S   6.2  47.4   1380:52 rippled"
        var o = new Top();
        // 16.3
        var value = Double.parseDouble(line.substring(24, 31).trim().replace(",", "."));
        o.virtualMemory(Size.of(value, sizeUnit));
        return o;
    }

    private void virtualMemory(Size virtualMemory) {
        this.virtualMemory = virtualMemory;
    }

    public Size virtualMemory() {
        return virtualMemory;
    }
}
