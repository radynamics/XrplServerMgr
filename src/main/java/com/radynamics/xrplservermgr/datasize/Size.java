package com.radynamics.xrplservermgr.datasize;

public class Size {
    private final Double value;
    private final SizeUnit unit;

    private Size(Double value, SizeUnit unit) {
        this.value = value;
        this.unit = unit;
    }

    public static Size of(Long value, SizeUnit unit) {
        return of(value.doubleValue(), unit);
    }

    public static Size of(Double value, SizeUnit unit) {
        return new Size(value, unit);
    }

    public Double value() {
        return value;
    }

    public SizeUnit unit() {
        return unit;
    }

    @Override
    public String toString() {
        return "%s, %s".formatted(value, unit.shortName());
    }
}
