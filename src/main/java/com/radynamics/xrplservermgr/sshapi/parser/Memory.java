package com.radynamics.xrplservermgr.sshapi.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Memory {
    private final String label;
    private long total;
    private long used;
    private long free;

    public Memory(String label) {
        this.label = label;
    }

    public static List<Memory> parse(String lines, char delimiter) {
        var list = new ArrayList<Memory>();
        var scanner = new Scanner(lines);
        // Ignore header row
        scanner.nextLine();
        while (scanner.hasNextLine()) {
            list.add(parseLine(scanner.nextLine(), delimiter));
        }
        return list;
    }

    private static Memory parseLine(String line, char delimiter) {
        var values = line.split(String.valueOf(delimiter));

        var d = new Memory(values[0].replaceAll(":", "")); // "Mem:"
        d.total(Long.valueOf(values[1]));
        d.used(Long.valueOf(values[2]));
        d.free(Long.valueOf(values[3]));
        return d;
    }

    public String path() {
        return label;
    }

    private void total(Long value) {
        total = value;
    }

    public Long total() {
        return total;
    }

    private void used(Long value) {
        used = value;
    }

    public Long used() {
        return used;
    }

    private void free(Long value) {
        free = value;
    }

    public Long free() {
        return free;
    }

    @Override
    public String toString() {
        return "%s, free: %s%%".formatted(label, free);
    }
}
