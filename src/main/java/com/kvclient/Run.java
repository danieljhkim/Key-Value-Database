package com.kvclient;

import com.kvclient.cli.CLIClient;

public class Run {
    public static void main(String[] args) {
        CLIClient client = new CLIClient();
        client.connect("localhost", 6379);
        client.runCli();
        client.disconnect();
    }
}
