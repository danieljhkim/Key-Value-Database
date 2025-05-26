# KvDB - Key-Value Database


A Redis-like distributed key-value store implemented in Java.

This project provides a lightweight, in-memory database with disk persistence options, as well as relational-database (postgres) persistence via
simple CLI commands.
It supports both client-server architecture and interactive command-line interfaces for RDB and key-value store operations.

--- 

## CLI Preview

![Lucid Search Engine GUI](assets/img.png)

## Usage

### Prerequisites

- Java 11 or higher
- Maven
- PostgreSQL (optional, for SQL persistence)

### Client-Server Communication

- The server uses a simple TCP socket for communication.
- i.e. `nc localhost 6379` will connect to the server.

### Starting the Server and Client CLI

Make the scripts executable:

```bash
chmod +x scripts/run_server.sh scripts/run_client.sh
```

Open up a terminal & start the Server

```bash
./scripts/run_server.sh
```

Open up another terminal & start the KvClient

```bash
./scripts/run_client.sh
```

### Basic CLI Commands

#### In-Memory Store Operations

- `SET key value` - Set key to hold string value
- `GET key` - Get the value of key
- `DEL key` - Delete one or more keys
- `EXISTS key` - Check if key(s) exist
- `ALL` - Get all key-value pairs
- `TRUNCATE/DROP` - Remove all keys from store and disk
- `SAVE` - Save the current state to disk

#### SQL Commands

- `SQL INIT [table_name]` - Initialize a new table (default if no name given)
- `SQL USE [table_name]` - Switch to an existing table
- `SQL GET [key]` - Retrieve value for a given key
- `SQL SET [key] [value]` - Store a key-value pair
- `SQL DEL [key]` - Remove a key-value pair
- `SQL TRUNCATE/DROP` - Remove all entries from the current table
- `SQL PING` - Check connection to database
- `SQL HELP/INFO` - Display help message

#### Other Commands

- `PING` - Test connection
- `HELP` - Show help message
- `EXIT` - Exit the client

--- 

## Configuration

Configuration is done via `application.properties` file located in the `src/main/resources` directory.


### File-based Persistence

The system supports file-based persistence with options for auto-flushing and custom file types.

```properties
kvdb.persistence.filepath=data/kvstore.dat
kvdb.persistence.filetype=dat
kvdb.persistence.enableAutoFlush=true
kvdb.persistence.autoFlushInterval=2
```

### PostgreSQL Persistence

SQL commands leverage PostgreSQL for persistent storage.

```properties
kvdb.database.default.url=jdbc:postgresql://localhost:5432/kvdb
kvdb.database.default.driver=org.postgresql.Driver
kvdb.database.default.username=yourusername
kvdb.database.default.password=yourpassword
kvdb.database.default.table=kv_store
```

### Server Configuration

The server can be configured to run on a specific host and port.

```properties
kvdb.server.port=6379
kvdb.server.host=localhost
```

--- 

# License

This project is licensed under the MIT License.
