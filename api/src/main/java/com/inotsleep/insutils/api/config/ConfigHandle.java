package com.inotsleep.insutils.api.config;

import com.inotsleep.insutils.api.config.codecs.Codec;
import com.inotsleep.insutils.api.service.ServiceManager;
import com.inotsleep.insutils.spi.config.UnsafeConfig;

public interface ConfigHandle {
    static ConfigHandle getInstance() {
        return ServiceManager.require(ConfigHandle.class);
    }

    void saveConfig(UnsafeConfig config);
    void reloadConfig(UnsafeConfig config);

    <T> void registerCodec(Codec<T> codec);
}
