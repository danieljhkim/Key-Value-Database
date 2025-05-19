# KvDB - Distributed Key-Value Database

---

A Redis-like distributed key-value store implemented in Java. 
This project provides a lightweight, in-memory database with persistence options and basic Redis command compatibility.


## Usage

### Prerequisites
- Java 11 or higher
- Maven (for building)

### Starting the Server and Client

Make the scripts executable:
```bash
chmod +x scripts/run_server.sh scripts/run_client.sh
```

Starting the Server

```bash
./scripts/run_server.sh
```

Starting the Client CLI

```bash
./scripts/run_client.sh
```

### Basic Commands

#### String Operations

- `SET key value` - Set key to hold string value
- `GET key` - Get the value of key
- `DEL key` - Delete one or more keys
- `EXISTS key` - Check if key(s) exist
- `ALL` - Get all key-value pairs
- `CLEAR` - Remove all keys from store and disk
- `SAVE` - Save the current state to disk

#### Other Commands
- `PING` - Test connection
