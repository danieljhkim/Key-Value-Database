#!/bin/bash

# run_cluster.sh - Script to start the KV Cluster Server

# Get the directory where the script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Project directory is one level up from the scripts directory
PROJECT_DIR="$( cd "$SCRIPT_DIR/.." && pwd )/kv.coordinator"

# Paths
CLUSTER_SERVER_JAR="$PROJECT_DIR/target/kv.coordinator-1.0-SNAPSHOT.jar"
CONFIG_FILE="$PROJECT_DIR/src/main/resources/cluster-config.yaml"

# Ensure logs directory exists
mkdir -p "$PROJECT_DIR/logs"

# Check if the configuration file exists
if [ ! -f "$CONFIG_FILE" ]; then
  echo "Configuration file not found: $CONFIG_FILE"
  exit 1
fi

# Start ClusterServer
echo "Starting ClusterServer..."
nohup java -jar "$CLUSTER_SERVER_JAR" > "$PROJECT_DIR/logs/cluster-server.log" 2>&1 &

echo "ClusterServer started. Node servers will be managed by the ClusterServer."