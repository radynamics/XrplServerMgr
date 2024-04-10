package com.radynamics.xrplservermgr.xrpl.rippled;

import com.google.gson.JsonObject;

public class RippledCommandException extends Exception {
    private JsonObject json;

    public RippledCommandException(String errorMessage) {
        super(errorMessage);
    }

    public RippledCommandException(String errorMessage, JsonObject json) {
        this(errorMessage);
        this.json = json;
    }

    public RippledCommandException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public RippledCommandException(Throwable e) {
        super(e);
    }


    public JsonObject json() {
        return json;
    }
}
