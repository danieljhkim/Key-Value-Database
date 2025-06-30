package com.kvdb.kvdbserver.persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

// TODO: fail handling and retry logic

public class WALManager {

    // Write-Ahead Log (WAL) file path

    private final Path walFile;

    public WALManager(String fileName) {
        this.walFile = Paths.get(fileName);
    }

    public synchronized void log(String operation, String key, String value) {
        try (BufferedWriter writer = Files.newBufferedWriter(walFile, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(operation + " " + key + " " + (value != null ? value : "") + "\n");
        } catch (IOException e) {
            System.err.println("Failed to log operation: " + operation + " " + key + " " + value);
            e.printStackTrace();
        }
    }

    public synchronized List<String[]> replay() {
        List<String[]> ops = new ArrayList<>();
        if (!Files.exists(walFile)) return ops;

        try (BufferedReader reader = Files.newBufferedReader(walFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split(" ", 3);
                ops.add(parts);
            }
        } catch(IOException e) {
            System.err.println("Failed to read WAL file: " + walFile);
            e.printStackTrace();
        }
        return ops;
    }

    public synchronized void clear() {
        try {
            Files.deleteIfExists(walFile);
        } catch (IOException e) {
            System.err.println("Failed to clear WAL file: " + walFile);
            e.printStackTrace();
        }
    }
}