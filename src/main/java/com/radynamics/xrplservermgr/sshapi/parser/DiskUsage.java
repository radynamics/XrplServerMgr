package com.radynamics.xrplservermgr.sshapi.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DiskUsage {
    private final String path;
    private long size;
    private long used;
    private long available;
    private int usedPercent;
    private String mountedOn;

    public DiskUsage(String path) {
        this.path = path;
    }

    public static List<DiskUsage> parse(String lines, char delimiter) {
        var list = new ArrayList<DiskUsage>();
        var scanner = new Scanner(lines);
        // Ignore header row
        scanner.nextLine();
        while (scanner.hasNextLine()) {
            list.add(parseLine(scanner.nextLine(), delimiter));
        }
        return list;
    }

    private static DiskUsage parseLine(String line, char delimiter) {
        var values = line.split(String.valueOf(delimiter));

        var d = new DiskUsage(values[0]);
        d.size(Long.valueOf(values[1]));
        d.used(Long.valueOf(values[2]));
        d.available(Long.valueOf(values[3]));
        // "55%" -> "55"
        d.usedPercent(Integer.valueOf(values[4].substring(0, values[4].length() - 1)));
        d.mountedOn(values[5]);
        return d;
    }

    public String path() {
        return path;
    }

    private void size(Long value) {
        size = value;
    }

    public Long size() {
        return size;
    }

    private void used(Long value) {
        used = value;
    }

    public Long used() {
        return used;
    }

    private void available(Long value) {
        available = value;
    }

    public Long available() {
        return available;
    }

    private void usedPercent(Integer value) {
        usedPercent = value;
    }

    public Integer usedPercent() {
        return usedPercent;
    }

    private void mountedOn(String value) {
        mountedOn = value;
    }

    public String mountedOn() {
        return mountedOn;
    }

    @Override
    public String toString() {
        return "%s, used: %s%%".formatted(path, usedPercent);
    }
}
