package com.kvdatabase.protocol;

import com.kvdatabase.repository.BaseRepository;
import com.kvdatabase.repository.KvStoreRepository;

public class SQLCommandParser implements CommandParser {

    private static final String HELP_TEXT = """
        SQL Command Parser Usage:
        SQL INIT [table_name] - Initialize a new table (default if no name given)
        SQL USE [table_name] - Switch to an existing table
        SQL GET/SELECT [key] - Retrieve value for a given key
        SQL SET/INSERT [key] [value] - Store a key-value pair
        SQL DEL/DELETE [key] - Remove a key-value pair
        SQL CLEAR/DROP - Remove all entries from the current table
        SQL PING - Check connection to database
        SQL HELP/INFO - Display this help message""";

    private static final String INIT_TABLE_WARNING =
            "WARNING: default table initialized. Use 'SQL USE [table_name]' to initialize a different table.";
    private BaseRepository dataSource;

    public SQLCommandParser(BaseRepository repo) {
        this.dataSource = repo;
    }

    public SQLCommandParser() {
        this.dataSource = null;
    }

    public void setDataSource(BaseRepository repo) {
        this.dataSource = repo;
    }

    @Override
    @SuppressWarnings("All")
    public String process(String command) {
        if (command == null || command.isEmpty()) return "ERR: Empty command";

        String[] parts = command.split(" ");
        if (parts.length <= 1) return "ERR: Empty command";

        String cmd = parts[1].toUpperCase();
        if (!cmd.equals("INIT") && dataSource == null) {
            this.dataSource = new KvStoreRepository();
            return INIT_TABLE_WARNING;
        }
        return switch (cmd) {
            case "HELP", "INFO" -> HELP_TEXT;
            case "INIT" -> {
                if (parts.length == 2) {
                    this.dataSource = new KvStoreRepository();
                    yield "OK: Default Table initialized: " + dataSource.getTableName();
                } else if (parts.length == 3) {
                    this.dataSource = new KvStoreRepository(parts[1]);
                    yield "OK: Table initialized: " + dataSource.getTableName();
                } else {
                    yield "ERR: Usage: INIT [table_name]";
                }
            }
            case "USE" -> {
                if (parts.length != 3) yield "ERR: Usage: USE [table_name]";
                dataSource.initializeTable(parts[2]);
                yield "OK: Table initialized: " + dataSource.getTableName();
            }
            case "GET", "SELECT" -> {
                if (parts.length != 3) yield "ERR: Usage: GET [key]";
                yield dataSource.get(parts[2]);
            }
            case "SET", "INSERT" -> {
                if (parts.length != 4) yield "ERR: Usage: SET [key] [value]";
                yield String.valueOf(dataSource.put(parts[2], parts[3]));
            }
            case "DEL", "DELETE" -> {
                if (parts.length != 3) yield "ERR: Usage: DEL key";
                yield String.valueOf(dataSource.delete(parts[2]));
            }
            case "CLEAR", "DROP" -> {
                int res = dataSource.clear();
                if (res == 0) yield "ERR: No keys to delete";
                yield "OK: " + dataSource.getTableName() + " cleared";
            }
            case "PING" -> {
                boolean res = dataSource.isHealthy();
                yield res ? "PONG" : "ERR: No connection";
            }
            default -> "ERR: Unknown SQL command";
        };
    }
}
