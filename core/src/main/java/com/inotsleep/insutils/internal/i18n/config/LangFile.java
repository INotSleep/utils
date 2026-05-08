package com.inotsleep.insutils.internal.i18n.config;

import com.inotsleep.insutils.api.i18n.LangEntry;
import com.inotsleep.insutils.api.yaml.*;
import com.inotsleep.insutils.spi.config.UnsafeConfig;

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

    public void putAll(LangFile other) {
        if (other == null) {
            return;
        }
        translations.putAll(other.translations);
    }

    public LangFile(File file, Map<String, LangEntry> defaults) {
        super(file);
        translations.putAll(defaults);
    }

    @Override
    public void beforeDeserialization(YamlMappingNode node) {
        translations.clear();
        processMappingNode(node, "");
    }

    @Override
    public void afterDeserialization(YamlMappingNode node) {
    }

    private void processMappingNode(YamlMappingNode node, String prefix) {
        for (YamlNodeTyple tuple : node.getNodes()) {
            YamlScalarNode keyNode = tuple.getKey();
            if (keyNode == null) {
                continue;
            }

            String key = keyNode.getValue();
            YamlNode valueNode = tuple.getValue();
            switch (valueNode) {
                case YamlMappingNode mappingNode -> {
                    processMappingNode(mappingNode, prefix + key + ".");
                    continue;
                }
                case YamlScalarNode scalarNode -> {
                    translations.put(prefix + key, new LangEntry(scalarNode.getValue()));
                    continue;
                }
                case YamlSequenceNode sequenceNode -> {
                    List<String> values = sequenceNode
                            .getNodes()
                            .stream()
                            .filter(it -> it instanceof YamlScalarNode)
                            .map(it -> ((YamlScalarNode) it).getValue())
                            .toList();

                    translations.put(prefix + key, new LangEntry(values));
                }
                case null, default -> {
                }
            }

        }
    }

    @Override
    public void beforeSerialization(YamlMappingNode root) {
        root.setNodes(new ArrayList<>());

        for (Map.Entry<String, LangEntry> entry : translations.entrySet()) {
            String fullKey = entry.getKey();
            LangEntry langEntry = entry.getValue();

            String[] parts = fullKey.split("\\.");
            insertLangEntry(root, parts, 0, langEntry);
        }
    }

    @Override
    public void afterSerialization(YamlMappingNode node) {
    }

    private YamlScalarType getScalarStyle(String value) {
        return value.contains("\n") || value.contains("\r") ? YamlScalarType.LITERAL : YamlScalarType.DOUBLE_QUOTED;
    }

    private void insertLangEntry(YamlMappingNode current, String[] parts, int index, LangEntry entry) {
        String keyPart = parts[index];
        if (index == parts.length - 1) {
            YamlNode valueNode = null;

            if (entry.isList()) {
                List<YamlNode> items = new ArrayList<>();
                for (String line : entry.getListValue()) {
                    items.add(YamlNodes.scalar(line, getScalarStyle(line)));
                }
                valueNode = YamlNodes.sequence(items);
            } else if (entry.isString()) {
                valueNode = YamlNodes.scalar(entry.getValue(), getScalarStyle(entry.getValue()));
            }

            if (valueNode != null) {
                current.getNodes().add(YamlNodes.entry(keyPart, valueNode));
            }
            return;
        }

        YamlMappingNode childMap = null;
        for (YamlNodeTyple tuple : current.getNodes()) {
            if (tuple.getKey() == null || tuple.getValue() == null) {
                continue;
            }

            if (keyPart.equals(tuple.getKey().getValue()) && tuple.getValue() instanceof YamlMappingNode mappingNode) {
                childMap = mappingNode;
                break;
            }
        }

        if (childMap == null) {
            childMap = YamlNodes.mapping();
            current.getNodes().add(YamlNodes.entry(keyPart, childMap));
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
}
