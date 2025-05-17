package com.kvdatabase.protocol;

import com.kvdatabase.store.KeyValueStore;

public class CommandParser {
    public static String process(String command, KeyValueStore store) {
        String[] parts = command.split(" ");
        if (parts.length == 0) return "ERR: Empty command";

        String cmd = parts[0].toUpperCase();
        switch (cmd) {
            case "SET":
                if (parts.length != 3) return "ERR: Usage SET key value";
                return store.set(parts[1], parts[2]);
            case "GET":
                if (parts.length != 2) return "ERR: Usage GET key";
                return store.get(parts[1]);
            case "DEL":
                if (parts.length != 2) return "ERR: Usage DEL key";
                return store.del(parts[1]);
            default:
                return "ERR: Unknown command";
        }
    }
}
