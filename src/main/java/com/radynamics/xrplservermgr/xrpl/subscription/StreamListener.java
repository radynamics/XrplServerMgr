package com.radynamics.xrplservermgr.xrpl.subscription;

import com.google.gson.JsonObject;

public interface StreamListener {
    String channelName();

    void onReceive(JsonObject data);
}
