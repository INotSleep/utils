package com.inotsleep.insutils.api.storage.connection;

import com.inotsleep.insutils.api.storage.StorageSettings;
import com.inotsleep.insutils.api.service.ServiceManager;

import java.io.File;

public interface ConnectionHandler {
    static ConnectionHandler getInstance() {
        return ServiceManager.require(ConnectionHandler.class);
    }

    Connection createConnection(StorageSettings settings, File basePath);
}
