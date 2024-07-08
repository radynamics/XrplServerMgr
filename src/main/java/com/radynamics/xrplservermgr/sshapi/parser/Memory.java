package com.radynamics.xrplservermgr.sshapi.parser;

import com.radynamics.xrplservermgr.datasize.Size;
import com.radynamics.xrplservermgr.datasize.SizeUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Memory {
    private final String label;
    private Size total;
    private Size used;
    private Size free;

    public Memory(String label) {
        this.label = label;
    }

    public static List<Memory> parse(String lines, char delimiter, SizeUnit unit) {
        var list = new ArrayList<Memory>();
        var scanner = new Scanner(lines);
        // Ignore header row
        scanner.nextLine();
        while (scanner.hasNextLine()) {
            list.add(parseLine(scanner.nextLine(), delimiter, unit));
        }
        return list;
    }

    private static Memory parseLine(String line, char delimiter, SizeUnit unit) {
        var values = line.split(String.valueOf(delimiter));

        var d = new Memory(values[0].replaceAll(":", "")); // "Mem:"
        d.total(Size.of(Long.valueOf(values[1]), unit));
        d.used(Size.of(Long.valueOf(values[2]), unit));
        d.free(Size.of(Long.valueOf(values[3]), unit));
        return d;
    }

    public String path() {
        return label;
    }

    private void total(Size value) {
        total = value;
    }

    public Size total() {
        return total;
    }

    private void used(Size value) {
        used = value;
    }

    public Size used() {
        return used;
    }

    private void free(Size value) {
        free = value;
    }

    public Size free() {
        return free;
    }

    @Override
    public String toString() {
        return "%s, free: %s%%".formatted(label, free);
    }
}
