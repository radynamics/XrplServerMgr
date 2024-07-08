package com.radynamics.xrplservermgr.datasize;

public class SizeConverter {
    public static final Size toGb(Size value) {
        if (value.unit() == SizeUnit.GIBIBYTES) return Size.of(value.value() * 1.074, SizeUnit.GIGABYTES);

        throw new IllegalStateException("Conversion %s -> %s is not available: ".formatted(value.unit().shortName(), "GB"));
    }
}
