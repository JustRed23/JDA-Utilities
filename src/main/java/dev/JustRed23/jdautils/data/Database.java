package dev.JustRed23.jdautils.data;

class Database {

    static final String TABLE_CREATION_STRING = "CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, setting VARCHAR(255) UNIQUE NOT NULL, value VARCHAR(255) NOT NULL)";

    //TODO: add some caching to reduce the amount of database calls
}
