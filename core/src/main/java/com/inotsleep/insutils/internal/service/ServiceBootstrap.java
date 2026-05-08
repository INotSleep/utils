package com.inotsleep.insutils.internal.service;

import com.inotsleep.insutils.api.config.ConfigHandle;
import com.inotsleep.insutils.api.logging.LoggingManager;
import com.inotsleep.insutils.api.service.ServiceManager;
import com.inotsleep.insutils.api.storage.connection.ConnectionHandler;
import com.inotsleep.insutils.internal.config.ConfigHandleImpl;
import com.inotsleep.insutils.internal.logging.LoggingManagerImpl;
import com.inotsleep.insutils.internal.storage.connection.ConnectionHandlerImpl;

public final class ServiceBootstrap {
    private ServiceBootstrap() {
    }

    public static void registerCommonServices() {
        registerIfAbsent(LoggingManager.class, new LoggingManagerImpl());
        registerIfAbsent(ConfigHandle.class, new ConfigHandleImpl());
        registerIfAbsent(ConnectionHandler.class, new ConnectionHandlerImpl());
    }

    private static <T> void registerIfAbsent(Class<T> type, T implementation) {
        if (ServiceManager.get(type) != null) {
            return;
        }
        ServiceManager.register(type, implementation);
    }
}
