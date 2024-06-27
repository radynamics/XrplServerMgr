package com.radynamics.xrplservermgr.db;

public class DbException extends Exception {
    public DbException(String errorMessage) {
        super(errorMessage);
    }
}
