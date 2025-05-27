package com.kvclustermanager;

import com.kvclustermanager.cluster.ClusterNode;
import com.kvclustermanager.config.ClusterConfig;
import com.kvclustermanager.server.ClusterServer;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ClusterServerApplication {
    /**
     * Main entry point for the ClusterServer application.
     * Accepts command-line arguments for port and configuration file path.
     *
     * @param args Command-line arguments: [port] [configFilePath]
     */
    private static final Logger LOGGER = Logger.getLogger(ClusterServerApplication.class.getName());
    private static final int DEFAULT_PORT = 7000;
    private static final String DEFAULT_CONFIG_PATH = "src/main/resources/cluster-config.yaml";

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        String configFilePath = DEFAULT_CONFIG_PATH;

        // Parse command-line arguments for port and config file path
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                LOGGER.warning("Invalid port provided, using default port: " + DEFAULT_PORT);
            }
        }
        if (args.length > 1) {
            configFilePath = args[1];
        }

        try {
            LOGGER.info("Starting ClusterServer on port " + port + " with config file: " + configFilePath);
            ClusterConfig clusterConfig = new ClusterConfig(configFilePath);
            ClusterServer clusterServer = new ClusterServer(port, clusterConfig);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                LOGGER.info("Shutting down ClusterServer...");
                clusterServer.shutdown();
            }));

            clusterServer.start();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to start ClusterServer", e);
            System.exit(1);
        }
    }

}