package com.kvdb.kvclustercoordinator.server;

import com.kvdb.kvclustercoordinator.cluster.ClusterManager;
import com.kvdb.kvclustercoordinator.handler.ClusterClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ClusterServer is the main server class that manages cluster nodes and handles client connections.
 * It initializes cluster nodes from a configuration file and accepts client connections to handle requests.
 */
public class ClusterServer {

    private static final Logger LOGGER = Logger.getLogger(ClusterServer.class.getName());
    private final ClusterManager clusterManager;
    private int port;
    private ServerSocket serverSocket;
    private boolean running = false;
    private final int threadPoolSize = 10;
    private ExecutorService threadPool;


    public ClusterServer(int port, ClusterManager clusterManager) {
        this.port = port;
        this.clusterManager = clusterManager;
    }

    public void start() {
        if (running) {
            LOGGER.warning("Server is already running");
            return;
        }
        LOGGER.info("Starting ClusterServer...");
        this.clusterManager.initializeClusterNodes();
        LOGGER.info("ClusterServer started successfully.");
        threadPool = Executors.newFixedThreadPool(threadPoolSize);
        try {
            serverSocket = new ServerSocket(port);
            running = true;
            LOGGER.info("Server started on port " + port);
            acceptConnectionLoop();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to start server on port " + port, e);
            shutdown();
        }
    }

    public void acceptConnectionLoop() {
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                LOGGER.info("Accepted connection from " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
                threadPool.execute(new ClusterClientHandler(clientSocket, clusterManager));
            } catch (IOException e) {
                if (running) {
                    LOGGER.log(Level.WARNING, "Error accepting client connection", e);
                }
            }
        }
    }

    public void shutdown() {
        LOGGER.info("Shutting down ClusterServer...");
        if (!running) {
            return;
        }
        this.clusterManager.shutdownClusterNodes();
        running = false;
        LOGGER.info("Shutting down server...");
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error closing server socket", e);
        }
        if (threadPool != null) {
            threadPool.shutdown();
            try {
                if (!threadPool.awaitTermination(10, TimeUnit.SECONDS)) {
                    threadPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                threadPool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        LOGGER.info("ClusterServer shut down successfully.");
    }

}
