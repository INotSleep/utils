package com.inotsleep.insutils.api.config;

import com.inotsleep.insutils.api.config.codecs.Codec;
import com.inotsleep.insutils.spi.config.UnsafeConfig;

import java.util.ServiceLoader;

public interface ConfigHandle {
    static ConfigHandle getInstance() {
        return ServiceLoader
                .load(ConfigHandle.class, ConfigHandle.class.getClassLoader())
                .findFirst()
                .orElseThrow();
    }

    void saveConfig(UnsafeConfig config);
    void reloadConfig(UnsafeConfig config);

    <T> void registerCodec(Codec<T> codec);
}
