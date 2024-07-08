package com.radynamics.xrplservermgr.datasize;

import java.text.DecimalFormat;

public class SizeFormatter {
    public static String format(Size value) {
        return "%s %s".formatted(formatNumber(value), value.unit().shortName());
    }

    public static String formatNumber(Size value) {
        var df = new DecimalFormat("#.##");
        return df.format(value.value());
    }
}
