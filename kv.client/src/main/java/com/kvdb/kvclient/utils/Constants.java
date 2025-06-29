package com.kvdb.kvclient.utils;

public class Constants {
    public static final String PROMPT = "> ";
    public static final String END_MARKER = "END_MARKER";
    public static final String WELCOME_MESSAGE = "Welcome to the KV-Store CLI! Type 'HELP' for available commands.";
    public static final String SQL_HELP_MESSAGE = "SQL Available commands: \n" +
            "1. SQL INIT [table] - Initialize a new table.\n" +
            "2. SQL USE [table] - Use an existing table.\n" +
            "3. SQL SET key value - Insert a key-value pair.\n" +
            "4. SQL GET key - Retrieve the value for a given key.\n" +
            "5. SQL DEL key - Delete a key-value pair.\n" +
            "6. SQL HELP - Show this help message.";

    public static final String KV_HELP_MESSAGE = "KV-Store Available commands: \n" +
            "1. KV SET key value - Store a key-value pair.\n" +
            "2. KV GET key - Retrieve the value for a given key.\n" +
            "3. KV DEL key - Remove a key-value pair.\n" +
            "4. KV EXISTS key - Check if a key exists (returns 1 if exists, 0 if not).\n" +
            "5. KV SIZE - Return the number of key-value pairs stored.\n" +
            "6. KV CLEAR - Remove all entries.\n" +
            "8. KV PING - Check connection.\n" +
            "9. KV SHUTDOWN/QUIT/TERMINATE - Close the database connection.\n" +
            "10. KV HELP/INFO - Display this help message.\n";

    public static final String LINE_SEPARATOR = "----------------------------------------";
}
