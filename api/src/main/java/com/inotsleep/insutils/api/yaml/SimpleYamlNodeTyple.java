package com.inotsleep.insutils.api.yaml;

public class SimpleYamlNodeTyple implements YamlNodeTyple {
    private YamlScalarNode key;
    private YamlNode value;

    public SimpleYamlNodeTyple(YamlScalarNode key, YamlNode value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public YamlScalarNode getKey() {
        return key;
    }

    @Override
    public void setKey(YamlScalarNode key) {
        this.key = key;
    }

    @Override
    public YamlNode getValue() {
        return value;
    }

    @Override
    public void setValue(YamlNode value) {
        this.value = value;
    }
}
