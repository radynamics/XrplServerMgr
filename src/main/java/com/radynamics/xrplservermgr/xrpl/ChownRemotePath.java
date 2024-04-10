package com.radynamics.xrplservermgr.xrpl;

public interface ChownRemotePath {
    Boolean doChown(String remotePath, String newOwner);
}
