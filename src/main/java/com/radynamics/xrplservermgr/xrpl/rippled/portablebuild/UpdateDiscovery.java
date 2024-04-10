package com.radynamics.xrplservermgr.xrpl.rippled.portablebuild;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.radynamics.xrplservermgr.utils.Utils;
import com.radynamics.xrplservermgr.xrpl.XrplApiException;
import com.radynamics.xrplservermgr.xrpl.XrplBinaryPackage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UpdateDiscovery {
    public List<XrplBinaryPackage> list() throws XrplApiException {
        JsonArray entries;
        try {
            entries = JsonParser.parseString(Utils.getContent(new URL("https://api.github.com/repos/XRPLF/rippled-portable-builds/contents/releases"))).getAsJsonArray();
        } catch (IOException | XrplApiException e) {
            throw new XrplApiException(e);
        }

        var list = new ArrayList<XrplBinaryPackage>();
        for (var entry : entries) {
            var e = entry.getAsJsonObject();
            var name = e.get("name").getAsString();
            var indexEnding = name.lastIndexOf(".");
            if (indexEnding > -1) {
                var ending = name.substring(indexEnding);
                final var ignoredEndings = List.of(".sig", ".sha512sum");
                if (ignoredEndings.contains(ending)) {
                    continue;
                }
            }
            var indexVersionDelimiter = name.lastIndexOf("-");
            var version = name.substring(indexVersionDelimiter + 1);
            list.add(new XrplBinaryPackage(version, version));
        }

        list.sort(Comparator.comparing(XrplBinaryPackage::versionText));
        Collections.reverse(list);
        return list;
    }
}
