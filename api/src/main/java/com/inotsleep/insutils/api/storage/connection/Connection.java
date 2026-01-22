package com.inotsleep.insutils.api.storage.connection;

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
}