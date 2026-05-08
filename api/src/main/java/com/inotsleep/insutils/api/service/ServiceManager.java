package com.inotsleep.insutils.api.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ServiceManager {
    private static final Map<Class<?>, Object> SERVICES = new ConcurrentHashMap<>();

    private ServiceManager() {
    }

    public static <T> void register(Class<T> type, T implementation) {
        if (type == null) {
            throw new NullPointerException("type");
        }
        if (implementation == null) {
            throw new NullPointerException("implementation");
        }
        if (!type.isInstance(implementation)) {
            throw new IllegalArgumentException("Implementation " + implementation.getClass().getName() + " is not assignable to " + type.getName());
        }

        Object previous = SERVICES.putIfAbsent(type, implementation);
        if (previous != null) {
            throw new IllegalStateException("Service already registered: " + type.getName());
        }
    }

    public static <T> void replace(Class<T> type, T implementation) {
        if (type == null) {
            throw new NullPointerException("type");
        }
        if (implementation == null) {
            throw new NullPointerException("implementation");
        }
        if (!type.isInstance(implementation)) {
            throw new IllegalArgumentException("Implementation " + implementation.getClass().getName() + " is not assignable to " + type.getName());
        }

        SERVICES.put(type, implementation);
    }

    public static <T> T get(Class<T> type) {
        if (type == null) {
            throw new NullPointerException("type");
        }

        Object service = SERVICES.get(type);
        if (service == null) {
            return null;
        }
        return type.cast(service);
    }

    public static <T> T require(Class<T> type) {
        T service = get(type);
        if (service == null) {
            throw new IllegalStateException("Service is not registered: " + type.getName());
        }
        return service;
    }
}
