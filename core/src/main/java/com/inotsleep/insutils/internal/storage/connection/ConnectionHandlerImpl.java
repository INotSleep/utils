package com.inotsleep.insutils.internal.storage.connection;

import com.inotsleep.insutils.api.storage.connection.Connection;
import com.inotsleep.insutils.api.storage.connection.ConnectionHandler;
import com.inotsleep.insutils.api.storage.StorageSettings;

import java.io.File;

public class ConnectionHandlerImpl implements ConnectionHandler {
    public Connection createConnection(StorageSettings settings, File basePath) {
        return switch (settings.type) {
            case SQLITE -> new SQLiteConnection(settings, basePath);
            case MYSQL -> new MySQLConnection(settings);
            default -> throw new IllegalArgumentException("Unsupported storage type: " + settings.type);
        };
    }
}
