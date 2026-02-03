package com.inotsleep.insutils.api.config;

import com.inotsleep.insutils.api.config.codecs.Codec;
import com.inotsleep.insutils.spi.config.UnsafeConfig;

import java.util.concurrent.atomic.AtomicReference;

public interface ConfigHandle {
    AtomicReference<ConfigHandle> instance = new AtomicReference<>();

    static void setInstance(ConfigHandle instance) {
        if (instance == null) {
            throw new NullPointerException("instance");
        }
        if (!ConfigHandle.instance.compareAndSet(null, instance)) {
            throw new IllegalStateException("ConfigHandle instance already set");
        }
    }

    static ConfigHandle getInstance() {
        return ConfigHandle.instance.get();
    }

    void saveConfig(UnsafeConfig config);
    void reloadConfig(UnsafeConfig config);

    <T> void registerCodec(Codec<T> codec);
}
