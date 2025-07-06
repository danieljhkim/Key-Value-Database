package com.kvdb.kvclustercoordinator.cluster;

public class ClusterNode {

    private final String id;
    private final ClusterNodeClient client;
    private final String host;
    private final int port;
    public boolean isGrpc;
    private boolean isRunning = false;

    public ClusterNode(String id, String host, int port, boolean useGrpc, int grpcPort) {
        this.id = id;
        this.host = host;
        this.port = port;
        this.isGrpc = useGrpc;
        this.client = useGrpc ? new GrpcClusterNodeClient(host, grpcPort) : new HttpClusterNodeClient(host, port);
    }

    public ClusterNode(String id, String host, int port) {
        this(id, host, port, false, 0);
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

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public ClusterNodeClient getClient() {
        return client;
    }

    public void shutdown() {
        if (client != null) {
            client.shutdown();
        }
    }
}