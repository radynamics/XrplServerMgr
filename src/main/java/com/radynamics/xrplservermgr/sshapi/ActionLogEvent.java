package com.radynamics.xrplservermgr.sshapi;

public class ActionLogEvent {
    private final ActionLogLevel level;
    private final String message;

    private ActionLogEvent(ActionLogLevel level, String message) {
        this.level = level;
        this.message = message;
    }

    public static ActionLogEvent info(String message) {
        return new ActionLogEvent(ActionLogLevel.Info, message);
    }

    public static ActionLogEvent warn(String message) {
        return new ActionLogEvent(ActionLogLevel.Warning, message);
    }

    public static ActionLogEvent error(String message) {
        return new ActionLogEvent(ActionLogLevel.Error, message);
    }

    public String message() {
        return message;
    }

    public ActionLogLevel level() {
        return level;
    }

    @Override
    public String toString() {
        return "%s: %s".formatted(level, message);
    }
}
