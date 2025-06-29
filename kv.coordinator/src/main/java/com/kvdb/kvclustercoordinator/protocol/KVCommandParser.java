package com.kvdb.kvclustercoordinator.protocol;


import com.kvdb.kvcommon.protocol.CommandExecutor;

public class KVCommandParser {

    private static final String HELP_TEXT =
            """
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


    public String executeCommand(String[] parts, CommandExecutor executor) {
        String cmd = parts[0].trim().toUpperCase();
        return switch (cmd) {
            case "HELP", "INFO" -> HELP_TEXT;
            case "SET" -> handleSet(parts, executor);
            case "GET" -> handleGet(parts, executor);
            case "DEL" -> handleDelete(parts, executor);
            case "EXISTS" -> handleExists(parts, executor);
            case "DROP" -> handleDrop(executor);
            case "SHUTDOWN", "QUIT", "TERMINATE" -> handleShutdown(executor);
            case "PING" -> handlePing();
            default -> "ERR: Unknown command";
        };
    }

    private String handleSet(String[] parts, CommandExecutor executor) {
        if (parts.length != 3) return "ERR: Usage: SET key value";
        return String.valueOf(executor.put(parts[1], parts[2]));
    }

    private String handleGet(String[] parts, CommandExecutor executor) {
        if (parts.length != 2) return "ERR: Usage: GET key";
        return executor.get(parts[1]);
    }

    private String handleDelete(String[] parts, CommandExecutor executor) {
        if (parts.length != 2) return "ERR: Usage: DEL key";
        return String.valueOf(executor.delete(parts[1]));
    }

    private String handleExists(String[] parts, CommandExecutor executor) {
        if (parts.length != 2) return "ERR: Usage: EXISTS key";
        return executor.exists(parts[1]) ? "1" : "0";
    }

    private String handleDrop(CommandExecutor executor) {
        executor.truncate();
        return "OK";
    }

    private String handleShutdown(CommandExecutor executor) {
        executor.shutdown();
        return "OK";
    }

    private String handlePing() {
        return "PONG";
    }

    public String getHelpText() {
        return HELP_TEXT;
    }
}
