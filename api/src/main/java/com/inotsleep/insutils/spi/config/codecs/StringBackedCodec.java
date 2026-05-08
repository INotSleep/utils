package com.inotsleep.insutils.spi.config.codecs;

import com.inotsleep.insutils.api.config.codecs.Codec;
import com.inotsleep.insutils.api.yaml.YamlNode;
import com.inotsleep.insutils.api.yaml.YamlNodes;
import com.inotsleep.insutils.api.yaml.YamlScalarNode;
import com.inotsleep.insutils.api.yaml.YamlScalarType;

public abstract class StringBackedCodec<T> implements Codec<T> {
    @Override
    public YamlNode serialize(T value) {
        String stringValue = serializeToString(value);
        boolean multiline = stringValue.contains("\n");
        return YamlNodes.scalar(stringValue, multiline ? YamlScalarType.LITERAL : YamlScalarType.DOUBLE_QUOTED);
    }

    @Override
    public T deserialize(YamlNode node) {
        if (node instanceof YamlScalarNode scalarNode) {
            return deserializeFromString(scalarNode.getValue());
        }
        throw new IllegalArgumentException("Unsupported node type " + node.getClass());
    }

    public abstract String serializeToString(T value);
    public abstract T deserializeFromString(String node);
}
