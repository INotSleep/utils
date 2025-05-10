package me.inotsleep.utils.storage.connection;

import me.inotsleep.utils.storage.StorageSettings;

import java.io.File;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface BaseConnection {
    void connect() throws SQLException;
    void disconnect() throws SQLException;
    ResultSet executeQuery(String query, Object... params) throws SQLException;
    int executeUpdate(String query, Object... params) throws SQLException;
    DatabaseMetaData getMetaData() throws SQLException;

    boolean isConnected();

    static BaseConnection createConnection(StorageSettings settings, File basePath) throws SQLException {
        switch (settings.type) {
            case SQLITE:
                return new SQLiteConnection(settings, basePath);
            case MYSQL:
                return new MySQLConnection(settings);
            default:
                throw new IllegalArgumentException("Unsupported storage type: " + settings.type);
        }
    }
}