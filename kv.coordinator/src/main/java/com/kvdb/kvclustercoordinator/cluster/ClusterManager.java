package com.kvdb.kvclustercoordinator.cluster;

import com.kvdb.kvclustercoordinator.config.ClusterConfig;
import com.kvdb.kvclustercoordinator.sharding.ShardingStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClusterManager {

    Logger LOGGER = Logger.getLogger(ClusterManager.class.getName());
    private ClusterConfig clusterConfig;
    private ShardingStrategy shardingStrategy;
    private List<ClusterNode> clusterNodes = new ArrayList<>();
    private boolean initialized = false;

    public ClusterManager(ClusterConfig clusterConfig, ShardingStrategy shardingStrategy) {
        this.clusterConfig = clusterConfig;
        this.shardingStrategy = shardingStrategy;
    }

    public void initializeClusterNodes() {
        this.clusterNodes.clear();
        try {
            for (ClusterNode node : clusterConfig.getNodes()) {
                boolean good = startClusterNodes(node);
                if (good) {
                    clusterNodes.add(node);
                } else {
                    LOGGER.warning("Failed to start node: " + node.getId() + " on port: " + node.getPort());
                }
            }
            LOGGER.info("Initialized " + clusterNodes.size() + " cluster nodes from configuration");
            this.initialized = true;
        } catch (Exception e) {
            this.initialized = false;
            LOGGER.log(Level.SEVERE, "Failed to initialize cluster nodes", e);
            throw new RuntimeException("Failed to initialize cluster nodes", e);
        }
    }

    private boolean startClusterNodes(ClusterNode node) {
        String coordinatorDir = System.getProperty("user.dir");
        String serverJarPath = coordinatorDir + "/kv.server/target/kv.server-1.0-SNAPSHOT.jar"; //TODO:: Adjust path dynamically
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
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to start node: " + node.getId(), e);
        }
        return false;
    }

    public ClusterNode getNodeById(String id) {
        if (!initialized) {
            LOGGER.warning("Cluster nodes not initialized");
            return null;
        }
        for (ClusterNode node : clusterNodes) {
            if (node.getId().equals(id)) {
                return node;
            }
        }
        LOGGER.warning("No node found with ID: " + id);
        return null;
    }

    public ClusterNode getShardedNode(String[] command) {
        if (!initialized) {
            LOGGER.warning("Cluster nodes not initialized");
            return null;
        }
        if (command.length < 3) {
            LOGGER.warning("Invalid command format, expected at least 3 parts");
            return shardingStrategy.getShard(clusterNodes);
        }
        String key = command[2];
        ClusterNode node = shardingStrategy.getShardWithKey(key, clusterNodes);
        LOGGER.info("Routing command to node: " + node.getId() + " for key: " + key);
        return node;
    }

    public void shutdownClusterNodes() {
        LOGGER.info("Shutting down cluster nodes...");
        for (ClusterNode node : clusterNodes) {
            try {
                node.shutdown();
                LOGGER.info("Node " + node.getId() + " shut down successfully.");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to shut down node: " + node.getId(), e);
            }
        }
        clusterNodes.clear();
        initialized = false;
    }

}
