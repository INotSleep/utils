package me.inotsleep.utils.storage.connection;

import me.inotsleep.utils.storage.StorageSettings;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface BaseConnection {
    void connect() throws SQLException;
    void disconnect() throws SQLException;
    ResultSet executeQuery(String query, Object... params) throws SQLException;
    int executeUpdate(String query, Object... params) throws SQLException;
    boolean isConnected();

    static BaseConnection createConnection(StorageSettings settings) throws SQLException {
        switch (settings.type) {
            case SQLITE:
                return new SQLiteConnection(settings);
            case MYSQL:
                return new MySQLConnection(settings);
            default:
                throw new IllegalArgumentException("Unsupported storage type: " + settings.type);
        }
    }
}