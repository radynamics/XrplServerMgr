package com.radynamics.xrplservermgr.xrpl.rippled;

public class CommandUserInput {
    private final String command;
    private final String arguments;

    public CommandUserInput(String command, String arguments) {
        this.command = command;
        this.arguments = arguments;
    }

    public static CommandUserInput parse(String text) {
        var indexFirstDelimiter = text.indexOf(" ");
        if (indexFirstDelimiter == -1) {
            // Eg "ledger_closed"
            return new CommandUserInput(text, null);
        } else {
            // Eg "ledger_data {"binary":false,"ledger_index":"validated"}"
            // Eg "ledger_closed {}"
            var cmd = text.substring(0, indexFirstDelimiter);
            var args = text.substring(indexFirstDelimiter + 1);
            return new CommandUserInput(cmd, args);
        }
    }

    public String command() {
        return command;
    }

    public String arguments() {
        return arguments;
    }

    public boolean hasArguments() {
        return arguments != null;
    }

    @Override
    public String toString() {
        return "%s %s".formatted(command, arguments);
    }
}
