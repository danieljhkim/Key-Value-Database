package com.kvdatabase;

import com.kvcommon.config.SystemConfig;
import com.kvdatabase.server.KVGrpcServer;
import com.kvdatabase.server.KVServer;

public class Application {
    private static final int DEFAULT_PORT = 6379;
    private static final int DEFAULT_GRPC_PORT = 9001;
    static SystemConfig config = SystemConfig.getInstance();

    public static void main(String[] args) {
        try {
            if (args.length > 0 && "grpc".equalsIgnoreCase(args[0])) {
                startGrpcServer(args);
            } else {
                startServer(args);
            }
        } catch (Exception e) {
            System.out.println("Server Failed to Start");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void startServer(String[] args) throws Exception {
        int port = Integer.parseInt(config.getProperty("server.port", String.valueOf(DEFAULT_PORT)));
        new KVServer(port).start();
    }

    public static void startGrpcServer(String[] args) throws Exception {
        int port = Integer.parseInt(config.getProperty("server.port", String.valueOf(DEFAULT_GRPC_PORT)));
        new KVGrpcServer(port).start();
    }
}