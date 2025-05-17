package com.kvdatabase.store;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class KeyValueStore {

    private static final String OK_RESPONSE = "OK";
    private static final String NIL_RESPONSE = "(nil)";

    private static final KeyValueStore INSTANCE = new KeyValueStore();
    private final Map<String, String> store = new ConcurrentHashMap<>();

    private KeyValueStore() {}

    public static KeyValueStore getInstance() {
        return INSTANCE;
    }

    public String set(String key, String value) {
        Objects.requireNonNull(key, "Key cannot be null");
        Objects.requireNonNull(value, "Value cannot be null");

        store.put(key, value);
        return OK_RESPONSE;
    }

    public String get(String key) {
        Objects.requireNonNull(key, "Key cannot be null");

        return store.getOrDefault(key, NIL_RESPONSE);
    }

    public String del(String key) {
        Objects.requireNonNull(key, "Key cannot be null");

        return store.remove(key) != null ? "1" : "0";
    }

    public int size() {
        return store.size();
    }

    public void clear() {
        store.clear();
    }

    public boolean exists(String key) {
        Objects.requireNonNull(key, "Key cannot be null");

        return store.containsKey(key);
    }

    public Map<String, String> getAll() {
        return Collections.unmodifiableMap(store);
    }
}