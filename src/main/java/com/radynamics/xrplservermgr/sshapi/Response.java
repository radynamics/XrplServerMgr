package com.radynamics.xrplservermgr.sshapi;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;

public class Response {
    private final boolean success;
    private final String response;
    private String errorOutput;
    private int exitStatus;

    private Response(boolean success, String response) {
        this.success = success;
        this.response = response;
    }

    public static Response success(String message) {
        return new Response(true, message);
    }

    public static Response error(String message) {
        return new Response(false, message);
    }

    public boolean success() {
        return success;
    }

    public JsonObject asJson() {
        return JsonParser.parseString(response).getAsJsonObject();
    }

    public String asString() {
        return response;
    }

    public boolean isEmpty() {
        return StringUtils.isEmpty(response);
    }

    public Response exitStatus(int exitStatus) {
        this.exitStatus = exitStatus;
        return this;
    }

    public int exitStatus() {
        return exitStatus;
    }

    public String errorOutput() {
        return this.errorOutput;
    }

    public Response errorOutput(String errorOutput) {
        this.errorOutput = errorOutput;
        return this;
    }
}
