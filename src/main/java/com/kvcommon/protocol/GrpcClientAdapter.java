package com.kvcommon.protocol;

import com.kvclustermanager.cluster.GrpcClusterNodeClient;

/**
 * Adapter that wraps a GrpcClusterNodeClient and implements CommandExecutor
 */
public class GrpcClientAdapter implements CommandExecutor {
    
    private final GrpcClusterNodeClient grpcClient;
    
    public GrpcClientAdapter(GrpcClusterNodeClient grpcClient) {
        this.grpcClient = grpcClient;
    }
    
    @Override
    public String get(String key) {
        return grpcClient.sendGet(key);
    }
    
    @Override
    public boolean put(String key, String value) {
        return grpcClient.sendSet(key, value);
    }
    
    @Override
    public boolean delete(String key) {
        return false; // TODO: implement
    }
}
