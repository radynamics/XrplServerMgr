package com.radynamics.xrplservermgr.xrpl.parser.debuglog;

import java.time.ZonedDateTime;

public class LogEvent {
    private final ZonedDateTime dateTime;
    private final String partition;
    private final Severity severity;
    private String message;

    public LogEvent(ZonedDateTime dateTime, String partition, Severity severity, String message) {
        this.dateTime = dateTime;
        this.partition = partition;
        this.severity = severity;
        this.message = message;
    }

    public ZonedDateTime dateTime() {
        return dateTime;
    }

    public String partition() {
        return partition;
    }

    public Severity severity() {
        return severity;
    }

    public String message() {
        return message;
    }

    public void message(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "%s: [%s] %s".formatted(dateTime, severity.textId(), message);
    }
}
