package com.radynamics.xrplservermgr.sshapi.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AptCache {
    private final String version;

    public AptCache(String version) {
        this.version = version;
    }

    public static List<AptCache> parse(String raw) {
        // Example output for "apt-cache policy rippled"
        /*
rippled:
  Installed: 2.0.0-1
  Candidate: 2.0.0-1
  Version table:
  *** 2.0.0-1 500
         500 https://repos.ripple.com/repos/rippled-deb jammy/stable amd64 Packages
         100 /var/lib/dpkg/status
      1.12.0-1 500
         500 https://repos.ripple.com/repos/rippled-deb jammy/stable amd64 Packages
      1.11.0-1 500
         500 https://repos.ripple.com/repos/rippled-deb jammy/stable amd64 Packages
        */

        var list = new ArrayList<AptCache>();
        var scanner = new Scanner(raw);
        var versionTableSeen = false;
        while (scanner.hasNextLine()) {
            var line = scanner.nextLine();
            // Ignore until "Version table:"
            versionTableSeen = versionTableSeen || line.trim().equals("Version table:");
            if (!versionTableSeen) {
                continue;
            }

            final String PRIORITY_ROW = "        ";
            if (line.startsWith(PRIORITY_ROW)) {
                continue;
            }

            final String SUFFIX_INSTALLABLE = "500";
            if (line.endsWith(SUFFIX_INSTALLABLE)) {
                // Eg. "  *** 2.0.0-1 500"
                // Eg. "      1.11.0-1 500"
                var indexPriority = line.lastIndexOf(" ");
                var indexVersion = line.substring(0, indexPriority).lastIndexOf(" ");
                list.add(new AptCache(line.substring(indexVersion + 1, indexPriority)));
            }
        }
        return list;
    }

    public String version() {
        return version;
    }
}
