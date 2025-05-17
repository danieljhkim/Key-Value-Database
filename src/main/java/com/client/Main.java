package com.client;

import com.client.cli.CLIClient;

public class Main {
    public static void main(String[] args) {
        CLIClient client = new CLIClient();
        client.connect("localhost", 6379);
        client.runCli();
        client.disconnect();
    }
}
