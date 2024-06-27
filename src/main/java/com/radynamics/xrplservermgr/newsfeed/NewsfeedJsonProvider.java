package com.radynamics.xrplservermgr.newsfeed;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.radynamics.xrplservermgr.utils.Utils;
import com.radynamics.xrplservermgr.xrpl.XrplApiException;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NewsfeedJsonProvider {

    public List<NewsfeedEntry> list() throws NewsfeedException {
        JsonArray entries;
        try {
            entries = JsonParser.parseString(Utils.getContent(new URL("https://www.radynamics.com/xrplservermgr/newsfeed.json"))).getAsJsonArray();
        } catch (IOException | XrplApiException e) {
            throw new NewsfeedException(e);
        }

        var list = new ArrayList<NewsfeedEntry>();
        for (var entry : entries) {
            var e = entry.getAsJsonObject();

            var title = e.get("title").getAsString();
            var dt = Instant.parse(e.get("dt").getAsString());
            var desc = e.get("desc").getAsString();
            var link = URI.create(e.get("link").getAsString());
            list.add(NewsfeedEntry.create(dt, title, desc, link));
        }

        list.sort(Comparator.comparing(NewsfeedEntry::dateTime));
        Collections.reverse(list);
        return list;
    }
}
