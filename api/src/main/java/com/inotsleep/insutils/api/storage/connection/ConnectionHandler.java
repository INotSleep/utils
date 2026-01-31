package com.inotsleep.insutils.api.storage.connection;

import com.inotsleep.insutils.api.storage.StorageSettings;

import java.io.File;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

public interface ConnectionHandler {
    AtomicReference<ConnectionHandler> instance = new AtomicReference<>();

    static ConnectionHandler getInstance() {
        return instance.get();
    }

    static void setInstance(ConnectionHandler instance) {
        if (instance == null) {
            throw new NullPointerException("instance");
        }
        if (!ConnectionHandler.instance.compareAndSet(null, instance)) {
            throw new IllegalStateException("ConnectionHandler instance already set");
        }
    }


    Connection createConnection(StorageSettings settings, File basePath);
}
