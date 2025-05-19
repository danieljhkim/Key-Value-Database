package com.kvdatabase;


import com.kvdatabase.server.KVServer;

public class Application {
    public static void main(String[] args) {
        new KVServer(6379).start();
    }
}