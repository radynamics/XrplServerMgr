package com.radynamics.xrplservermgr.xrpl.subscription;

import com.google.gson.JsonObject;

import java.util.ArrayList;

public class LedgerStream implements StreamListener {
    private final ArrayList<LedgerStreamListener> listener = new ArrayList<>();

    @Override
    public String channelName() {
        return "ledger";
    }

    @Override
    public void onReceive(JsonObject data) {
        var parsed = new LedgerStreamData();
        parsed.ledgerIndex(data.get("ledger_index").getAsLong());
        parsed.ledgerTime(data.get("ledger_time").getAsLong());
        parsed.ledgerHash(data.get("ledger_hash").getAsString());
        parsed.txnCount(data.get("txn_count").getAsInt());

        raiseOnReceive(parsed);
    }

    public void addListener(LedgerStreamListener l) {
        listener.add(l);
    }

    private void raiseOnReceive(LedgerStreamData data) {
        for (var l : listener) {
            l.onReceive(data);
        }
    }
}
