package com.inotsleep.insutils.api.yaml;

import java.util.List;

public interface YamlSequenceNode extends YamlNode {
    List<YamlNode> getNodes();
    void setNodes(List<YamlNode> nodes);
}
