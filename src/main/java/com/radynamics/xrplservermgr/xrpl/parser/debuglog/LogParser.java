package com.radynamics.xrplservermgr.xrpl.parser.debuglog;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class LogParser {
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd HH:mm:ss.SSSSSSSSS").withLocale(Locale.US);

    public static List<LogEvent> parse(String content) {
        var list = new ArrayList<LogEvent>();

        var inJson = false;
        var sbMultilineCache = new StringBuilder();
        LogEvent last = null;

        var i = -1;
        var lines = content.split("\n");
        for (var l : lines) {
            i++;
            try {
                var dateTime = parseDateTime(l);
                // "Each line represents one log entry," is WRONG (https://xrpl.org/understanding-log-messages.html). JSONs are multiline formatted.
                if (dateTime.isEmpty()) {
                    inJson = true;
                    sbMultilineCache.append(l);
                    sbMultilineCache.append("\n");
                    continue;
                }

                if (inJson) {
                    inJson = false;
                    // last can be null if we start reading logs in the middle of a multiline message (streaming).
                    if (last != null) {
                        last.message(last.message() + " " + sbMultilineCache);
                    }
                    sbMultilineCache = new StringBuilder();
                }

                // "2020-Jul-08 20:10:17.372178946 UTC Peer:WRN [236] onReadMessage from n9J2CP7hZypxDJ27ZSxoy4VjbaSgsCNaRRJtJkNJM5KMdGaLdRy7 at 197.27.127.136:53046: stream truncated"
                int endPartitionIndex = l.indexOf(":", 35);
                var partition = l.substring(35, endPartitionIndex);
                var endSeverity = endPartitionIndex + 4;
                var severity = Severity.of(l.substring(endPartitionIndex + 1, endSeverity));
                var message = l.substring(endSeverity + 1);
                var entry = new LogEvent(dateTime.get(), partition, severity, message);
                list.add(entry);
                last = entry;
            } catch (Exception e) {
                throw new RuntimeException("Parsing error at line %s".formatted(i), e);
            }
        }
        return list;
    }

    private static Optional<ZonedDateTime> parseDateTime(String l) {
        try {
            return Optional.of(LocalDateTime.parse(l.substring(0, 30), dateFormatter).atZone(ZoneId.of("UTC")));
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }
}
