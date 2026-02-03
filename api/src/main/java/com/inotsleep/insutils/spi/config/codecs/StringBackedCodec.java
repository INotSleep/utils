package com.inotsleep.insutils.spi.config.codecs;

import com.inotsleep.insutils.api.config.codecs.Codec;
import org.snakeyaml.engine.v2.common.ScalarStyle;
import org.snakeyaml.engine.v2.nodes.Node;
import org.snakeyaml.engine.v2.nodes.ScalarNode;
import org.snakeyaml.engine.v2.nodes.Tag;

public abstract class StringBackedCodec<T> implements Codec<T> {
    @Override
    public Node serialize(T value) {
        String stringValue = serializeToString(value);
        boolean multiline = stringValue.contains("\n");
        return new ScalarNode(Tag.STR, stringValue, multiline ? ScalarStyle.LITERAL : ScalarStyle.DOUBLE_QUOTED);
    }

    @Override
    public T deserialize(Node node) {
        if (node instanceof ScalarNode scalarNode) {
            return deserializeFromString(scalarNode.getValue());
        }
        throw new IllegalArgumentException("Unsupported node type " + node.getClass());
    }

    public abstract String serializeToString(T value);
    public abstract T deserializeFromString(String node);
}
