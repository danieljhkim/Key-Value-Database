package com.kvdb.kvclustercoordinator.server;

import com.kvdb.kvclustercoordinator.cluster.ClusterNode;
import com.kvdb.kvclustercoordinator.config.ClusterConfig;
import com.kvdb.kvclustercoordinator.handler.ClusterClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
    private final List<ClusterNode> clusterNodes = new ArrayList<>();
    private final ClusterConfig clusterConfig;
    private int port;
    private ServerSocket serverSocket;
    private boolean running = false;
    private final int threadPoolSize = 10;
    private ExecutorService threadPool;
    private int nodeCount = 0;
    private int nodeIdx = 0;

    public ClusterServer(int port, String configFilePath) {
        this.port = port;
        this.clusterConfig = new ClusterConfig(configFilePath);
    }

    public ClusterServer(int port, ClusterConfig clusterConfig) {
        this.port = port;
        this.clusterConfig = clusterConfig;
    }

    public void start() {
        if (running) {
            LOGGER.warning("Server is already running");
            return;
        }
        LOGGER.info("Starting ClusterServer...");
        initializeClusterNodes();
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
                ClusterNode clusterNode = getNextNode();
                if (clusterNode == null) {
                    LOGGER.warning("No available cluster nodes to handle the request");
                    clientSocket.close();
                    continue;
                }
                threadPool.execute(new ClusterClientHandler(clusterNode, clientSocket));
            } catch (IOException e) {
                if (running) {
                    LOGGER.log(Level.WARNING, "Error accepting client connection", e);
                }
            }
        }
    }

    private void initializeClusterNodes() {
        try {
            clusterNodes.clear();
            clusterNodes.addAll(clusterConfig.getNodes());
            this.nodeCount = clusterNodes.size();
            startClusterNodes(clusterNodes);
            LOGGER.info("Initialized " + clusterNodes.size() + " cluster nodes from configuration");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize cluster nodes", e);
            throw new RuntimeException("Failed to initialize cluster nodes", e);
        }
    }

    private void startClusterNodes(List<ClusterNode> clusterNodes) {
        String coordinatorDir = System.getProperty("user.dir");
        String serverJarPath = coordinatorDir + "/../kv.server/target/kv.server-1.0-SNAPSHOT.jar"; //TODO:: Adjust path dynamically

        for (ClusterNode node : clusterNodes) {
            try {
                String command = String.format(
                        "java -jar %s %d %s",
                        serverJarPath,
                        node.getPort(),
                        node.isGrpc ? "grpc" : "http"
                );
                ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
                processBuilder.inheritIO();
                processBuilder.start();
                LOGGER.info("Started node: " + node.getId() + " on port: " + node.getPort());
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to start node: " + node.getId(), e);
            }
        }
    }

    public void shutdown() {
        LOGGER.info("Shutting down ClusterServer...");
        shutdownClusterNodes();
        if (!running) {
            return;
        }
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

    private void shutdownClusterNodes() {
        for (ClusterNode node : clusterNodes) {
            LOGGER.info("Shutting down node: " + node.getId());
            // TODO: shutdown nodes
        }
    }

    public List<ClusterNode> getClusterNodes() {
        return Collections.unmodifiableList(clusterNodes);
    }

    public Optional<ClusterNode> getNodeById(String nodeId) {
        return clusterNodes.stream()
                .filter(node -> node.getId().equals(nodeId))
                .findFirst();
    }

    private ClusterNode getNextNode() {
        if (nodeCount == 0) {
            return null;
        }
        ClusterNode node = clusterNodes.get(nodeIdx);
        nodeIdx = (nodeIdx + 1) % nodeCount;
        return node;
    }


}
