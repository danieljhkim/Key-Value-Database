package com.kvdatabase.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FilePersistenceManager<T> implements PersistenceManager<T> {

    private static final Logger LOGGER = Logger.getLogger(FilePersistenceManager.class.getName());

    private final Path filePath;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final TypeReference<T> typeReference; // Retains type info for deserialization
    private final ObjectMapper objectMapper = new ObjectMapper();

    public FilePersistenceManager(String fileName, TypeReference<T> typeReference) {
        this.filePath = Paths.get(fileName);
        this.typeReference = typeReference;
        try {
            Files.createDirectories(filePath.getParent());
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to create directory for persistence", e);
        }
    }

    @Override
    public void save(T data) throws IOException {
        lock.writeLock().lock();
        try {
            if (data == null) {
                LOGGER.log(Level.WARNING, "Data to save is null");
                return;
            }
            if (!Files.exists(filePath.getParent())) {
                Files.createDirectories(filePath.getParent());
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to create directory for persistence", e);
        }
        try {
            objectMapper.writeValue(filePath.toFile(), data);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public T load() throws IOException {
        lock.readLock().lock();
        try {
            if (!Files.exists(filePath)) {
                LOGGER.log(Level.WARNING, "File does not exist: " + filePath);
                return null;
            }
            T out = objectMapper.readValue(filePath.toFile(), typeReference);
            if (out == null) {
                LOGGER.log(Level.WARNING, "Loaded data is null");
            }
            return out;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to read data", e);
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void close() throws IOException {
        System.out.println("Resource closed.");
    }
}
