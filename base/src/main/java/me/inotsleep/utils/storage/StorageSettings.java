package me.inotsleep.utils.storage;

import me.inotsleep.utils.config.Comment;
import me.inotsleep.utils.config.Path;
import me.inotsleep.utils.config.SerializableObject;

public class StorageSettings extends SerializableObject {

    @Comment("Database settings.")
    @Comment(" ")
    @Comment("Available types: SQLITE, MYSQL")
    @Path("storageType")
    public StorageType type;

    @Comment("File name for SQLite database")
    @Path("sqliteFile")
    public String sqliteFileName;

    @Comment("Host of MySQL database")
    @Path("host")
    public String host;

    @Comment("Port of MySQL database")
    @Path("port")
    public String port;

    @Comment("Database name")
    @Path("database")
    public String database;

    @Comment("Password for MySQL database")
    @Path("password")
    public String password;

    @Comment("Username for MySQL database")
    @Path("username")
    public String username;

    @Comment("Connection options")
    @Path("password")
    public String options;

    @Comment("Table prefix")
    @Path("tablePrefix")
    public String tablePrefix;

    public enum StorageType {
        SQLITE, MYSQL;

        public static StorageType fromString(String string) {
            if (string == null) return null;
            return StorageType.valueOf(string.toUpperCase());
        }
    }
}
