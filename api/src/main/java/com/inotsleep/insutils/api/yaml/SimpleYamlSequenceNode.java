package com.inotsleep.insutils.api.yaml;

import java.util.ArrayList;
import java.util.List;

public class SimpleYamlSequenceNode extends AbstractYamlNode implements YamlSequenceNode {
    private List<YamlNode> nodes;

    public SimpleYamlSequenceNode() {
        this(new ArrayList<>());
    }

    public SimpleYamlSequenceNode(List<YamlNode> nodes) {
        super(YamlNodeType.SEQUENCE);
        this.nodes = safeCopy(nodes);
    }

    @Override
    public List<YamlNode> getNodes() {
        return nodes;
    }

    @Override
    public void setNodes(List<YamlNode> nodes) {
        this.nodes = safeCopy(nodes);
    }
}
