package com.kvclustermanager.cluster;

import com.kvcommon.proto.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GrpcClusterNodeClient implements ClusterNodeClient {

    private final ManagedChannel channel;
    private final KVServiceGrpc.KVServiceBlockingStub stub;

    public GrpcClusterNodeClient(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        this.stub = KVServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public String sendGet(String key) {
        KeyRequest request = KeyRequest.newBuilder().setKey(key).build();
        ValueResponse response = stub.get(request);
        return response.getValue();
    }

    @Override
    public boolean sendSet(String key, String value) {
        KeyValueRequest request = KeyValueRequest.newBuilder()
                .setKey(key)
                .setValue(value)
                .build();
        SetResponse response = stub.set(request);
        return response.getSuccess();
    }

    public void shutdown() {
        channel.shutdown();
    }
}