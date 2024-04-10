package com.radynamics.xrplservermgr.xrpl.parser.config;

public class Line {
    private final int number;
    private final String value;

    public Line(int number, String value) {
        this.number = number;
        this.value = value;
    }

    public int number() {
        return number;
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return "line: %s: %s".formatted(number, value);
    }
}
