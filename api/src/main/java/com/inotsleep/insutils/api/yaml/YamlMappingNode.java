package com.inotsleep.insutils.api.yaml;

import java.util.List;

public interface YamlMappingNode extends YamlNode {
    List<YamlNodeTyple> getNodes();
    void setNodes(List<YamlNodeTyple> nodes);
}
