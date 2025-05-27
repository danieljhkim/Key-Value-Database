package com.kvcommon.protocol;

import com.kvdatabase.repository.BaseRepository;
import com.kvdatabase.repository.KVDataBaseRepository;

public class SQLCommandParser extends CommandParser {

    private static final String HELP_TEXT =
            """
        SQL Command Parser Usage:
        SQL INIT [table_name] - Initialize a new table (default if no name given)
        SQL USE [table_name] - Switch to an existing table
        SQL GET [key] - Retrieve value for a given key
        SQL SET [key] [value] - Store a key-value pair
        SQL DEL/DELETE [key] - Remove a key-value pair
        SQL CLEAR/DROP/TRUNCATE - Remove all entries from the current table
        SQL PING - Check connection to database
        SQL HELP/INFO - Display this help message""";

    private static final String INIT_TABLE_WARNING =
            "WARNING: default table initialized. Use 'SQL USE [table_name]' to initialize a different table.";

    public SQLCommandParser(CommandExecutor executor) {
        super(executor);
    }
    
    public SQLCommandParser(BaseRepository repo) {
        super(new BaseRepositoryAdapter(repo));
    }

    public SQLCommandParser() {
        super(new BaseRepositoryAdapter(new KVDataBaseRepository()));
    }

    @Override
    String executeCommand(String[] parts) {
        String cmd = parts[1].trim().toUpperCase();
        if (!cmd.equals("INIT") && executor == null) {
            this.executor = new BaseRepositoryAdapter(new KVDataBaseRepository());
            return INIT_TABLE_WARNING;
        }
        cmd = cmd.trim().toUpperCase();
        return switch (cmd) {
            case "HELP", "INFO" -> HELP_TEXT;
            case "INIT" -> handleInit(parts);
            case "USE" -> handleUse(parts);
            case "GET" -> handleGet(parts);
            case "SET" -> handleSet(parts);
            case "DEL", "DELETE" -> handleDelete(parts);
            case "CLEAR", "DROP", "TRUNCATE" -> handleClear();
            case "PING" -> handlePing();
            default -> "ERR: Unknown SQL command";
        };
    }

    private String handleInit(String[] parts) {
        if (parts.length == 3) {
            KVDataBaseRepository repo = new KVDataBaseRepository(parts[2]);
            this.executor = new BaseRepositoryAdapter(repo);
            return "OK: Table initialized: " + executor.getTableName();
        }
        return "ERR: Usage: INIT [table_name]";
    }

    private String handleUse(String[] parts) {
        if (parts.length != 3) return "ERR: Usage: USE [table_name]";
        executor.initialize(parts[2]);
        return "OK: Table initialized: " + executor.getTableName();
    }

    private String handleGet(String[] parts) {
        if (parts.length != 3) return "ERR: Usage: GET [key]";
        return String.valueOf(executor.get(parts[2]));
    }

    private String handleSet(String[] parts) {
        if (parts.length != 4) return "ERR: Usage: SET [key] [value]";
        return String.valueOf(executor.put(parts[2], parts[3]));
    }

    private String handleDelete(String[] parts) {
        if (parts.length != 3) return "ERR: Usage: DEL [key]";
        return String.valueOf(executor.delete(parts[2]));
    }

    private String handleClear() {
        int res = executor.truncate();
        if (res == 0) return "ERR: No keys to delete";
        return "OK: " + executor.getTableName() + " cleared";
    }

    private String handlePing() {
        return executor.isHealthy() ? "PONG" : "ERR: No connection";
    }

    @Override
    public String getHelpText() {
        return HELP_TEXT;
    }
}
