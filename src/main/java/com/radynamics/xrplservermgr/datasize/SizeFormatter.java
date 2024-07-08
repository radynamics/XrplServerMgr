package com.radynamics.xrplservermgr.datasize;

import java.text.DecimalFormat;

public class SizeFormatter {
    public static String format(Size value) {
        var df = new DecimalFormat("#.##");
        return "%s %s".formatted(df.format(value.value()), value.unit().shortName());
    }
}
