package com.kvdatabase.repository;

import com.kvcommon.annotations.Timer;
import com.kvcommon.exception.DatabaseException;
import com.kvdatabase.storage.KVDatabase;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KVDataBaseRepository implements BaseRepository {

    private static final Logger LOGGER = Logger.getLogger(KVDataBaseRepository.class.getName());
    private static final int BATCH_SIZE = 100;

    private final KVDatabase dbConfig;
    private final String DB_NAME = "default";
    private String tableName;

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS %s (" +
                    "key VARCHAR(255) PRIMARY KEY, " +
                    "value TEXT, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

    private static final String SQL_PUT =
            "INSERT INTO %s (key, value) VALUES (?, ?) " +
                    "ON CONFLICT (key) DO UPDATE SET value = ?, updated_at = CURRENT_TIMESTAMP";

    private static final String SQL_GET = "SELECT value FROM %s WHERE key = ?";
    private static final String SQL_UPDATE =
            "UPDATE %s SET value = ?, updated_at = CURRENT_TIMESTAMP WHERE key = ?";
    private static final String SQL_DELETE = "DELETE FROM %s WHERE key = ?";
    private static final String SQL_EXISTS = "SELECT 1 FROM %s WHERE key = ? LIMIT 1";
    private static final String SQL_GET_ALL_KEYS = "SELECT key FROM %s";
    private static final String SQL_CLEAR = "DELETE FROM %s";

    public KVDataBaseRepository() {
        this.dbConfig = KVDatabase.getInstance(DB_NAME);
        this.tableName = dbConfig.getDefaultTableName(DB_NAME);
        initialize();
    }

    public KVDataBaseRepository(String tableName) {
        this.dbConfig = KVDatabase.getInstance(DB_NAME);
        this.tableName = tableName;
        initialize(tableName);
    }

    public void initialize(String tableName) {
        try (Connection conn = dbConfig.getConnection(DB_NAME);
             Statement stmt = conn.createStatement()) {
            stmt.execute(String.format(SQL_CREATE_TABLE, tableName));
            LOGGER.info("Initialized table: " + tableName);
            this.tableName = tableName;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize table: " + tableName, e);
            throw new DatabaseException("Failed to initialize table", e);
        }
    }

    public void initialize() {
        initialize(tableName);
    }

    @Timer
    public boolean put(String key, String value) {
        try (Connection conn = dbConfig.getConnection(DB_NAME);
             PreparedStatement pstmt = conn.prepareStatement(String.format(SQL_PUT, tableName))) {
            pstmt.setString(1, key);
            pstmt.setString(2, value);
            pstmt.setString(3, value);
            int rows = pstmt.executeUpdate();
            if (!conn.getAutoCommit()) {
                conn.commit();
            }
            return rows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to put key: " + key, e);
            return false;
        }
    }

    @Override
    public String get(String key) {
        if (key == null) {
            return null;
        }
        try (Connection conn = dbConfig.getConnection(DB_NAME);
             PreparedStatement pstmt = conn.prepareStatement(String.format(SQL_GET, tableName))) {
            pstmt.setString(1, key);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("value");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to get key: " + key, e);
        }
        return null;
    }

    @Override
    public boolean update(String key, String value) {
        if (key == null) {
            return false;
        }
        try (Connection conn = dbConfig.getConnection(DB_NAME);
             PreparedStatement pstmt = conn.prepareStatement(String.format(SQL_UPDATE, tableName))) {
            pstmt.setString(1, value);
            pstmt.setString(2, key);
            int rows = pstmt.executeUpdate();

            if (!conn.getAutoCommit()) {
                conn.commit();
            }
            return rows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to update key: " + key, e);
            return false;
        }
    }

    @Override
    public boolean delete(String key) {
        if (key == null) {
            return false;
        }
        try (Connection conn = dbConfig.getConnection(DB_NAME);
             PreparedStatement pstmt = conn.prepareStatement(String.format(SQL_DELETE, tableName))) {
            pstmt.setString(1, key);
            int rows = pstmt.executeUpdate();

            if (!conn.getAutoCommit()) {
                conn.commit();
            }
            return rows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to delete key: " + key, e);
            return false;
        }
    }

    @Override
    public boolean exists(String key) {
        if (key == null) {
            return false;
        }
        try (Connection conn = dbConfig.getConnection(DB_NAME);
             PreparedStatement pstmt = conn.prepareStatement(String.format(SQL_EXISTS, tableName))) {
            pstmt.setString(1, key);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to check key existence: " + key, e);
            return false;
        }
    }

    @Override
    @Timer
    public List<String> getAllKeys() {
        List<String> keys = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection(DB_NAME);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(String.format(SQL_GET_ALL_KEYS, tableName))) {
            while (rs.next()) {
                keys.add(rs.getString("key"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to get all keys", e);
        }
        return keys;
    }

    @Override
    @Timer
    public Map<String, String> getMultiple(List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String> result = new HashMap<>();
        // Handle large key lists with batching
        if (keys.size() > BATCH_SIZE) {
            return getMultipleInBatches(keys);
        }
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            placeholders.append(i > 0 ? ",?" : "?");
        }
        String sql = "SELECT key, value FROM " + tableName + " WHERE key IN (" + placeholders + ")";
        try (Connection conn = dbConfig.getConnection(DB_NAME);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < keys.size(); i++) {
                pstmt.setString(i+1, keys.get(i));
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getString("key"), rs.getString("value"));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to get multiple keys", e);
        }
        return result;
    }

    private Map<String, String> getMultipleInBatches(List<String> keys) {
        Map<String, String> result = new HashMap<>();
        for (int i = 0; i < keys.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, keys.size());
            List<String> batch = keys.subList(i, end);
            result.putAll(getMultiple(batch));
        }
        return result;
    }

    @Override
    public int truncate() {
        try (Connection conn = dbConfig.getConnection(DB_NAME);
             Statement stmt = conn.createStatement()) {
            int result = stmt.executeUpdate(String.format(SQL_CLEAR, tableName));
            if (!conn.getAutoCommit()) {
                conn.commit();
            }
            return result;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to clear table: " + tableName, e);
            return 0;
        }
    }

    @Override
    public boolean isHealthy() {
        return dbConfig.isHealthy(DB_NAME);
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public void shutdown() {
        try {
            dbConfig.shutdown();
            LOGGER.info("Database connection closed for table: " + tableName);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to close database connection", e);
        }
    }
}