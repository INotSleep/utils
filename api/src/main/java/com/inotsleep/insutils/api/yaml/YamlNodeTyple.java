package com.inotsleep.insutils.api.yaml;

public interface YamlNodeTyple {
    YamlScalarNode getKey();
    void setKey(YamlScalarNode key);

    YamlNode getValue();
    void setValue(YamlNode value);
}
