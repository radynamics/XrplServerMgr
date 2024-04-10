package com.radynamics.xrplservermgr.newsfeed;

public class NewsfeedException extends Exception {
    public NewsfeedException(String errorMessage) {
        super(errorMessage);
    }

    public NewsfeedException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public NewsfeedException(Throwable e) {
        super(e);
    }
}
