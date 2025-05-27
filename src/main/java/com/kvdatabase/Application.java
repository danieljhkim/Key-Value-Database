package com.kvdatabase;

import com.kvcommon.config.SystemConfig;
import com.kvcommon.server.KVGrpcServer;
import com.kvcommon.server.KVServer;
import com.kvdatabase.repository.KVStoreRepository;
import com.kvdatabase.service.KVServiceImpl;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Application {
    private static final Logger LOGGER = Logger.getLogger(Application.class.getName());
    private static final int DEFAULT_PORT = 6379;
    private static final int DEFAULT_GRPC_PORT = 9001;
    private static final SystemConfig CONFIG = SystemConfig.getInstance();

    public static void main(String[] args) {
        try {
            if (isGrpcMode(args)) {
                startGrpcServer();
            } else {
                startHttpServer();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Server failed to start", e);
            System.exit(1);
        }
    }

    private static boolean isGrpcMode(String[] args) {
        return args.length > 0 && "grpc".equalsIgnoreCase(args[0]);
    }

    private static void startHttpServer() throws Exception {
        int port = getPort("server.port", DEFAULT_PORT);
        KVServer server = new KVServer(port);
        addShutdownHook(server::shutdown);
        LOGGER.info("Starting HTTP server on port " + port);
        server.start();
    }

    private static void startGrpcServer() throws Exception {
        int port = getPort("server.grpc.port", DEFAULT_GRPC_PORT);
        KVGrpcServer server = new KVGrpcServer.Builder()
                .setPort(port)
                .addService(new KVServiceImpl(new KVStoreRepository()))
                .build();
        addShutdownHook(server::shutdown);
        LOGGER.info("Starting gRPC server on port " + port);
        server.start();
    }

    private static int getPort(String propertyKey, int defaultPort) {
        return Integer.parseInt(CONFIG.getProperty(propertyKey, String.valueOf(defaultPort)));
    }

    private static void addShutdownHook(Runnable shutdownTask) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Shutting down server...");
            shutdownTask.run();
        }));
    }
}