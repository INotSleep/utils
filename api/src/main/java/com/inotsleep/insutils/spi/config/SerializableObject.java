package com.inotsleep.insutils.spi.config;

import org.snakeyaml.engine.v2.nodes.MappingNode;

public class SerializableObject extends UnsafeSerializableObject {
    @Override
    public void beforeDeserialization(MappingNode node) {
        beforeDeserialization();
    }

    @Override
    public void afterDeserialization(MappingNode node) {
        afterDeserialization();
    }

    @Override
    public void beforeSerialization(MappingNode node) {
        beforeSerialization();
    }

    @Override
    public void afterSerialization(MappingNode node) {
        afterSerialization();
    }

    public void beforeDeserialization() {}
    public void afterDeserialization() {}
    public void beforeSerialization() {}
    public void afterSerialization() {}
}
