package com.radynamics.xrplservermgr.xrpl.subscription;

import com.google.gson.JsonObject;

import java.util.ArrayList;

public class PeerStatusStream implements StreamListener {
    private final ArrayList<PeerStatusStreamListener> listener = new ArrayList<>();

    @Override
    public String channelName() {
        return "peer_status";
    }

    @Override
    public void onReceive(JsonObject data) {
        var parsed = new PeerStatusStreamData();
        parsed.action(data.get("action").getAsString());
        parsed.date(data.get("date").getAsLong());
        parsed.ledgerHash(data.get("ledger_hash").getAsString());
        parsed.ledgerIndex(data.get("ledger_index").getAsLong());
        parsed.ledgerIndexMin(data.get("ledger_index_min").getAsLong());
        parsed.ledgerIndexMax(data.get("ledger_index_max").getAsLong());

        raiseOnReceive(parsed);
    }

    public void addListener(PeerStatusStreamListener l) {
        listener.add(l);
    }

    private void raiseOnReceive(PeerStatusStreamData data) {
        for (var l : listener) {
            l.onReceive(data);
        }
    }
}
