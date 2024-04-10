package com.radynamics.xrplservermgr.xrpl.parser.debuglog;

import org.apache.commons.lang3.NotImplementedException;

public enum Severity {
    Fatal("FTL"),
    Error("ERR"),
    Warning("WRN"),
    Info("NFO"),
    Debug("DBG");

    private final String textId;

    Severity(String textId) {
        this.textId = textId;
    }

    public static Severity of(String textId) {
        for (var e : Severity.values()) {
            if (e.textId.equals(textId)) {
                return e;
            }
        }
        throw new NotImplementedException(String.format("Severity %s unknown.", textId));
    }

    public String textId() {
        return textId;
    }
}
