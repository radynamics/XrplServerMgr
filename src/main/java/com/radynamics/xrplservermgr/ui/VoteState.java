package com.radynamics.xrplservermgr.ui;

import com.radynamics.xrplservermgr.xrpl.parser.Feature;
import com.radynamics.xrplservermgr.xrpl.rippled.Vote;

public class VoteState {
    private final Feature feature;
    private final Vote oldVetoed;
    private Vote currentVetoed;

    public VoteState(Feature feature) {
        this.feature = feature;
        if (feature.vetoed() == null) {
            this.oldVetoed = null;
        } else {
            this.oldVetoed = feature.vetoed() ? Vote.reject : Vote.accept;
        }
        this.currentVetoed = oldVetoed;
    }

    public Feature feature() {
        return feature;
    }

    public Vote old() {
        return oldVetoed;
    }

    public Vote current() {
        return currentVetoed;
    }

    public void current(Vote value) {
        this.currentVetoed = value;
    }

    public boolean changed() {
        if (oldVetoed == null && currentVetoed != null) return true;
        if (oldVetoed != null && currentVetoed == null) return true;
        if (oldVetoed == null && currentVetoed == null) return false;
        return !oldVetoed.asString().equals(currentVetoed.asString());
    }

    @Override
    public String toString() {
        return "%s: current: %s".formatted(feature.name(), currentVetoed);
    }
}
