package com.inotsleep.insutils.spi.config;

import org.snakeyaml.engine.v2.nodes.MappingNode;

import java.io.File;
import java.io.InputStream;

public class Config extends UnsafeConfig {
    public Config(File configFile) {
        super(configFile);
    }

    public Config(File baseDir, String fileName) {
        super(baseDir, fileName);
    }

    public Config(InputStream stream) {
        super(stream);
    }

    @Override
    public void beforeDeserialization(MappingNode node) {

    }

    @Override
    public void afterDeserialization(MappingNode node) {

    }

    @Override
    public void beforeSerialization(MappingNode node) {

    }

    @Override
    public void afterSerialization(MappingNode node) {

    }
}
