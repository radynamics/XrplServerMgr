package com.radynamics.xrplservermgr.datasize;

public class SizeConverter {
    public static Size toGb(Size value) {
        var result = convert(value);
        if (result.unit() == SizeUnit.KILOBYTES) result = Size.of(result.value() / 1000, SizeUnit.MEGABYTES);
        if (result.unit() == SizeUnit.MEGABYTES) result = Size.of(result.value() / 1000, SizeUnit.GIGABYTES);
        return result;
    }

    private static Size convert(Size value) {
        var converted = value;
        if (value.unit() == SizeUnit.KIBIBYTES) converted = Size.of(value.value() * 1.024, SizeUnit.KILOBYTES);
        if (value.unit() == SizeUnit.MEBIBYTES) converted = Size.of(value.value() * 1.048576, SizeUnit.MEGABYTES);
        if (value.unit() == SizeUnit.GIBIBYTES) converted = Size.of(value.value() * 1.073741824, SizeUnit.GIGABYTES);
        return converted;
    }
}
