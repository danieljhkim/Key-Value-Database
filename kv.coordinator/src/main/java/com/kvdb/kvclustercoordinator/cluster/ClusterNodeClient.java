package com.kvdb.kvclustercoordinator.cluster;

public interface ClusterNodeClient {
    boolean sendSet(String key, String value);
    String sendGet(String key);
}