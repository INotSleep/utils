package com.inotsleep.insutils.api.yaml;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractYamlNode implements YamlNode {
    private final YamlNodeType type;
    private List<YamlCommentLine> blockComments = new ArrayList<>();
    private List<YamlCommentLine> endComments = new ArrayList<>();
    private List<YamlCommentLine> inLineComments = new ArrayList<>();

    protected AbstractYamlNode(YamlNodeType type) {
        this.type = type;
    }

    @Override
    public YamlNodeType getType() {
        return type;
    }

    @Override
    public List<YamlCommentLine> getBlockComments() {
        return blockComments;
    }

    @Override
    public List<YamlCommentLine> getEndComments() {
        return endComments;
    }

    @Override
    public List<YamlCommentLine> getInLineComments() {
        return inLineComments;
    }

    @Override
    public void setBlockComments(List<YamlCommentLine> blockComments) {
        this.blockComments = safeCopy(blockComments);
    }

    @Override
    public void setEndComments(List<YamlCommentLine> endComments) {
        this.endComments = safeCopy(endComments);
    }

    @Override
    public void setInLineComments(List<YamlCommentLine> inLineComments) {
        this.inLineComments = safeCopy(inLineComments);
    }

    protected static <T> List<T> safeCopy(List<T> source) {
        return source == null ? new ArrayList<>() : new ArrayList<>(source);
    }
}
