package com.radynamics.xrplservermgr.xrpl.subscription;

import java.util.List;

public enum Stream {
    Validations,
    Ledger,
    PeerStatus;

    public static List<Stream> all() {
        return List.of(Stream.Validations, Stream.Ledger, Stream.PeerStatus);
    }
}
