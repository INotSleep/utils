package com.inotsleep.insutils.api.config;

import org.snakeyaml.engine.v2.nodes.Node;

public interface Codec<T> {
    Node serialize(T value);
    T deserialize(Node node);
    TypeKey<T> key();

    @SuppressWarnings("unchecked")
    default Node serializeAny(Object value) {
        return serialize((T) value);
    }

    default Object deserializeAny(Node node) {
        return deserialize(node);
    }
}
