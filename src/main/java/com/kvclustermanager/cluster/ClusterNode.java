package com.kvclustermanager.cluster;

public class ClusterNode {

    private final String id;
    private final ClusterNodeClient client;
    private final String host;
    private final int port;
    public boolean isGrpc;


    public ClusterNode(String id, String host, int port, boolean useGrpc) {
        this.id = id;
        this.host = host;
        this.port = port;
        this.isGrpc = useGrpc;
        this.client = useGrpc ? new GrpcClusterNodeClient(host, port) : new HttpClusterNodeClient(host, port);
    }

    public String getId() {
        return id;
    }

    public boolean sendSet(String key, String value) {
        return client.sendSet(key, value);
    }

    public String sendGet(String key) {
        return client.sendGet(key);
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }
}