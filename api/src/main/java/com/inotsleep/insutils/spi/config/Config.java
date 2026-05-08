package com.inotsleep.insutils.spi.config;

import com.inotsleep.insutils.api.yaml.YamlMappingNode;

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
    public void beforeDeserialization(YamlMappingNode node) {

    }

    @Override
    public void afterDeserialization(YamlMappingNode node) {

    }

    @Override
    public void beforeSerialization(YamlMappingNode node) {

    }

    @Override
    public void afterSerialization(YamlMappingNode node) {

    }
}
