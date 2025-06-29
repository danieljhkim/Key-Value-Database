#!/bin/bash

# run_cluster.sh - Script to start the KV Cluster Server - make sure to package the project first

# Get the directory where the script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

PROJECT_DIR="$( cd "$SCRIPT_DIR/.." && pwd )/kv.coordinator"

CLUSTER_SERVER_JAR="$PROJECT_DIR/target/kv.coordinator-1.0-SNAPSHOT.jar"
CONFIG_FILE="$PROJECT_DIR/src/main/resources/cluster-config.yaml"

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