package com.radynamics.xrplservermgr.xrpl.rippled;

public enum Vote {
    accept("accept"),

    reject("reject");

    private final String value;

    private Vote(String value) {
        this.value = value;
    }

    public String asString() {
        return value;
    }
}
