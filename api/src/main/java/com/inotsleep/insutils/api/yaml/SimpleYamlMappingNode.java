package com.inotsleep.insutils.api.yaml;

import java.util.ArrayList;
import java.util.List;

public class SimpleYamlMappingNode extends AbstractYamlNode implements YamlMappingNode {
    private List<YamlNodeTyple> nodes;

    public SimpleYamlMappingNode() {
        this(new ArrayList<>());
    }

    public SimpleYamlMappingNode(List<YamlNodeTyple> nodes) {
        super(YamlNodeType.MAPPING);
        this.nodes = safeCopy(nodes);
    }

    @Override
    public List<YamlNodeTyple> getNodes() {
        return nodes;
    }

    @Override
    public void setNodes(List<YamlNodeTyple> nodes) {
        this.nodes = safeCopy(nodes);
    }
}
