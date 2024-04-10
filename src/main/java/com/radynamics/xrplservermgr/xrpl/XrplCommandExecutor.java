package com.radynamics.xrplservermgr.xrpl;

import com.google.gson.JsonObject;
import com.radynamics.xrplservermgr.sshapi.SshApiException;
import com.radynamics.xrplservermgr.sshapi.SshSession;
import com.radynamics.xrplservermgr.xrpl.rippled.CommandUserInput;
import com.radynamics.xrplservermgr.xrpl.rippled.RippledCommandException;
import org.apache.commons.lang3.StringUtils;

public class XrplCommandExecutor {
    private final SshSession session;
    private final String binaryPath;
    private final String configPath;

    public XrplCommandExecutor(SshSession session, String binaryPath, String configPath) {
        this.session = session;
        this.binaryPath = binaryPath;
        this.configPath = configPath;
    }

    public JsonObject execute(String command) throws SshApiException, RippledCommandException {
        var cmd = CommandUserInput.parse(command);
        if (cmd.hasArguments()) {
            return execute(cmd.command(), cmd.arguments());
        } else {
            return executeValidateResponse("%s --silent".formatted(command));
        }
    }

    public JsonObject execute(String command, String arguments) throws SshApiException, RippledCommandException {
        // Eg "rippled -q json ledger_closed '{}'" (https://xrpl.org/docs/references/http-websocket-apis/public-api-methods/utility-methods/json/)
        var sb = new StringBuilder();
        sb.append("-q json ");
        sb.append(command);
        if (!StringUtils.isEmpty(arguments)) {
            sb.append(" '%s' ".formatted(arguments));
        }
        return executeValidateResponse(sb.toString());
    }

    private JsonObject executeValidateResponse(String args) throws SshApiException, RippledCommandException {
        // TODO: rippled might need sudo (depending on installation)
        // TODO: confArg only needed if installed using PortableBuildInstaller
        var command = "%s %s --conf %s".formatted(binaryPath, args, configPath);
        var response = session.execute(command);

        if (response.isEmpty()) {
            throw new RippledCommandException("No response returned. Command '%s'".formatted(command));
        }

        var json = response.asJson();
        if (!response.success()) {
            throw new RippledCommandException("Command failed. %s".formatted(json.get("error_message").getAsString()), json);
        }

        var result = json.get("result").getAsJsonObject();
        if (!"success".equals(result.get("status").getAsString())) {
            throw new RippledCommandException("Command failed. %s".formatted(result.get("error_message").getAsString()), json);
        }
        return result;
    }
}
