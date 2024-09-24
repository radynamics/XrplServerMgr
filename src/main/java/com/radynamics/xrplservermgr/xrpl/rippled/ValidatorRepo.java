package com.radynamics.xrplservermgr.xrpl.rippled;

import com.radynamics.xrplservermgr.xrpl.KnownValidator;
import com.radynamics.xrplservermgr.xrpl.KnownValidatorRepo;

import java.util.ArrayList;
import java.util.Optional;

public class ValidatorRepo implements KnownValidatorRepo {
    private final ArrayList<KnownValidator> validators = new ArrayList<>();

    public static ValidatorRepo main() {
        var repo = new ValidatorRepo();
        repo.validators.add(KnownValidator.of("nHUpDEZX5Zy9auiu4yhDmhirNu6PyB1LvzQEL9Mxmqjr818w663q", "xspectar.com"));
        repo.validators.add(KnownValidator.of("nHDB2PAPYqF86j9j3c6w1F1ZqwvQfiWcFShZ9Pokg9q4ohNDSkAz", "xrpscan.com"));
        repo.validators.add(KnownValidator.of("nHU3AenyRuJ4Yei4YHkh6frZg8y2RwXznkMAomUE1ptV5Spvqsih", "xrpl.aesthetes.art"));
        repo.validators.add(KnownValidator.of("nHUfxETNHsA9reyYCVYwNztEbifMg6U9YUdcgVvzMwGNpphKSSf6", "xrpkuwait.com"));
        repo.validators.add(KnownValidator.of("nHUwGQrfZfieeLFeGRdGnAmGpHBCZq9wvm5c59wTc2JhJMjoXmd8", "xrpgoat.com"));
        repo.validators.add(KnownValidator.of("nHBWa56Vr7csoFcCnEPzCCKVvnDQw3L28mATgHYQMGtbEfUjuYyB", "xrp.vet"));
        repo.validators.add(KnownValidator.of("nHUfPizyJyhAJZzeq3duRVrZmsTZfcLn7yLF5s2adzHdcHMb9HmQ", "xrp.unic.ac.cy"));
        repo.validators.add(KnownValidator.of("nHUrUNXCy4DgPPNABX9C6mUctpoq7CwgLKAUxjw6zYtTfiqsj1ew", "xrp-validator.interledger.org"));
        repo.validators.add(KnownValidator.of("nHU2k8Po4dgygiQUG8wAADMk9RqkrActeKwsaC9MdtJ9KBvcpVji", "verum.eminence.im"));
        repo.validators.add(KnownValidator.of("nHUdjQgg33FRu88GQDtzLWRw95xKnBurUZcqPpe3qC9XVeBNrHeJ", "validator.xrpl.robertswarthout.com"));
        repo.validators.add(KnownValidator.of("nHUXeusfwk61c4xJPneb9Lgy7Ga6DVaVLEyB29ftUdt9k2KxD6Hw", "validator.xrpl-labs.com"));
        repo.validators.add(KnownValidator.of("nHUpDPFoCNysckDSHiUBEdDXRu2iYLUgYjTzrj3bde5iDRkNtY8f", "validator.poli.usp.br"));
        repo.validators.add(KnownValidator.of("nHUY14bKLLm72ukzo2t6AVnQiu4bCd1jkimwWyJk3txvLeGhvro5", "validator.gatehub.net"));
        repo.validators.add(KnownValidator.of("nHU4bLE3EmSqNwfL4AP1UZeTNPrSPPP6FXLKXo2uqfHuvBQxDVKd", "ripple.com"));
        repo.validators.add(KnownValidator.of("nHBVACxZaNbUjZZkBfj7gRxF3xgG2vbcP4m48KzVwntdTogi5Tfs", "onxrp.com"));
        repo.validators.add(KnownValidator.of("nHUge3GFusbqmfYAJjxfKgm2j4JXGxrRsfYMcEViHrFSzQDdk5Hq", "katczynski.net"));
        repo.validators.add(KnownValidator.of("nHUvcCcmoH1FJMMC6NtF9KKA4LpCWhjsxk2reCQidsp5AHQ7QY9H", "jon-nilsen.no"));
        repo.validators.add(KnownValidator.of("nHUDpRzvY8fSRfQkmJMqjmVSaFmMEVxBNn2tNQy5VAhFJ6is6GFk", "ekiserrepe.es"));
        repo.validators.add(KnownValidator.of("nHUcNC5ni7XjVYfCMe38Rm3KQaq27jw7wJpcUYdo4miWwpNePRTw", "cabbit.tech"));
        repo.validators.add(KnownValidator.of("nHB8QMKGt9VB4Vg71VszjBVQnDW3v3QudM4DwFaJfy96bj4Pv9fA", "bithomp.com"));
        repo.validators.add(KnownValidator.of("nHUtmbn4ALrdU6U8pmd8AMt4qKTdZTbYJ3u1LHyAzXga3Zuopv5Y", "bifrostwallet.com"));
        repo.validators.add(KnownValidator.of("nHUED59jjpQ5QbNhesXMhqii9gA8UfbBmv3i5StgyxG98qjsT4yn", "arrington-xrp-capital.blockdaemon.com"));
        repo.validators.add(KnownValidator.of("nHUr8EhgKeTc9ESNt4nMYzWC2Pu7GgRHMRTsNEyGBTCfnHPxmXcm", "anodos.finance"));
        repo.validators.add(KnownValidator.of("nHUFE9prPXPrHcG3SkwP1UzAQbSphqyQkQK9ATXLZsfkezhhda3p", "alloy.ee"));
        repo.validators.add(KnownValidator.of("nHBidG3pZK11zQD6kpNDoAhDxH6WLGui6ZxSbUx7LSqLHsgzMPec", "bitso.com"));
        repo.validators.add(KnownValidator.of("nHUpJSKQTZdB1TDkbCREMuf8vEqFkk84BcvZDhsQsDufFDQVajam", "data443.com"));
        repo.validators.add(KnownValidator.of("nHUpcmNsxAw47yt2ADDoNoQrzLyTJPgnyq16u6Qx2kRPA17oUNHz", "isrdc.in"));
        repo.validators.add(KnownValidator.of("nHUnhRJK3csknycNK5SXRFi8jvDp3sKoWvS9wKWLq1ATBBGgPBjp", "peerisland.com"));
        repo.validators.add(KnownValidator.of("nHUVPzAmAmQ2QSc4oE1iLfsGi17qN2ado8PhxvgEkou76FLxAz7C", "ripple.ittc.ku.edu"));
        repo.validators.add(KnownValidator.of("nHUFCyRCrUjvtZmKiLeF8ReopzKuUoKeDeXo3wEUBVSaawzcSBpW", "ripple.kenan-flagler.unc.edu"));
        repo.validators.add(KnownValidator.of("nHDH7bQJpVfDhVSqdui3Z8GPvKEBQpo6AKHcnXe21zoD4nABA6xj", "ripplevalidator.uwaterloo.ca"));
        repo.validators.add(KnownValidator.of("nHULqGBkJtWeNFjhTzYeAsHA3qKKS7HoBh8CV3BAGTGMZuepEhWC", "shadow.haas.berkeley.edu"));
        repo.validators.add(KnownValidator.of("nHUq9tJvSyoXQKhRytuWeydpPjvTz3M9GfUpEqfsg9xsewM7KkkK", "students.cs.ucl.ac.uk"));
        repo.validators.add(KnownValidator.of("nHUryiyDqEtyWVtFG24AAhaYjMf9FRLietbGzviF3piJsMm9qyDR", "www.bitrue.com"));
        repo.validators.add(KnownValidator.of("nHUbgDd63HiuP68VRWazKwZRzS61N37K3NbfQaZLhSQ24LGGmjtn", "xrp-col.anu.edu.au"));
        return repo;
    }

    public static ValidatorRepo test() {
        return new ValidatorRepo();
    }

    public Optional<KnownValidator> get(String publicKey) {
        for (var v : validators) {
            if (v.publicKey().equals(publicKey)) {
                return Optional.of(v);
            }
        }
        return Optional.empty();
    }
}
