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
            default -> "ERR: Unknown command";
        };
    }
}
