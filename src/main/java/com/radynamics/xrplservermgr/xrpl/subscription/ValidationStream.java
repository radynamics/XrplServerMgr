package com.radynamics.xrplservermgr.xrpl.subscription;

import com.google.gson.JsonObject;

import java.util.ArrayList;

public class ValidationStream implements StreamListener {
    private final ArrayList<ValidationStreamListener> listener = new ArrayList<>();

    @Override
    public String channelName() {
        return "validations";
    }

    @Override
    public void onReceive(JsonObject data) {
        var parsed = new ValidationStreamData();
        parsed.ledgerIndex(data.get("ledger_index").getAsString());
        parsed.validationPublicKey(data.get("validation_public_key").getAsString());
        parsed.signingTime(data.get("signing_time").getAsLong());

        raiseOnReceive(parsed);
    }

    public void addListener(ValidationStreamListener l) {
        listener.add(l);
    }

    private void raiseOnReceive(ValidationStreamData data) {
        for (var l : listener) {
            l.onReceive(data);
        }
    }
}
