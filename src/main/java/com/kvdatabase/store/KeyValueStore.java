package com.kvdatabase.store;

import com.fasterxml.jackson.core.type.TypeReference;
import com.kvdatabase.annotations.Timer;
import com.kvdatabase.persistence.FilePersistenceManager;
import com.kvdatabase.persistence.PersistenceManager;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.kvcommon.config.SystemConfig;

public class KeyValueStore {

    private static final Logger LOGGER = Logger.getLogger(KeyValueStore.class.getName());
    private static final SystemConfig config = SystemConfig.getInstance();
    private static final String OK_RESPONSE = "OK";
    private static final String NIL_RESPONSE = "(nil)";
    private static final KeyValueStore INSTANCE = new KeyValueStore();
    private static final int FLUSH_INTERVAL = Integer.parseInt(config.getProperty("kvdb.persistence.autoFlushInterval", "1000"));
    private static final boolean ENABLE_AUTO_FLUSH = Boolean.parseBoolean(config.getProperty("kvdb.persistence.enableAutoFlush", "true"));
    private static int curFlushInterval = 0;
    private final Map<String, String> store = new ConcurrentHashMap<>();
    private final PersistenceManager<Map<String, String>> persistenceManager;

    private KeyValueStore() {
        TypeReference<Map<String, String>> typeRef = new TypeReference<>() {};
        String persistenceFilePath = config.getProperty("kvdb.persistence.filepath");
        this.persistenceManager = new FilePersistenceManager<>(persistenceFilePath, typeRef);
        loadFromDisk();
        System.out.println(FLUSH_INTERVAL);
    }

    public static KeyValueStore getInstance() {
        return INSTANCE;
    }

    @Timer
    private void loadFromDisk() {
        try {
            Map<String, String> loadedData = persistenceManager.load();
            if (loadedData == null) {
                LOGGER.warning("Loaded data is null, initializing empty store");
                return;
            }
            store.putAll(loadedData);
            LOGGER.info("Loaded " + loadedData.size() + " entries from disk");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load data from disk", e);
        }
    }

    @Timer
    private void saveToDisk() {
        LOGGER.info("Saving data to disk");
        try {
            persistenceManager.save(store);
            curFlushInterval = 0;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to save data to disk", e);
        }
    }

    public String set(String key, String value) {
        Objects.requireNonNull(key, "Key cannot be null");
        Objects.requireNonNull(value, "Value cannot be null");
        store.put(key, value);
        curFlushInterval++;
        flush();
        return OK_RESPONSE;
    }

    @Timer
    public String get(String key) {
        Objects.requireNonNull(key, "Key cannot be null");
        return store.getOrDefault(key, NIL_RESPONSE);
    }

    public String del(String key) {
        Objects.requireNonNull(key, "Key cannot be null");
        curFlushInterval++;
        flush();
        return store.remove(key) != null ? "1" : "0";
    }

    public int size() {
        return store.size();
    }

    public void clear() {
        store.clear();
        saveToDisk();
    }

    public boolean exists(String key) {
        Objects.requireNonNull(key, "Key cannot be null");
        return store.containsKey(key);
    }

    public Map<String, String> getAll() {
        return Collections.unmodifiableMap(store);
    }

    public void flush() {
        if (ENABLE_AUTO_FLUSH && curFlushInterval >= FLUSH_INTERVAL) {
            LOGGER.info("Auto-flushing data to disk");
            saveToDisk();
        }
    }

    public void shutdown() {
        saveToDisk();
        try {
            persistenceManager.close();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error closing persistence manager", e);
        }
    }
}