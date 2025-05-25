#!/bin/bash
# run_client.sh - Script to start the KV Client

# Get the directory where the script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
# Project directory is one level up from the scripts directory
PROJECT_DIR="$( cd "$SCRIPT_DIR/.." && pwd )"

# Define client class
CLIENT_CLASS="com.kvclient.Run"

# Default values
DEFAULT_HOST=""
DEFAULT_PORT=""

# Parse command line arguments
HOST=$DEFAULT_HOST
PORT=$DEFAULT_PORT

# Process arguments
while [[ $# -gt 0 ]]; do
  case "$1" in
    -h|--host)
      HOST="$2"
      shift 2
      ;;
    -p|--port)
      PORT="$2"
      shift 2
      ;;
    --help)
      echo "Usage: $0 [-h|--host hostname] [-p|--port port]"
      echo "  Default host: $DEFAULT_HOST"
      echo "  Default port: $DEFAULT_PORT"
      exit 0
      ;;
    *)
      echo "Unknown option: $1"
      echo "Use --help for usage information"
      exit 1
      ;;
  esac
done

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
  echo "Maven is not installed. Please install Maven first."
  exit 1
fi

# Change to project directory and start the client
cd "$PROJECT_DIR" || exit 1
echo "Starting KV Client from $PROJECT_DIR..."
echo "Connecting to $HOST:$PORT..."

# Pass host and port as system properties
mvn exec:java -Dexec.mainClass="$CLIENT_CLASS" -Dkvclient.host="$HOST" -Dkvclient.port="$PORT"