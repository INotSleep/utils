package com.inotsleep.insutils.api.yaml;

public interface YamlScalarNode extends YamlNode {
    String getValue();
    void setValue(String value);

    YamlScalarType getScalarType();
    void setScalarType(YamlScalarType scalarType);
}
