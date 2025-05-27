package com.kvcommon.protocol;

import com.kvclustermanager.cluster.GrpcClusterNodeClient;

public class GrpcCommandParser extends CommandParser {

    private static final String HELP_TEXT =
            """
            GRPC Command Parser Usage:
            GRPC GET [key] - Retrieve value for a given key using gRPC
            GRPC SET [key] [value] - Store a key-value pair using gRPC
            GRPC HELP/INFO - Display this help message""";

    public GrpcCommandParser(GrpcClusterNodeClient grpcClient) {
        super(new GrpcClientAdapter(grpcClient));
    }
    
    public GrpcCommandParser(CommandExecutor executor) {
        super(executor);
    }

    @Override
    String executeCommand(String[] parts) {
        if (parts.length < 2) {
            return "ERR: Invalid GRPC command format";
        }

        String cmd = parts[1].trim().toUpperCase();
        return switch (cmd) {
            case "HELP", "INFO" -> HELP_TEXT;
            case "GET" -> handleGet(parts);
            case "SET" -> handleSet(parts);
            default -> "ERR: Unknown GRPC command";
        };
    }

    private String handleGet(String[] parts) {
        if (parts.length != 3) {
            return "ERR: Usage: GRPC GET [key]";
        }
        String key = parts[2];
        return executor.get(key);
    }

    private String handleSet(String[] parts) {
        if (parts.length != 4) {
            return "ERR: Usage: GRPC SET [key] [value]";
        }
        String key = parts[2];
        String value = parts[3];
        boolean success = executor.put(key, value);
        return success ? "OK" : "ERR: Failed to set value";
    }

    @Override
    public String getHelpText() {
        return HELP_TEXT;
    }
}
