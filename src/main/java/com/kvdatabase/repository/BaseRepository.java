package com.kvdatabase.repository;

import java.util.List;
import java.util.Map;

public interface BaseRepository {
    boolean put(String key, String value);
    String get(String key);
    boolean update(String key, String value);
    boolean delete(String key);
    boolean exists(String key);
    List<String> getAllKeys();
    Map<String, String> getMultiple(List<String> keys);
    int clear();
}
