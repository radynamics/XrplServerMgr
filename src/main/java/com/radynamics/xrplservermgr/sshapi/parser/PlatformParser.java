package com.radynamics.xrplservermgr.sshapi.parser;

public class PlatformParser {
    public static Platform parse(String line) {
        // line eg. on RedHat "ID_LIKE="fedora"", or on Ubuntu "ID_LIKE=debian\n"
        var idLike = line.substring(line.indexOf("=") + 1).replaceAll("\n", "").replaceAll("\"", "");
        switch (idLike) {
            case "debian":
                return Platform.Debian;
            case "fedora":
                return Platform.Fedora;
            default:
                return Platform.Unknown;
        }
    }
}
