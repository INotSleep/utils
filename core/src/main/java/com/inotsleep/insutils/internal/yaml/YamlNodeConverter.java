package com.inotsleep.insutils.internal.yaml;

import com.inotsleep.insutils.api.config.CommentType;
import com.inotsleep.insutils.api.yaml.*;
import org.snakeyaml.engine.v2.comments.CommentLine;
import org.snakeyaml.engine.v2.common.FlowStyle;
import org.snakeyaml.engine.v2.common.ScalarStyle;
import org.snakeyaml.engine.v2.nodes.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class YamlNodeConverter {
    private YamlNodeConverter() {
    }

    public static YamlNode toApiNode(Node node) {
        if (node == null) {
            return null;
        }

        YamlNode apiNode = switch (node.getNodeType()) {
            case SCALAR -> toApiScalarNode((ScalarNode) node);
            case SEQUENCE -> toApiSequenceNode((SequenceNode) node);
            case MAPPING -> toApiMappingNode((MappingNode) node);
            default -> throw new IllegalArgumentException("Unsupported SnakeYAML node type: " + node.getNodeType());
        };

        copyCommentsToApi(node, apiNode);
        return apiNode;
    }

    public static YamlMappingNode toApiMappingNode(MappingNode node) {
        return toApiMappingNodeInternal(node);
    }

    public static Node toSnakeNode(YamlNode node) {
        if (node == null) {
            return null;
        }

        Node snakeNode = switch (node.getType()) {
            case SCALAR -> toSnakeScalarNode((YamlScalarNode) node);
            case SEQUENCE -> toSnakeSequenceNode((YamlSequenceNode) node);
            case MAPPING -> toSnakeMappingNode((YamlMappingNode) node);
        };

        copyCommentsToSnake(node, snakeNode);
        return snakeNode;
    }

    public static MappingNode toSnakeMappingNode(YamlMappingNode node) {
        return toSnakeMappingNodeInternal(node);
    }

    private static YamlScalarNode toApiScalarNode(ScalarNode node) {
        return new SimpleYamlScalarNode(node.getValue(), fromSnakeScalarStyle(node.getScalarStyle()));
    }

    private static YamlSequenceNode toApiSequenceNode(SequenceNode node) {
        List<YamlNode> apiNodes = new ArrayList<>();
        for (Node child : node.getValue()) {
            apiNodes.add(toApiNode(child));
        }
        return new SimpleYamlSequenceNode(apiNodes);
    }

    private static YamlMappingNode toApiMappingNodeInternal(MappingNode node) {
        List<YamlNodeTyple> tuples = new ArrayList<>();
        for (NodeTuple tuple : node.getValue()) {
            if (!(tuple.getKeyNode() instanceof ScalarNode keyNode)) {
                throw new IllegalArgumentException("Only scalar mapping keys are supported in api.yaml wrapper");
            }

            YamlScalarNode apiKey = toApiScalarNode(keyNode);
            YamlNode apiValue = toApiNode(tuple.getValueNode());
            tuples.add(new SimpleYamlNodeTyple(apiKey, apiValue));
        }
        return new SimpleYamlMappingNode(tuples);
    }

    private static ScalarNode toSnakeScalarNode(YamlScalarNode node) {
        String value = node.getValue() == null ? "" : node.getValue();
        return new ScalarNode(Tag.STR, value, toSnakeScalarStyle(node.getScalarType()));
    }

    private static SequenceNode toSnakeSequenceNode(YamlSequenceNode node) {
        List<Node> snakeNodes = new ArrayList<>();
        List<YamlNode> children = node.getNodes();
        if (children != null) {
            for (YamlNode child : children) {
                if (child != null) {
                    snakeNodes.add(toSnakeNode(child));
                }
            }
        }
        return new SequenceNode(Tag.SEQ, snakeNodes, FlowStyle.AUTO);
    }

    private static MappingNode toSnakeMappingNodeInternal(YamlMappingNode node) {
        List<NodeTuple> tuples = new ArrayList<>();
        List<YamlNodeTyple> sourceTuples = node.getNodes();
        if (sourceTuples != null) {
            for (YamlNodeTyple tuple : sourceTuples) {
                if (tuple == null || tuple.getKey() == null || tuple.getValue() == null) {
                    continue;
                }

                ScalarNode keyNode = toSnakeScalarNode(tuple.getKey());
                Node valueNode = toSnakeNode(tuple.getValue());
                tuples.add(new NodeTuple(keyNode, valueNode));
            }
        }
        return new MappingNode(Tag.MAP, tuples, FlowStyle.AUTO);
    }

    private static ScalarStyle toSnakeScalarStyle(YamlScalarType scalarType) {
        if (scalarType == null) {
            return ScalarStyle.PLAIN;
        }
        return switch (scalarType) {
            case DOUBLE_QUOTED -> ScalarStyle.DOUBLE_QUOTED;
            case SINGLE_QUOTED -> ScalarStyle.SINGLE_QUOTED;
            case LITERAL -> ScalarStyle.LITERAL;
            case FOLDED -> ScalarStyle.FOLDED;
            case JSON_SCALAR_STYLE -> ScalarStyle.JSON_SCALAR_STYLE;
            case PLAIN -> ScalarStyle.PLAIN;
        };
    }

    private static YamlScalarType fromSnakeScalarStyle(ScalarStyle scalarStyle) {
        return switch (scalarStyle) {
            case DOUBLE_QUOTED -> YamlScalarType.DOUBLE_QUOTED;
            case SINGLE_QUOTED -> YamlScalarType.SINGLE_QUOTED;
            case LITERAL -> YamlScalarType.LITERAL;
            case FOLDED -> YamlScalarType.FOLDED;
            case JSON_SCALAR_STYLE -> YamlScalarType.JSON_SCALAR_STYLE;
            case PLAIN -> YamlScalarType.PLAIN;
        };
    }

    private static void copyCommentsToApi(Node source, YamlNode target) {
        target.setBlockComments(toApiComments(source.getBlockComments()));
        target.setEndComments(toApiComments(source.getEndComments()));
        target.setInLineComments(toApiComments(source.getInLineComments()));
    }

    private static List<YamlCommentLine> toApiComments(List<CommentLine> source) {
        List<YamlCommentLine> result = new ArrayList<>();
        if (source == null) {
            return result;
        }

        for (CommentLine commentLine : source) {
            result.add(new YamlCommentLine(
                    fromSnakeCommentType(commentLine.getCommentType()),
                    commentLine.getValue()
            ));
        }
        return result;
    }

    private static void copyCommentsToSnake(YamlNode source, Node target) {
        target.setBlockComments(toSnakeComments(source.getBlockComments()));
        target.setEndComments(toSnakeComments(source.getEndComments()));
        target.setInLineComments(toSnakeComments(source.getInLineComments()));
    }

    private static List<CommentLine> toSnakeComments(List<YamlCommentLine> source) {
        List<CommentLine> result = new ArrayList<>();
        if (source == null) {
            return result;
        }

        for (YamlCommentLine commentLine : source) {
            result.add(new CommentLine(
                    Optional.empty(),
                    Optional.empty(),
                    commentLine.comment(),
                    toSnakeCommentType(commentLine.commentType())
            ));
        }
        return result;
    }

    private static org.snakeyaml.engine.v2.comments.CommentType toSnakeCommentType(CommentType commentType) {
        if (commentType == null) {
            return org.snakeyaml.engine.v2.comments.CommentType.BLANK_LINE;
        }
        return switch (commentType) {
            case BLOCK -> org.snakeyaml.engine.v2.comments.CommentType.BLOCK;
            case IN_LINE -> org.snakeyaml.engine.v2.comments.CommentType.IN_LINE;
            case BLANK_LINE -> org.snakeyaml.engine.v2.comments.CommentType.BLANK_LINE;
        };
    }

    private static CommentType fromSnakeCommentType(org.snakeyaml.engine.v2.comments.CommentType commentType) {
        if (commentType == null) {
            return CommentType.BLANK_LINE;
        }
        return switch (commentType) {
            case BLOCK -> CommentType.BLOCK;
            case IN_LINE -> CommentType.IN_LINE;
            case BLANK_LINE -> CommentType.BLANK_LINE;
        };
    }
}
