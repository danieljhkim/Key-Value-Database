package com.kvcommon.protocol;

public abstract class CommandParser {

    static final String OK_RESPONSE = "OK";
    static final String ERROR_RESPONSE = "ERROR";
    static final String NIL_RESPONSE = "(nil)";
    protected CommandExecutor executor;

    public CommandParser(CommandExecutor executor) {
        this.executor = executor;
    }

    public CommandParser() {
        this(null);
    }

    public void setCommandExecutor(CommandExecutor executor) {
        this.executor = executor;
    }

    public CommandExecutor getCommandExecutor() {
        return executor;
    }

    abstract String getHelpText();

    abstract String executeCommand(String[] args);

    public String process(String[] parts) {
        if (parts.length == 0) return "ERR: Empty command";
        return executeCommand(parts);
    }
}
