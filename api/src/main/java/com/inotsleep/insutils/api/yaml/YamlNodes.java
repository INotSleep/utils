package com.inotsleep.insutils.api.yaml;

import java.util.List;

public final class YamlNodes {
    private YamlNodes() {
    }

    public static YamlScalarNode scalar(String value) {
        return scalar(value, YamlScalarType.PLAIN);
    }

    public static YamlScalarNode scalar(String value, YamlScalarType scalarType) {
        return new SimpleYamlScalarNode(value, scalarType);
    }

    public static YamlSequenceNode sequence() {
        return new SimpleYamlSequenceNode();
    }

    public static YamlSequenceNode sequence(List<YamlNode> nodes) {
        return new SimpleYamlSequenceNode(nodes);
    }

    public static YamlMappingNode mapping() {
        return new SimpleYamlMappingNode();
    }

    public static YamlMappingNode mapping(List<YamlNodeTyple> nodes) {
        return new SimpleYamlMappingNode(nodes);
    }

    public static YamlNodeTyple entry(String key, YamlNode value) {
        return new SimpleYamlNodeTyple(scalar(key, YamlScalarType.PLAIN), value);
    }

    public static YamlNodeTyple entry(YamlScalarNode key, YamlNode value) {
        return new SimpleYamlNodeTyple(key, value);
    }
}
