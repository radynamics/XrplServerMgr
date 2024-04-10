package com.radynamics.xrplservermgr.xrpl.parser.config;

import java.util.ArrayList;
import java.util.List;

public class Section {
    private final Line line;
    private final String name;
    private final ArrayList<Line> lines = new ArrayList<>();

    public Section(Line line) {
        this.line = line;
        this.name = line.value().substring(1, line.value().length() - 1);
    }

    public void add(Line line) {
        this.lines.add(line);
    }

    public String name() {
        return name;
    }

    public Line line() {
        return line;
    }

    public List<Line> lines() {
        return lines;
    }

    public void remove(Line line) {
        lines.remove(line);
    }

    @Override
    public String toString() {
        return "%s: %s values".formatted(name, lines.size());
    }
}
