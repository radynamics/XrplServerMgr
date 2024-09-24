package com.radynamics.xrplservermgr.xrpl.xahaud;

import com.radynamics.xrplservermgr.xrpl.KnownValidator;
import com.radynamics.xrplservermgr.xrpl.KnownValidatorRepo;

import java.util.ArrayList;
import java.util.Optional;

public class ValidatorRepo implements KnownValidatorRepo {
    private final ArrayList<KnownValidator> validators = new ArrayList<>();

    public static ValidatorRepo main() {
        var repo = new ValidatorRepo();
        repo.validators.add(KnownValidator.of("nHUCQgftpGqfDAY7XMCXPbyBBvGRg9T6n1YMnmaG2trnrYeywAjL", "cbotlabs.xyz"));
        repo.validators.add(KnownValidator.of("nHBtrGRbE8pBk1g892ts7TbZQBQkPx3LLkJqBEzhm15HxDA2aXnN", "digitalgoverning.eu"));
        repo.validators.add(KnownValidator.of("nHBcpUteNtfXeEquLuXVSAAPKxPigmkLAvmpnPjiiPQ4dWZvkoxi", "digitalgoverning.eu"));
        repo.validators.add(KnownValidator.of("nHUetpkzTgf8c6A1DTGemRrhL4nMjsiJrtcsmmg75ufzV5EDMbWU", "evernode.org"));
        repo.validators.add(KnownValidator.of("nHUbAZx7esZvJ4h9gRBPaiuyJUwEMgpxc4TwwZmTvEKTWeiXoh7P", "evernode.org"));
        repo.validators.add(KnownValidator.of("nHBTtSHyh8wweHqkWcVw16QYMA97n4voLFwfs7t7oSaTZmfPcmCz", "offledger.net"));
        repo.validators.add(KnownValidator.of("nHUsPqy1TB4PMFFsDfLz5LdyYAGdGpuifWR3HuPd3wKpYpSHfc14", "uknoderunner.com"));
        repo.validators.add(KnownValidator.of("nHBVzNWFu2yT373tTto5HT1Tybdq2GE8hsdhHdowUDdj9wBieNFo", "validator.onxahau.com"));
        repo.validators.add(KnownValidator.of("nHD9nhqSzzUFEmu8DuRpKrgF1rQzBkjVpMmYhtjmddhGoDLAF63i", "www.bitrue.com"));
        repo.validators.add(KnownValidator.of("nHB2tHvDXE2GM3Cp9ivyAXU3NDLkf8mzYREQkcZ7wFJyBiaVLu24", "xahau.alloy.ee"));
        repo.validators.add(KnownValidator.of("nHUmhzbkWzsjn7BwmYbtynDy4zbyDAgcQtXoRN8tMVGsNxzQJeuZ", "xahau.alloy.ee"));
        repo.validators.add(KnownValidator.of("nHB45nBNgjKMssrRqaNVr2tpCq3t55J5APRRDD6ov1U41JfVFjr6", "xahau.xrpl-labs.com"));
        repo.validators.add(KnownValidator.of("nHUhc1jmBqJ2n7hpUHwHxoWTx5U6p5ihrTXCRJHZbyh6imsoJC52", "xahau.xrpl-labs.com"));
        repo.validators.add(KnownValidator.of("nHUBSX2fFkvAXxw8eVGrd2xwqx9p8sKXYvso2FdAfrHiYWwNj7PK", "xahau1.geveo.com"));
        repo.validators.add(KnownValidator.of("nHUzXFEQwBkqHWp71E23fY9QKvEGQqVncJfm8fPxn4bRBYQ3Eee3", "xahaudevtable.com"));
        repo.validators.add(KnownValidator.of("nHB6YCfTKQJRTB8kYDmDfJEMHaSq3NVWqFc221bLSAz2daVKbH1S", "xrplwin.com"));
        repo.validators.add(KnownValidator.of("nHB1BVMv96GUa272VehmoCeGM61uLRHZVbxAG1i9kQ9Xzd1KVrh1", "gatehub.net"));
        repo.validators.add(KnownValidator.of("nHBzXTffnWr4JXY88bSt9pENiySJQeoA7MXR68bUJYa5uKh1Q5Qf", "gatehub.net"));
        repo.validators.add(KnownValidator.of("nHBgVQ9ugD4BqQJQVhfd223qzks4cJBtc6aTE2AE3e6a1azFCMd1", "anodos.finance"));
        repo.validators.add(KnownValidator.of("nHUGSv93gCN8CMAesCD6h6MMGJWVnizptV92Xobv76NiPPjjNTau", "jbekker.dev"));
        repo.validators.add(KnownValidator.of("nHBdZgzguPahPTpqDaQzvsLyisjZnfLLiyeWwQnMkozyjA5pmf5r", "solonation.io"));
        repo.validators.add(KnownValidator.of("nHDHYR9AXVT3CWQaq7KCQaWvSXWFFFWVfMSVcc6HkfvaJxDNEwaA", "transia.co"));
        repo.validators.add(KnownValidator.of("nHUuPpBvymQcRu49WVRxD1Bq2F89UgrN6Mxa43tjgMrJNFEyZr7g", "validator.xrsaint.vip"));
        repo.validators.add(KnownValidator.of("nHUK8LCS3c3jggJWg117Wwnmc2XbNLLgJwVQojMKrA3L2xhAzokb", "xah.xrpl-commons.org"));
        repo.validators.add(KnownValidator.of("nHBugomPVkgX3zhHqEps8KaByGV7mq4yGKqR2pQzdFVqrkDDyFHP", "xahau.es"));
        repo.validators.add(KnownValidator.of("nHBdRDPaoqxGCMT8WisB3sfy2sY3vvhN9QWvnW2wmT26b9mGCZNK", "xahau.nz"));
        repo.validators.add(KnownValidator.of("nHB14s9UJCdXMcgDSLHikjZ2L8bZWodZVer6uxhRzwrvW2uj62Wj", "xrp.moneymindedapes.com"));
        repo.validators.add(KnownValidator.of("nHUWa7fPeMZtAFQnUc6abfD772B7rtFG4pX4mcfBDyCczZqvsQ8a", "tequ.dev"));

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
