package com.radynamics.xrplservermgr.xrpl.parser;

import com.google.gson.JsonObject;

public class ServerInfo {
    private boolean amendmentBlocked;
    private String buildVersion;
    private String hostId;
    private String nodeSize;
    private String pubKeyNode;
    private String pubKeyValidator;
    private String serverState;

    public static ServerInfo parse(JsonObject json) {
        var o = new ServerInfo();
        var info = json.get("info").getAsJsonObject();
        o.amendmentBlocked(info.has("amendment_blocked") && info.get("amendment_blocked").getAsBoolean());
        o.buildVersion(info.get("build_version").getAsString());
        o.hostId(info.get("hostid").getAsString());
        o.nodeSize(info.get("node_size").getAsString());
        o.pubKeyNode(info.get("pubkey_node").getAsString());
        o.pubKeyValidator(info.get("pubkey_validator").getAsString());
        o.serverState(info.get("server_state").getAsString());
        return o;
    }

    public boolean amendmentBlocked() {
        return amendmentBlocked;
    }

    private void amendmentBlocked(boolean amendmentBlocked) {
        this.amendmentBlocked = amendmentBlocked;
    }

    public String buildVersion() {
        return buildVersion;
    }

    private void buildVersion(String buildVersion) {
        this.buildVersion = buildVersion;
    }

    public String hostId() {
        return hostId;
    }

    private void hostId(String hostId) {
        this.hostId = hostId;
    }

    public String nodeSize() {
        return nodeSize;
    }

    private void nodeSize(String nodeSize) {
        this.nodeSize = nodeSize;
    }

    public String pubKeyNode() {
        return pubKeyNode;
    }

    private void pubKeyNode(String pubKeyNode) {
        this.pubKeyNode = pubKeyNode;
    }

    public String pubKeyValidator() {
        return pubKeyValidator;
    }

    private void pubKeyValidator(String pubKeyValidator) {
        this.pubKeyValidator = pubKeyValidator;
    }

    public String serverState() {
        return serverState;
    }

    private void serverState(String serverState) {
        this.serverState = serverState;
    }
}
