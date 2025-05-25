package com.kvdatabase.protocol;

import com.kvdatabase.store.KeyValueStore;

public class KVCommandParser implements CommandParser {

    private static final String HELP_TEXT = """
        KV Command Parser Usage:
        SET [key] [value] - Store a key-value pair
        GET [key] - Retrieve value for a given key
        DEL [key] - Remove a key-value pair
        EXISTS [key] - Check if a key exists (returns 1 if exists, 0 if not)
        SIZE - Return the number of key-value pairs stored
        CLEAR - Remove all entries
        ALL - Return all key-value pairs
        PING - Check connection
        SHUTDOWN/QUIT/TERMINATE - Close the database connection
        HELP/INFO - Display this help message""";

    private KeyValueStore dataSource;

    public KVCommandParser(KeyValueStore store) {
        this.dataSource = store;
    }

    public void setDataSource(KeyValueStore store) {
        this.dataSource = store;
    }
    @Override
    public String process(String command) {
        String[] parts = command.split(" ");
        if (parts.length == 0) return "ERR: Empty command";

        String cmd = parts[0].toUpperCase();
        return switch (cmd) {
            case "HELP", "INFO" -> HELP_TEXT;
            case "SET" -> {
                if (parts.length != 3) yield "ERR: Usage: SET key value";
                yield dataSource.set(parts[1], parts[2]);
            }
            case "GET" -> {
                if (parts.length != 2) yield "ERR: Usage: GET key";
                yield dataSource.get(parts[1]);
            }
            case "DEL" -> {
                if (parts.length != 2) yield "ERR: Usage: DEL key";
                yield dataSource.del(parts[1]);
            }
            case "EXISTS" -> {
                if (parts.length != 2) yield "ERR: Usage: EXISTS key";
                yield dataSource.exists(parts[1]) ? "1" : "0";
            }
            case "SIZE" -> String.valueOf(dataSource.size());
            case "CLEAR" -> {
                dataSource.clear();
                yield "OK";
            }
            case "ALL" -> {
                StringBuilder sb = new StringBuilder();
                dataSource.getAll().forEach((k, v) -> sb.append(k).append(":").append(v).append("\n"));
                yield sb.toString();
            }
            case "SHUTDOWN", "QUIT", "TERMINATE" -> {
                dataSource.shutdown();
                yield "OK";
            }
            case "PING" -> "PONG";

            default -> "ERR: Unknown command";
        };
    }


}
