package com.kvclustermanager.cluster;

public interface ClusterNodeClient {
    boolean sendSet(String key, String value);
    String sendGet(String key);
}