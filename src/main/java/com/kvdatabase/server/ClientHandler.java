package com.kvdatabase.server;

import com.kvdatabase.protocol.CommandParser;
import com.kvdatabase.store.KeyValueStore;
import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Handles client connections and processes commands for the key-value database. */
public class ClientHandler implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(ClientHandler.class.getName());
    private static final KeyValueStore STORE = KeyValueStore.getInstance();

    private final Socket clientSocket;
    private final String clientAddress;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.clientAddress = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
    }

    @Override
    public void run() {
        LOGGER.info("Client connected from " + clientAddress);

        try {
            Socket socket = this.clientSocket;
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            processClientCommands(reader, writer);

        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error handling client " + clientAddress, e);
        } finally {
            LOGGER.info("Connection closed with client " + clientAddress);
        }
    }

    private void processClientCommands(BufferedReader reader, BufferedWriter writer)
            throws IOException {
        String command;
        while ((command = reader.readLine()) != null) {
            try {
                String trimmedCommand = command.trim();
                LOGGER.fine("Command received: " + trimmedCommand);
                String response = CommandParser.process(trimmedCommand, STORE);
                writer.write(response + "\n");
                writer.flush();
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
