package com.radynamics.xrplservermgr.newsfeed;

import java.net.URI;
import java.time.Instant;

public class NewsfeedEntry {
    private Instant dateTime;
    private String title;
    private String text;
    private URI more;

    public static NewsfeedEntry create(Instant dateTime, String title, String text, URI more) {
        var o = new NewsfeedEntry();
        o.dateTime = dateTime;
        o.title = title;
        o.text = text;
        o.more = more;
        return o;
    }

    public String title() {
        return title;
    }

    public Instant dateTime() {
        return dateTime;
    }

    public String text() {
        return text;
    }

    public URI more() {
        return more;
    }

    @Override
    public String toString() {
        return "title: " + title;
    }
}
