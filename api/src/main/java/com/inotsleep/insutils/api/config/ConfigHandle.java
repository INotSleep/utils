package com.inotsleep.insutils.api.config;

import com.inotsleep.insutils.api.config.codecs.Codec;
import com.inotsleep.insutils.api.service.ServiceManager;
import com.inotsleep.insutils.api.yaml.YamlNode;
import com.inotsleep.insutils.spi.config.UnsafeConfig;

import java.lang.reflect.Type;

public interface ConfigHandle {
    static ConfigHandle getInstance() {
        return ServiceManager.require(ConfigHandle.class);
    }

    void saveConfig(UnsafeConfig config);
    void reloadConfig(UnsafeConfig config);

    <T> void registerCodec(Codec<T> codec);

    <T> YamlNode serialize(TypeKey<T> type, T value);

    default <T> YamlNode serialize(Class<T> type, T value) {
        return serialize(TypeKey.of(type), value);
    }

    @SuppressWarnings({
            "rawtypes",
            "unchecked"
    })
    default YamlNode serialize(Type type, Object value) {
        return serialize((TypeKey) TypeKey.of(type), value);
    }

    <T> T deserialize(TypeKey<T> type, YamlNode node);

    default <T> T deserialize(Class<T> type, YamlNode node) {
        return deserialize(TypeKey.of(type), node);
    }

    default Object deserialize(Type type, YamlNode node) {
        return deserialize(TypeKey.of(type), node);
    }
}
