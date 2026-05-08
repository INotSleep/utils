package com.inotsleep.insutils.api.storage.connection;

import com.inotsleep.insutils.api.storage.StorageSettings;

import java.io.File;
import java.util.ServiceLoader;

public interface ConnectionHandler {
    static ConnectionHandler getInstance() {
        return ServiceLoader
                .load(ConnectionHandler.class, ConnectionHandler.class.getClassLoader())
                .findFirst()
                .orElseThrow();
    }

    Connection createConnection(StorageSettings settings, File basePath);
}
