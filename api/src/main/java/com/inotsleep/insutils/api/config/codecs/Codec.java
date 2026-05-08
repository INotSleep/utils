package com.inotsleep.insutils.api.config.codecs;

import com.inotsleep.insutils.api.config.TypeKey;
import com.inotsleep.insutils.api.yaml.YamlNode;

public interface Codec<T> {
    YamlNode serialize(T value);
    T deserialize(YamlNode node);
    TypeKey<T> key();

    @SuppressWarnings("unchecked")
    default YamlNode serializeAny(Object value) {
        return serialize((T) value);
    }

    default Object deserializeAny(YamlNode node) {
        return deserialize(node);
    }
}
