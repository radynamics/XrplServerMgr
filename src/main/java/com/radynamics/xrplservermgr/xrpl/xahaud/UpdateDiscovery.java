package com.radynamics.xrplservermgr.xrpl.xahaud;

import com.radynamics.xrplservermgr.utils.Utils;
import com.radynamics.xrplservermgr.xrpl.XrplApiException;
import com.radynamics.xrplservermgr.xrpl.XrplBinaryPackage;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UpdateDiscovery {
    public List<XrplBinaryPackage> list(UpdateChannel channel) throws XrplApiException {
        String htmlContent;
        try {
            htmlContent = Utils.getContent(new URL("https://build.xahau.tech/"));
        } catch (IOException | XrplApiException e) {
            throw new XrplApiException(e);
        }

        var linkPattern = Pattern.compile("(<a[^>]+>.+?</a>)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        var pageMatcher = linkPattern.matcher(htmlContent);
        var links = new ArrayList<String>();
        while (pageMatcher.find()) {
            links.add(pageMatcher.group());
        }

        var hrefs = new ArrayList<String>();
        for (var a : links) {
            var hrefStart = a.indexOf("\"");
            var hrefEnd = a.indexOf("\"", hrefStart + 1);
            hrefs.add(a.substring(hrefStart + 1, hrefEnd));
        }

        final var ignored = List.of("../", "1000mb.bin");
        var filtered = hrefs.stream().filter(o -> !ignored.contains(o) && !o.endsWith(".releaseinfo")).collect(Collectors.toList());
        filtered = filtered.stream().filter(channel::includes).collect(Collectors.toList());

        var list = new ArrayList<XrplBinaryPackage>();
        for (var href : filtered) {
            list.add(new XrplBinaryPackage(href, URLDecoder.decode(href, StandardCharsets.UTF_8)));
        }

        list.sort(Comparator.comparing(XrplBinaryPackage::versionText));
        Collections.reverse(list);
        return list;
    }
}
