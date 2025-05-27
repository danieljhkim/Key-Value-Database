package com.kvclustermanager.server;

import com.kvcommon.server.KVServer;
import com.kvclustermanager.cluster.ClusterNode;
import com.kvclustermanager.config.ClusterConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClusterServer extends KVServer {
    private static final Logger LOGGER = Logger.getLogger(ClusterServer.class.getName());
    private final List<ClusterNode> clusterNodes = new ArrayList<>();
    private final ClusterConfig clusterConfig;


    public ClusterServer(int port, String configFilePath) {
        super(port);
        this.clusterConfig = new ClusterConfig(configFilePath);
    }


    public ClusterServer(int port, ClusterConfig clusterConfig) {
        super(port);
        this.clusterConfig = clusterConfig;
    }

    @Override
    public void start() {
        LOGGER.info("Starting ClusterServer...");
        super.start();
        initializeClusterNodes();
        LOGGER.info("ClusterServer started successfully.");
    }

    @Override
    public void shutdown() {
        LOGGER.info("Shutting down ClusterServer...");
        shutdownClusterNodes();
        super.shutdown();
        LOGGER.info("ClusterServer shut down successfully.");
    }

    private void initializeClusterNodes() {
        try {
            clusterNodes.clear();
            clusterNodes.addAll(clusterConfig.getNodes());
            LOGGER.info("Initialized " + clusterNodes.size() + " cluster nodes from configuration");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize cluster nodes", e);
            throw new RuntimeException("Failed to initialize cluster nodes", e);
        }
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

    private void startClusterNodes() {
        for (ClusterNode node : clusterConfig.getNodes()) {
            try {
                String command = String.format(
                        "java -jar target/kvdb-1.0-SNAPSHOT.jar %d %s",
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
}
