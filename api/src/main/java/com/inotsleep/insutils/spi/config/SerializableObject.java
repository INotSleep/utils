package com.inotsleep.insutils.spi.config;

import com.inotsleep.insutils.api.yaml.YamlMappingNode;

public class SerializableObject extends UnsafeSerializableObject {
    @Override
    public void beforeDeserialization(YamlMappingNode node) {
        beforeDeserialization();
    }

    @Override
    public void afterDeserialization(YamlMappingNode node) {
        afterDeserialization();
    }

    @Override
    public void beforeSerialization(YamlMappingNode node) {
        beforeSerialization();
    }

    @Override
    public void afterSerialization(YamlMappingNode node) {
        afterSerialization();
    }

    public void beforeDeserialization() {}
    public void afterDeserialization() {}
    public void beforeSerialization() {}
    public void afterSerialization() {}
}
