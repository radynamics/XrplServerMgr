package com.radynamics.xrplservermgr.xrpl;

import java.util.Optional;

public interface KnownValidatorRepo {
    Optional<KnownValidator> get(String publicKey);
}
