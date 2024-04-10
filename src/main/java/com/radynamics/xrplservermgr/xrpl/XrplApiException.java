package com.radynamics.xrplservermgr.xrpl;

public class XrplApiException extends Exception {
    public XrplApiException(String errorMessage) {
        super(errorMessage);
    }

    public XrplApiException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public XrplApiException(Throwable e) {
        super(e);
    }
}
