package com.radynamics.xrplservermgr.xrpl;

import org.apache.commons.lang3.NotImplementedException;

public enum LedgerId {
    Rippled(0, "rippled"),
    Xahaud(1, "xahaud");

    private final int numericId;
    private final String textId;

    LedgerId(int numericId, String textId) {
        this.numericId = numericId;
        this.textId = textId;
    }

    public static LedgerId of(int id) {
        for (var e : LedgerId.values()) {
            if (e.numericId() == id) {
                return e;
            }
        }
        throw new NotImplementedException(String.format("LedgerId %s unknown.", id));
    }

    public static LedgerId of(String id) {
        for (var e : LedgerId.values()) {
            if (e.textId().equals(id)) {
                return e;
            }
        }
        throw new NotImplementedException(String.format("LedgerId %s unknown.", id));
    }

    public boolean sameAs(LedgerId ledgerId) {
        if (ledgerId == null) return false;
        return numericId() == ledgerId.numericId();
    }

    public int numericId() {
        return numericId;
    }

    public String textId() {
        return textId;
    }
}
