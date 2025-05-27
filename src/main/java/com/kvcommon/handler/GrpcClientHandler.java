package com.kvcommon.handler;

import com.kvclustermanager.cluster.GrpcClusterNodeClient;
import com.kvcommon.protocol.GrpcCommandParser;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles client connections using gRPC for communication with cluster nodes.
 */
public class GrpcClientHandler implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(GrpcClientHandler.class.getName());
    private static final String GRPC_COMMAND = "GRPC";

    private final Socket clientSocket;
    private final String clientAddress;
    private final GrpcClusterNodeClient grpcClient;
    private final GrpcCommandParser grpcCommandParser;

    public GrpcClientHandler(Socket socket, String grpcHost, int grpcPort) {
        this.clientSocket = socket;
        this.clientAddress = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
        this.grpcClient = new GrpcClusterNodeClient(grpcHost, grpcPort);
        this.grpcCommandParser = new GrpcCommandParser(grpcClient);
    }

    @Override
    public void run() {
        LOGGER.info("GRPC Client connected from " + clientAddress);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {

            processClientCommands(reader, writer);

        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error handling GRPC client " + clientAddress, e);
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Error closing client socket", e);
            }
            grpcClient.shutdown();
            LOGGER.info("Connection closed with GRPC client " + clientAddress);
        }
    }

    private void processClientCommands(BufferedReader reader, BufferedWriter writer) throws IOException {
        String command;
        while ((command = reader.readLine()) != null) {
            try {
                String[] parts = command.split(" ");
                if (parts.length == 0 || parts[0].isEmpty()) {
                    sendErrorResponse(writer, "Empty command received");
                    continue;
                }

                parts[0] = parts[0].toUpperCase();
                LOGGER.fine("Command received: " + command);

                if (parts[0].equals(GRPC_COMMAND)) {
                    String response = grpcCommandParser.process(parts);
                    writer.write(response + "\n");
                    writer.flush();
                } else {
                    sendErrorResponse(writer, "Only GRPC commands are supported");
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error processing command", e);
                sendErrorResponse(writer, "Internal server error");
            }
        }
    }

    private void sendErrorResponse(BufferedWriter writer, String message) {
        try {
            writer.write("ERROR: " + message + "\n");
            writer.flush();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to send error response", e);
        }
    }
}