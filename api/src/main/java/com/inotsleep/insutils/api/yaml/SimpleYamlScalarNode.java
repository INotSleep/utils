package com.inotsleep.insutils.api.yaml;

public class SimpleYamlScalarNode extends AbstractYamlNode implements YamlScalarNode {
    private String value;
    private YamlScalarType scalarType;

    public SimpleYamlScalarNode() {
        this("", YamlScalarType.PLAIN);
    }

    public SimpleYamlScalarNode(String value, YamlScalarType scalarType) {
        super(YamlNodeType.SCALAR);
        this.value = value;
        this.scalarType = scalarType == null ? YamlScalarType.PLAIN : scalarType;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public YamlScalarType getScalarType() {
        return scalarType;
    }

    @Override
    public void setScalarType(YamlScalarType scalarType) {
        this.scalarType = scalarType == null ? YamlScalarType.PLAIN : scalarType;
    }
}
