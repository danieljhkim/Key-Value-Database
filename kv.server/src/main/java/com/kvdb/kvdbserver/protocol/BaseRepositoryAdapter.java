package com.kvdb.kvdbserver.protocol;

import com.kvdb.kvcommon.protocol.CommandExecutor;
import com.kvdb.kvdbserver.repository.BaseRepository;

/**
 * Adapter that wraps a BaseRepository and implements CommandExecutor
 */
public class BaseRepositoryAdapter implements CommandExecutor {
    
    private final BaseRepository repository;
    
    public BaseRepositoryAdapter(BaseRepository repository) {
        this.repository = repository;
    }
    
    @Override
    public String get(String key) {
        return repository.get(key);
    }
    
    @Override
    public boolean put(String key, String value) {
        return repository.put(key, value);
    }
    
    @Override
    public boolean delete(String key) {
        return repository.delete(key);
    }
    
    @Override
    public boolean exists(String key) {
        return repository.exists(key);
    }
    
    @Override
    public int truncate() {
        return repository.truncate();
    }
    
    @Override
    public void shutdown() {
        repository.shutdown();
    }
    
    @Override
    public boolean isHealthy() {
        return repository.isHealthy();
    }
    
    @Override
    public String getTableName() {
        return repository.getTableName();
    }
    
    @Override
    public void initialize(String name) {
        repository.initialize(name);
    }
}
