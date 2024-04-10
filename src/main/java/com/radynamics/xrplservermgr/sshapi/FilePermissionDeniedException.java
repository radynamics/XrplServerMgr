package com.radynamics.xrplservermgr.sshapi;

public class FilePermissionDeniedException extends SshApiException {
    public FilePermissionDeniedException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
