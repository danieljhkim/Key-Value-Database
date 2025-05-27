#!/bin/bash

# Get the directory where the script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
# Project directory is one level up from the scripts directory
PROJECT_DIR="$( cd "$SCRIPT_DIR/.." && pwd )"

# Paths
CLUSTER_SERVER_JAR="$PROJECT_DIR/target/cluster-server-1.0-SNAPSHOT.jar"
NODE_JAR="$PROJECT_DIR/target/kvdb-1.0-SNAPSHOT.jar"
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

# Start Nodes
echo "Starting cluster nodes..."
while IFS= read -r line; do
  if [[ $line =~ ^-.*id:\ (.*)$ ]]; then
    NODE_ID="${BASH_REMATCH[1]}"
  elif [[ $line =~ ^.*port:\ (.*)$ ]]; then
    PORT="${BASH_REMATCH[1]}"
  elif [[ $line =~ ^.*useGrpc:\ (.*)$ ]]; then
    MODE="${BASH_REMATCH[1]}"
    echo "Starting node $NODE_ID on port $PORT in $MODE mode..."
    nohup java -jar "$NODE_JAR" "$MODE" > "$PROJECT_DIR/logs/$NODE_ID.log" 2>&1 &
  fi
done < "$CONFIG_FILE"

echo "All servers started."
