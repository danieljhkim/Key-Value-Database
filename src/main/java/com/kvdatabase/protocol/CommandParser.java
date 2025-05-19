package com.kvdatabase.protocol;

import com.kvdatabase.store.KeyValueStore;

public class CommandParser {
    public static String process(String command, KeyValueStore store) {
        String[] parts = command.split(" ");
        if (parts.length == 0) return "ERR: Empty command";

        String cmd = parts[0].toUpperCase();
        return switch (cmd) {
            case "SET" -> {
                if (parts.length != 3) yield "ERR: Usage SET key value";
                yield store.set(parts[1], parts[2]);
            }
            case "GET" -> {
                if (parts.length != 2) yield "ERR: Usage GET key";
                yield store.get(parts[1]);
            }
            case "DEL" -> {
                if (parts.length != 2) yield "ERR: Usage DEL key";
                yield store.del(parts[1]);
            }
            case "EXISTS" -> {
                if (parts.length != 2) yield "ERR: Usage EXISTS key";
                yield store.exists(parts[1]) ? "1" : "0";
            }
            case "SIZE" -> String.valueOf(store.size());
            case "CLEAR" -> {
                store.clear();
                yield "OK";
            }
            case "ALL" -> {
                StringBuilder sb = new StringBuilder();
                store.getAll().forEach((k, v) -> sb.append(k).append(":").append(v).append("\n"));
                yield sb.toString();
            }
            case "SHUTDOWN", "QUIT", "TERMINATE" -> {
                store.shutdown();
                yield "OK";
            }
            case "PING" -> "PONG";

            default -> "ERR: Unknown command";
        };
    }
}
