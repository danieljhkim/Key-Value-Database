package com.kvclustermanager.cluster;

import com.kvcommon.network.HttpClientWrapper;

import java.util.Map;

public class HttpClusterNodeClient implements ClusterNodeClient {

    private final String urlBase;
    private final HttpClientWrapper httpClient;

    public HttpClusterNodeClient(String host, int port) {
        this.urlBase = "http://" + host + ":" + port;
        this.httpClient = new HttpClientWrapper();
    }

    @Override
    public boolean sendSet(String key, String value) {
        try {
            Map<String, String> payload = Map.of("key", key, "value", value);
            httpClient.post(urlBase + "/set", payload);
            return true;
        } catch (Exception e) {
            System.err.println("HTTP SET failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String sendGet(String key) {
        try {
            return httpClient.get(urlBase + "/get?key=" + key, String.class);
        } catch (Exception e) {
            System.err.println("HTTP GET failed: " + e.getMessage());
            return null;
        }
    }
}