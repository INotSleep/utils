package com.inotsleep.insutils.api.config;

import com.inotsleep.insutils.spi.config.UnsafeConfig;
import com.inotsleep.insutils.spi.config.UnsafeSerializableObject;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

public interface ConfigHandle {
    AtomicReference<ConfigHandle> instance = new AtomicReference<>();

    static void setInstance(ConfigHandle instance) {
        ConfigHandle.instance.set(instance);
    }

    static ConfigHandle getInstance() {
        return ConfigHandle.instance.get();
    }

    void saveConfig(UnsafeConfig config);
    void reloadConfig(UnsafeConfig config);
}
