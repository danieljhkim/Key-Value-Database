package com.kvdatabase.store;

import java.util.concurrent.ConcurrentHashMap;

public class KeyValueStore {

    private final ConcurrentHashMap<String, String> store = new ConcurrentHashMap<>();
    private static final KeyValueStore instance = new KeyValueStore();

    private KeyValueStore() {}

    public static KeyValueStore getInstance() {
        return instance;
    }

    public String set(String key, String value) {
        store.put(key, value);
        return "OK";
    }

    public String get(String key) {
        return store.getOrDefault(key, "(nil)");
    }

    public String del(String key) {
        return store.remove(key) != null ? "1" : "0";
    }
}
