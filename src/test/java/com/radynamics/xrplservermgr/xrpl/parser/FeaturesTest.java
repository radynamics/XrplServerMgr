package com.radynamics.xrplservermgr.xrpl.parser;

import com.vdurmont.semver4j.Semver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class FeaturesTest {
    @Test
    public void createComparatorVersionName() {
        var list = new ArrayList<Feature>() {
            {
                add(createFeature("aaa", false, false, new Semver("1.0.0")));
                add(createFeature("ccc", false, false, new Semver("1.0.3")));
                add(createFeature("bbb", false, false, new Semver("1.0.2")));
            }
        };
        list.sort(Features.createComparator());

        Assertions.assertEquals("ccc", list.get(0).hash());
        Assertions.assertEquals("bbb", list.get(1).hash());
        Assertions.assertEquals("aaa", list.get(2).hash());
    }

    @Test
    public void createComparatorVersionUnknown() {
        var list = new ArrayList<Feature>() {
            {
                add(createFeature("aaa", false, false, new Semver("1.0.0")));
                add(createFeature("ccc", false, false, new Semver("1.0.3")));
                add(createFeature("bbb", false, false, null));
            }
        };
        list.sort(Features.createComparator());

        Assertions.assertEquals("bbb", list.get(0).hash());
        Assertions.assertEquals("ccc", list.get(1).hash());
        Assertions.assertEquals("aaa", list.get(2).hash());
    }

    @Test
    public void createComparatorVotingActive() {
        var list = new ArrayList<Feature>() {
            {
                add(createFeature("aaa", true, false, new Semver("1.0.0")));
                add(createFeature("bbb1", true, false, new Semver("1.0.2")));
                add(createFeature("ccc", false, false, new Semver("1.0.3")));
                add(createFeature("bbb", false, false, null));
            }
        };
        list.sort(Features.createComparator());

        Assertions.assertEquals("bbb", list.get(0).hash());
        Assertions.assertEquals("ccc", list.get(1).hash());
        Assertions.assertEquals("bbb1", list.get(2).hash());
        Assertions.assertEquals("aaa", list.get(3).hash());
    }

    private static Feature createFeature(String key, boolean enabled, boolean obsolete, Semver versionIntroduced) {
        var f = Feature.of(key, "feature " + key, enabled, true);
        f.obsolete(obsolete);
        f.versionIntroduced(versionIntroduced);
        return f;
    }
}
