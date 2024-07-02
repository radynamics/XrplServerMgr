package com.radynamics.xrplservermgr.sshapi.parser;

import java.util.*;
import java.util.stream.Collectors;

public class Yum {
    private final String version;

    public Yum(String version) {
        this.version = version;
    }

    public static List<Yum> parseUpdateList(String raw) {
        // Example output for "ayum --showduplicates list rippled | expand"
        /*
Not root, Subscription Management repositories not updated
Last metadata expiration check: 0:08:09 ago on Sun 21 Jan 2024 10:00:23 AM CET.
Installed Packages
rippled.x86_64                    2.0.0-1.el7                     @ripple-stable
Available Packages
rippled.x86_64                    1.2.0-1.el7                     ripple-stable
rippled.x86_64                    1.3.0-1.el7                     ripple-stable
rippled.x86_64                    1.3.1-1.el7                     ripple-stable
        */

        var list = new ArrayList<Yum>();
        var scanner = new Scanner(raw);
        var availablePackagesSeen = false;
        while (scanner.hasNextLine()) {
            var line = scanner.nextLine();

            // Ignore until "Available Packages"
            if (line.trim().equals("Available Packages")) {
                availablePackagesSeen = true;
                continue;
            }
            if (!availablePackagesSeen) {
                continue;
            }

            // Eg. "rippled.x86_64                    2.0.0-1.el7                     @ripple-stable"
            var values = Arrays.stream(line.split(" ")).filter(o -> !o.isEmpty()).collect(Collectors.toList());
            // Eg. "2.0.0-1.el7"
            list.add(new Yum(values.get(1)));
        }

        // yum returned from oldest to newest, return newest to oldest.
        Collections.reverse(list);
        return list;
    }

    public String version() {
        return version;
    }
}
