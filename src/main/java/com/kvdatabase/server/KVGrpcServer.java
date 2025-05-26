package com.kvdatabase.server;

import com.kvcommon.config.SystemConfig;
import com.kvdatabase.repository.KVStoreRepository;
import com.kvdatabase.service.KVServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

public class KVGrpcServer {

    private static final SystemConfig config = SystemConfig.getInstance();
    private final int port;
    private final Server server;

    public KVGrpcServer(int port) {
        this.port = port;
        this.server = ServerBuilder.forPort(port)
                .addService(new KVServiceImpl(new KVStoreRepository()))
                .build();
    }

    public KVGrpcServer() {
        this(Integer.parseInt(config.getProperty("grpc.port", "9001")));
    }

    public void start() throws Exception {
        server.start();
        System.out.println("gRPC KV server started on port " + port);
        server.awaitTermination();
    }

}