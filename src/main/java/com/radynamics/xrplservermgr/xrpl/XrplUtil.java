package com.radynamics.xrplservermgr.xrpl;

import com.radynamics.xrplservermgr.xrpl.rippled.RippledCommandException;

public final class XrplUtil {
    public static boolean isServiceNotRunningError(RippledCommandException e) {
        if (e.json() == null) {
            return false;
        }
        try {
            final var INTERNAL_ERROR = 73;
            return e.json().get("error_code").getAsInt() == INTERNAL_ERROR;
        } catch (Exception ignored) {
            return false;
        }
    }
}
