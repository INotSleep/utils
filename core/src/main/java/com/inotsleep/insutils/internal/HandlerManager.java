package com.inotsleep.insutils.internal;

import com.inotsleep.insutils.api.config.ConfigHandle;
import com.inotsleep.insutils.api.logging.LoggingManager;
import com.inotsleep.insutils.api.storage.connection.ConnectionHandler;
import com.inotsleep.insutils.internal.config.ConfigHandleImpl;
import com.inotsleep.insutils.internal.logging.LoggingManagerImpl;
import com.inotsleep.insutils.internal.storage.connection.ConnectionHandlerImpl;

public class HandlerManager {
    public static void loadHandlers() {
        ConfigHandle.setInstance(new ConfigHandleImpl());
        LoggingManager.setInstance(new LoggingManagerImpl());
        ConnectionHandler.setInstance(new ConnectionHandlerImpl());
    }
}
