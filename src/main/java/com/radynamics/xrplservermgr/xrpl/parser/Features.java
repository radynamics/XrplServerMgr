package com.radynamics.xrplservermgr.xrpl.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.radynamics.xrplservermgr.sshapi.Amendment;
import org.apache.commons.lang3.builder.CompareToBuilder;

import java.util.*;
import java.util.stream.Collectors;

public class Features {
    private final ArrayList<Feature> features = new ArrayList<>();

    public static Features parse(JsonObject json, List<Amendment> amendments) {
        var o = new Features();

        var jsonFeatures = json.getAsJsonObject("features");
        for (var e : jsonFeatures.entrySet()) {
            o.features.add(toFeature(e, amendments));
        }
        return o;
    }

    private static Feature toFeature(Map.Entry<String, JsonElement> e, List<Amendment> amendments) {
        var o = e.getValue().getAsJsonObject();
        // Unknown amendments don't have name.
        var name = o.has("name") ? o.get("name").getAsString() : "";
        var f = Feature.of(e.getKey(), name, o.get("enabled").getAsBoolean(), o.get("supported").getAsBoolean());
        if (o.has("vetoed")) {
            if (o.get("vetoed").getAsString().equals("Obsolete")) {
                f.obsolete(true);
            } else {
                f.vetoed(o.get("vetoed").getAsBoolean());
            }
        }
        var a = amendments.stream().filter(x -> x.hash().equals(f.hash())).findFirst();
        f.versionIntroduced(a.map(Amendment::introduced).orElse(null));
        return f;
    }

    public static Comparator<? super Feature> createComparator() {
        return (Comparator<Feature>) (o1, o2) -> {
            // Unknown amendments are newer and should be shown first
            if (o1.versionIntroduced() == null && o2.versionIntroduced() != null) return -1;
            if (o1.versionIntroduced() != null && o2.versionIntroduced() == null) return 1;
            return new CompareToBuilder()
                    // Newer first
                    .append(o2.versionIntroduced(), o1.versionIntroduced())
                    // Up for voting first
                    .append(o2.votingActive(), o1.votingActive())
                    .append(o1.name(), o2.name())
                    .build();
        };
    }

    public List<Feature> all() {
        return Collections.unmodifiableList(features);
    }

    public List<Feature> votedInFavor() {
        return all().stream().filter(o -> o.vetoed() != null && !o.obsolete() && !o.vetoed()).collect(Collectors.toList());
    }
}
