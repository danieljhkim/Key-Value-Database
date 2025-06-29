package com.kvdb.kvcommon.protocol;

/**
 * Common interface for all command executors (repository, GRPC client, etc.)
 */
public interface CommandExecutor {
    
    /**
     * Get a value by key
     */
    String get(String key);
    
    /**
     * Set a value for a key
     */
    boolean put(String key, String value);
    
    /**
     * Delete a key-value pair
     */
    boolean delete(String key);
    
    /**
     * Check if a key exists
     */
    default boolean exists(String key) {
        return get(key) != null;
    }
    
    /**
     * Remove all entries
     */
    default int truncate() {
        return 0; // Default implementation
    }
    
    /**
     * Shutdown the executor
     */
    default void shutdown() {
        // Default implementation does nothing
    }
    
    /**
     * Check if the executor is healthy
     */
    default boolean isHealthy() {
        return true;
    }
    
    /**
     * Get the name of the data source (table name, etc.)
     */
    default String getTableName() {
        return "default";
    }
    
    /**
     * Initialize with a specific name (e.g., table name)
     */
    default void initialize(String name) {
        // Default implementation does nothing
    }
}
