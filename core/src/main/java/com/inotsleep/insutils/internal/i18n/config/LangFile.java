package com.inotsleep.insutils.internal.i18n.config;

import com.inotsleep.insutils.api.i18n.LangEntry;
import com.inotsleep.insutils.spi.config.UnsafeConfig;
import org.snakeyaml.engine.v2.common.FlowStyle;
import org.snakeyaml.engine.v2.common.ScalarStyle;
import org.snakeyaml.engine.v2.nodes.*;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LangFile extends UnsafeConfig {
    private final Map<String, LangEntry> translations = new HashMap<>();

    public LangEntry getEntry(String key) {
        return translations.get(key);
    }

    public LangFile(File file, Map<String, LangEntry> defaults) {
        super(file);
        translations.putAll(defaults);
    }

    @Override
    public void beforeDeserialization(MappingNode node) {
        translations.clear();
        processMappingNode(node, "");
    }

    @Override
    public void afterDeserialization(MappingNode node) {

    }

    private void processMappingNode(MappingNode node, String prefix) {
        for (NodeTuple tuple: node.getValue()) {
            Node keyNode = tuple.getKeyNode();
            if (keyNode instanceof ScalarNode) {
                String key = ((ScalarNode) keyNode).getValue();
                Node valueNode = tuple.getValueNode();

                if (valueNode instanceof MappingNode mappingNode) {
                    processMappingNode(mappingNode, prefix + key + ".");
                } else if (valueNode instanceof ScalarNode scalarNode) {
                    translations.put(prefix + key, new LangEntry(scalarNode.getValue()));
                } else if (valueNode instanceof SequenceNode sequenceNode) {
                    translations.put(
                            prefix + key,
                            new LangEntry(
                                    sequenceNode
                                        .getValue()
                                        .stream()
                                        .filter(it -> it.getNodeType() == NodeType.SCALAR)
                                        .map(it -> ((ScalarNode) it).getValue())
                                        .toList()
                        )
                    );
                }
            }
        }
    }

    @Override
    public void beforeSerialization(MappingNode root) {
        root.setValue(new ArrayList<>());

        for (Map.Entry<String, LangEntry> entry : translations.entrySet()) {
            String fullKey = entry.getKey();
            LangEntry langEntry = entry.getValue();

            String[] parts = fullKey.split("\\.");
            insertLangEntry(root, parts, 0, langEntry);
        }
    }

    @Override
    public void afterSerialization(MappingNode node) {

    }

    private ScalarStyle getScalarStyle(String value) {
        return value.contains("\n") || value.contains("\r") ? ScalarStyle.LITERAL : ScalarStyle.DOUBLE_QUOTED;
    }

    private void insertLangEntry(MappingNode current, String[] parts, int index, LangEntry entry) {
        String keyPart = parts[index];
        if (index == parts.length - 1) {
            ScalarNode keyNode = new ScalarNode(Tag.STR, keyPart, ScalarStyle.PLAIN);
            Node valueNode = null;

            if (entry.isList()) {
                List<Node> items = new ArrayList<>();
                for (String line : entry.getListValue()) {
                    items.add(new ScalarNode(Tag.STR, line, getScalarStyle(line)));
                }
                valueNode = new SequenceNode(Tag.SEQ, items, FlowStyle.AUTO);
            } else if (entry.isString()) {
                valueNode = new ScalarNode(
                        Tag.STR,
                        entry.getValue(),
                        getScalarStyle(entry.getValue())
                );
            }

            if (valueNode != null) current.getValue().add(new NodeTuple(keyNode, valueNode));
            return;
        }

        MappingNode childMap = null;

        for (NodeTuple tuple : current.getValue()) {
            Node k = tuple.getKeyNode();
            Node v = tuple.getValueNode();

            if (k instanceof ScalarNode scalarKey
                    && scalarKey.getValue().equals(keyPart)
                    && v instanceof MappingNode mappingNode) {
                childMap = mappingNode;
                break;
            }
        }

        if (childMap == null) {
            childMap = new MappingNode(Tag.MAP, new ArrayList<>(), FlowStyle.AUTO);
            ScalarNode keyNode = new ScalarNode(Tag.STR, keyPart, ScalarStyle.PLAIN);
            current.getValue().add(new NodeTuple(keyNode, childMap));
        }

        insertLangEntry(childMap, parts, index + 1, entry);
    }

    public LangFile(File configFile) {
        super(configFile);
        reload();
    }

    public LangFile(InputStream stream) {
        super(stream);
        reload();
    }

    public LangFile copy(File configFile) {
        return new LangFile(configFile, translations);
    }

    public void putAll(Map<String, LangEntry> entries) {
        translations.putAll(entries);
    }

    public void putAll(LangFile langFile) {
        if (langFile == null) return;
        translations.putAll(langFile.translations);
    }
}
