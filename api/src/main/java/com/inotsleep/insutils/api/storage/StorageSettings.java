package com.inotsleep.insutils.api.storage;

import com.inotsleep.insutils.api.config.Comment;
import com.inotsleep.insutils.api.config.Path;
import com.inotsleep.insutils.spi.config.SerializableObject;

public class StorageSettings extends SerializableObject {

    @Comment("Database settings.")
    @Comment(" ")
    @Comment("Available types: SQLITE, MYSQL")
    @Path("storageType")
    public StorageType type = StorageType.SQLITE;

    @Comment("File name for SQLite database")
    @Path("sqliteFile")
    public String sqliteFileName = "database.db";

    @Comment("Host of MySQL database")
    @Path("host")
    public String host = "localhost";

    @Comment("Port of MySQL database")
    @Path("port")
    public String port = "3306";

    @Comment("Database name")
    @Path("database")
    public String database = "my_database";

    @Comment("Password for MySQL database")
    @Path("password")
    public String password = "veryStrongPassword";

    @Comment("Username for MySQL database")
    @Path("username")
    public String username = "username";

    @Comment("Connection options")
    @Path("options")
    public String options = "autoReconnect=true&useSSL=false;";

    @Comment("Table prefix")
    @Path("tablePrefix")
    public String tablePrefix = "";

    public enum StorageType {
        SQLITE, MYSQL;

        public static StorageType fromString(String string) {
            if (string == null) return null;
            return StorageType.valueOf(string.toUpperCase());
        }
    }
}
