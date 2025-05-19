#!/bin/bash
# client.sh - Script to start the KV Client

# Get the directory where the script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
# Project directory is one level up from the scripts directory
PROJECT_DIR="$( cd "$SCRIPT_DIR/.." && pwd )"

# Define client class
CLIENT_CLASS="com.kvclient.Run"

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
  echo "Maven is not installed. Please install Maven first."
  exit 1
fi

# Change to project directory and start the client
cd "$PROJECT_DIR" || exit 1
echo "Starting KV Client from $PROJECT_DIR..."
mvn exec:java -Dexec.mainClass="$CLIENT_CLASS"