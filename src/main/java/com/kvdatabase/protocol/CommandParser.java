package com.kvdatabase.protocol;

import com.kvdatabase.repository.BaseRepository;

public abstract class CommandParser {

    static final String OK_RESPONSE = "OK";
    static final String ERROR_RESPONSE = "ERROR";
    static final String NIL_RESPONSE = "(nil)";
    protected BaseRepository dataSource;

    public CommandParser(BaseRepository repo) {
        this.dataSource = repo;
    }

    public CommandParser() {
        this(null);
    }

    abstract String getHelpText();

    abstract String executeCommand(String[] args);

    public String process(String[] parts) {
        if (parts.length == 0) return "ERR: Empty command";
        return executeCommand(parts);
    }
}
