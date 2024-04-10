package com.radynamics.xrplservermgr.sshapi;

public class SshApiException extends Exception {
    public SshApiException(String errorMessage) {
        super(errorMessage);
    }

    public SshApiException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public SshApiException(Throwable e) {
        super(e);
    }
}
