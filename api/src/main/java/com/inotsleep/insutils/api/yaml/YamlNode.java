package com.inotsleep.insutils.api.yaml;

import java.util.List;

public interface YamlNode {
    YamlNodeType getType();

    List<YamlCommentLine> getBlockComments();
    List<YamlCommentLine> getEndComments();
    List<YamlCommentLine> getInLineComments();

    void setBlockComments(List<YamlCommentLine> blockComments);
    void setEndComments(List<YamlCommentLine> endComments);
    void setInLineComments(List<YamlCommentLine> inLineComments);
}
