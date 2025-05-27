package com.kvcommon.handler;

import java.net.Socket;

/**
 * Default implementation of ClientHandlerFactory that creates standard ClientHandler instances.
 */
public class DefaultClientHandlerFactory implements ClientHandlerFactory {
    
    @Override
    public Runnable createHandler(Socket clientSocket) {
        return new ClientHandler(clientSocket);
    }
}
