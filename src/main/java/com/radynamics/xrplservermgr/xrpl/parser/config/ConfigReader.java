package com.radynamics.xrplservermgr.xrpl.parser.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ConfigReader {
    private final ArrayList<Section> sections = new ArrayList<>();
    private final ArrayList<String> values = new ArrayList<>();

    public void read(String raw) {
        var scanner = new Scanner(raw);
        Section section = null;
        var lineNumber = -1;
        while (scanner.hasNextLine()) {
            var line = scanner.nextLine();
            lineNumber++;
            var trimmed = line.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            if (isComment(trimmed)) {
                continue;
            }

            if (isSection(trimmed)) {
                section = new Section(new Line(lineNumber, trimmed));
                sections.add(section);
                continue;
            }

            if (section == null) {
                values.add(trimmed);
            } else {
                section.add(new Line(lineNumber, trimmed));
            }
        }
    }

    private boolean isSection(String line) {
        return line.startsWith("[") && line.endsWith("]");
    }

    private boolean isComment(String line) {
        return line.startsWith("#");
    }

    public List<Section> sections(String name) {
        return sections.stream().filter(o -> o.name().equals(name)).collect(Collectors.toList());
    }
}
