package com.inotsleep.utils.storage.connection;

import com.inotsleep.utils.storage.StorageSettings;

import java.io.File;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface Connection {
    void connect() throws SQLException;
    void disconnect() throws SQLException;
    ResultSet executeQuery(String query, Object... params) throws SQLException;
    int executeUpdate(String query, Object... params) throws SQLException;
    DatabaseMetaData getMetaData() throws SQLException;

    boolean isConnected();

    static Connection createConnection(StorageSettings settings, File basePath) throws SQLException {
        return switch (settings.type) {
            case SQLITE -> new SQLiteConnection(settings, basePath);
            case MYSQL -> new MySQLConnection(settings);
            default -> throw new IllegalArgumentException("Unsupported storage type: " + settings.type);
        };
    }
}