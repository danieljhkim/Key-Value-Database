package com.kvdatabase.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JsonPersistenceManager implements PersistenceManager<Map<String, String>> {

    private static final Logger LOGGER = Logger.getLogger(JsonPersistenceManager.class.getName());

    private final Path filePath;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonPersistenceManager(String fileName) {
        this.filePath = Paths.get(fileName);
        try {
            Files.createDirectories(filePath.getParent());
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to create directory for persistence", e);
        }
    }

    @Override
    public void save(Map<String, String> data) throws IOException {
        lock.writeLock().lock();
        try {
            objectMapper.writeValue(filePath.toFile(), data);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Map<String, String> load() throws IOException {
        lock.readLock().lock();
        try {
            if (!Files.exists(filePath)) {
                return new HashMap<>();
            }
            return objectMapper.readValue(filePath.toFile(), Map.class);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to read JSON data", e);
            return new HashMap<>();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void close() throws IOException {
        System.out.println("Resource closed.");
    }
}
