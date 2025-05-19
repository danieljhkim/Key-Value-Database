#!/bin/bash
# server.sh - Script to start the KV Server

# Get the directory where the script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
# Project directory is one level up from the scripts directory
PROJECT_DIR="$( cd "$SCRIPT_DIR/.." && pwd )"

# Define server class
SERVER_CLASS="com.kvdatabase.Application"

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
  echo "Maven is not installed. Please install Maven first."
  exit 1
fi

# Change to project directory and start the server
cd "$PROJECT_DIR" || exit 1
echo "Starting KVServer from $PROJECT_DIR..."
mvn exec:java -Dexec.mainClass="$SERVER_CLASS"