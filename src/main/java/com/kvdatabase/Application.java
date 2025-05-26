package com.kvdatabase;

import com.kvcommon.config.SystemConfig;
import com.kvdatabase.server.KVServer;

public class Application {
    private static final int DEFAULT_PORT = 6379;
    static SystemConfig config = SystemConfig.getInstance();
    public static void main(String[] args) {
        int port;
        try {
            port = Integer.parseInt(config.getProperty("kvdb.server.port", String.valueOf(DEFAULT_PORT)));
        } catch (NumberFormatException ex) {
            System.out.println("Warning: Invalid port configuration. Using default port: " + DEFAULT_PORT);
            port = DEFAULT_PORT;
        }
        System.out.println("Starting KV Server on port " + port + "...");
        KVServer server = new KVServer(port);
        server.start();
    }
}