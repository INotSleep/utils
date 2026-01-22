package com.inotsleep.insutils.internal.storage.connection;

import com.inotsleep.insutils.api.storage.connection.Connection;
import com.inotsleep.insutils.api.storage.StorageSettings;

import java.sql.*;

class MySQLConnection implements Connection {
    private java.sql.Connection connection;
    private final String url;
    private final String user;
    private final String password;

    public MySQLConnection(StorageSettings settings) {
        this.url = "jdbc:mysql://" + settings.host + ":" + settings.port + "/" + settings.database + "?" + settings.options;
        this.user = settings.username;
        this.password = settings.password;
    }

    @Override
    public void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url, user, password);
        }
    }

    @Override
    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Override
    public ResultSet executeQuery(String query, Object... params) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(query);
        setParameters(stmt, params);
        return stmt.executeQuery();
    }

    @Override
    public int executeUpdate(String query, Object... params) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(query);
        setParameters(stmt, params);
        return stmt.executeUpdate();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return connection.getMetaData();
    }

    @Override
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    private void setParameters(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }
}