package com.inotsleep.insutils.internal.config;

import com.inotsleep.insutils.api.config.*;
import com.inotsleep.insutils.api.config.codecs.Codec;
import com.inotsleep.insutils.api.yaml.*;
import com.inotsleep.insutils.internal.config.codecs.DefaultCodecs;
import com.inotsleep.insutils.spi.config.UnsafeConfig;
import com.inotsleep.insutils.spi.config.UnsafeSerializableObject;
import com.inotsleep.insutils.api.logging.LoggingManager;
import com.inotsleep.insutils.internal.yaml.YamlNodeConverter;
import org.snakeyaml.engine.v2.api.*;
import org.snakeyaml.engine.v2.comments.CommentLine;
import org.snakeyaml.engine.v2.comments.CommentType;
import org.snakeyaml.engine.v2.common.FlowStyle;
import org.snakeyaml.engine.v2.common.ScalarStyle;
import org.snakeyaml.engine.v2.composer.Composer;
import org.snakeyaml.engine.v2.nodes.*;
import org.snakeyaml.engine.v2.parser.ParserImpl;
import org.snakeyaml.engine.v2.scanner.StreamReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class ConfigHandleImpl implements ConfigHandle {
    CodecRegistry registry;

    public ConfigHandleImpl() {
        registry = new CodecRegistry();
        DefaultCodecs.register(registry, createCodecContext());
    }

    private void serialize(UnsafeSerializableObject target, MappingNode node) {
        YamlMappingNode beforeNode = YamlNodeConverter.toApiMappingNode(node);
        target.beforeSerialization(beforeNode);
        applyYamlMappingChanges(node, beforeNode);

        List<Field> allFields = new ArrayList<>();
        Class<?> cls = target.getClass();
        while (cls != null) {
            allFields.addAll(Arrays.asList(cls.getDeclaredFields()));
            cls = cls.getSuperclass();
        }

        for (Field field: allFields) {
            field.setAccessible(true);

            Path path = field.getAnnotation(Path.class);
            if (path == null) {
                continue;
            }

            String key = path.value();
            ScalarNode keyNode = new ScalarNode(
                    Tag.STR,
                    key,
                    ScalarStyle.PLAIN
            );

            Object fieldValue;
            try {
                Object receiver = Modifier.isStatic(field.getModifiers()) ? null : target;
                boolean accessible = field.canAccess(receiver);
                field.setAccessible(true);
                fieldValue = field.get(receiver);
                field.setAccessible(accessible);
            } catch (IllegalAccessException e) {
                LoggingManager.error("Unable to access field value", e);
                continue;
            }
            if (fieldValue == null) {
                continue;
            }

            Node valueNode = serializeValue(field.getGenericType(), fieldValue);

            if (valueNode != null) {
                Node commentNode = (valueNode instanceof ScalarNode) ? valueNode : keyNode;

                processFieldComments(field, keyNode, commentNode);
                NodeTuple tuple = new NodeTuple(keyNode, valueNode);
                node.getValue().add(tuple);
            }
        }

        String[] header = parseHeader(target);
        if (header != null) node.setBlockComments(
                Arrays
                        .stream(header)
                        .map(
                                s ->
                                        new CommentLine(
                                                Optional.empty(),
                                                Optional.empty(),
                                                " " + s,
                                                CommentType.BLOCK
                                        )
                        )
                        .collect(Collectors.toList())
        );

        YamlMappingNode afterNode = YamlNodeConverter.toApiMappingNode(node);
        target.afterSerialization(afterNode);
        applyYamlMappingChanges(node, afterNode);
    }

    private void deserialize(UnsafeSerializableObject target, MappingNode node) {
        YamlMappingNode beforeNode = YamlNodeConverter.toApiMappingNode(node);
        target.beforeDeserialization(beforeNode);
        applyYamlMappingChanges(node, beforeNode);

        Map< String, Node > nodeMap = new HashMap < > ();
        for (NodeTuple tuple: node.getValue()) {
            Node keyNode = tuple.getKeyNode();
            if (keyNode instanceof ScalarNode) {
                String key = ((ScalarNode) keyNode).getValue();
                nodeMap.put(key, tuple.getValueNode());
            }
        }

        List<Field> allFields = new ArrayList<>();
        Class<?> cls = target.getClass();
        while (cls != null) {
            allFields.addAll(Arrays.asList(cls.getDeclaredFields()));
            cls = cls.getSuperclass();
        }

        for (Field field: allFields) {
            Path path = field.getAnnotation(Path.class);
            if (path == null) {
                continue;
            }

            String key = path.value();
            Node fieldNode = nodeMap.get(key);
            if (fieldNode == null) {
                continue;
            }
            try {
                Object receiver = Modifier.isStatic(field.getModifiers()) ? null : target;
                boolean accessible = field.canAccess(receiver);
                field.setAccessible(true);
                Object deserialized = deserializeField(field, fieldNode, key);
                if (deserialized != null) {
                    field.set(target, deserialized);
                }
                field.setAccessible(accessible);
            } catch (IllegalAccessException e) {
                LoggingManager.error("Unable to set field value", e);
            }
        }

        YamlMappingNode afterNode = YamlNodeConverter.toApiMappingNode(node);
        target.afterDeserialization(afterNode);
        applyYamlMappingChanges(node, afterNode);
    }

    private String[] parseHeader(UnsafeSerializableObject target) {
        Header header = target.getClass().getAnnotation(Header.class);
        if (header == null) return null;

        return header.value();
    }

    private void applyYamlMappingChanges(MappingNode target, YamlMappingNode source) {
        MappingNode converted = YamlNodeConverter.toSnakeMappingNode(source);
        target.setValue(copyOrEmpty(converted.getValue()));
        target.setBlockComments(copyOrEmpty(converted.getBlockComments()));
        target.setEndComments(copyOrEmpty(converted.getEndComments()));
        target.setInLineComments(copyOrEmpty(converted.getInLineComments()));
    }

    private static <T> ArrayList<T> copyOrEmpty(List<T> source) {
        return source == null ? new ArrayList<>() : new ArrayList<>(source);
    }

    private DefaultCodecs.CodecContext createCodecContext() {
        return new DefaultCodecs.CodecContext() {
            @Override
            public YamlNode serialize(Type type, Object value) {
                return ConfigHandleImpl.this.serialize(type, value);
            }

            @Override
            public Object deserialize(Type type, YamlNode node) {
                return ConfigHandleImpl.this.deserialize(type, node);
            }

            @Override
            public YamlScalarNode serializeMapKey(Type type, Object value) {
                return ConfigHandleImpl.this.serializeMapKey(type, value);
            }

            @Override
            public YamlMappingNode serializeObject(UnsafeSerializableObject value) {
                return ConfigHandleImpl.this.serializeObjectNode(value);
            }

            @Override
            public <T extends UnsafeSerializableObject> T deserializeObject(Class<T> type, YamlMappingNode node) {
                return ConfigHandleImpl.this.deserializeObjectNode(type, node);
            }
        };
    }

    private void processFieldComments(Field field, Node keyNode, Node valueNode) {
        Comments comments = field.getAnnotation(Comments.class);
        List<Comment> commentList = new ArrayList<>();
        if (comments == null) {
            Comment sComment = field.getAnnotation(Comment.class);
            if (sComment != null) commentList.add(sComment);
            else return;
        } else {
            commentList.addAll(
                    Arrays
                            .stream(comments.value())
                            .toList()
            );
        }

        List < CommentLine > lines = new ArrayList < > ();
        List < CommentLine > inlineLines = new ArrayList<>();

        for (Comment comment: commentList) {
            CommentLine line = new CommentLine(
                    Optional.empty(),
                    Optional.empty(),
                    " " + comment.value(),
                    toSnakeYmlCommentType(comment.type())
            );

            if (toSnakeYmlCommentType(comment.type()) == CommentType.BLOCK) lines.add(line);
            else if (toSnakeYmlCommentType(comment.type()) == CommentType.IN_LINE) inlineLines.add(line);

        }

        if (!lines.isEmpty()) {
            lines.addFirst(new CommentLine(
                    Optional.empty(),
                    Optional.empty(),
                    "",
                    CommentType.BLANK_LINE
            ));
        }

        keyNode.setBlockComments(lines);
        valueNode.setInLineComments(inlineLines);
    }

    private Node serializeValue(Type type, Object value) {
        if (value == null) {
            return serializeFallbackScalar(null, true);
        }

        Type resolvedType = resolveSerializationType(type, value);
        Optional<Codec<?>> optionalCodec = registry.find(resolvedType);
        if (optionalCodec.isPresent()) {
            return YamlNodeConverter.toSnakeNode(optionalCodec.get().serialize(TypeKey.of(resolvedType), value));
        }

        return serializeFallbackScalar(value, true);
    }

    private YamlScalarNode serializeMapKey(Type type, Object value) {
        YamlNode keyNode = serialize(resolveSerializationType(type, value), value);
        if (keyNode instanceof YamlScalarNode scalarNode) {
            if (scalarNode.getScalarType() == YamlScalarType.DOUBLE_QUOTED &&
                    scalarNode.getValue().indexOf('\n') < 0 &&
                    scalarNode.getValue().indexOf('\r') < 0) {
                return YamlNodes.scalar(scalarNode.getValue(), YamlScalarType.PLAIN);
            }
            return scalarNode;
        }

        LoggingManager.warn("Unsupported map key type: " + (value == null ? "null" : value.getClass().getName()) + ". Will treat as string.");
        return (YamlScalarNode) YamlNodeConverter.toApiNode(serializeFallbackScalar(value, false));
    }

    private Type resolveSerializationType(Type type, Object value) {
        Type normalizedType = normalizeKnownType(type);
        if (normalizedType == Object.class && value != null) {
            return value.getClass();
        }
        return normalizedType;
    }

    private ScalarNode serializeFallbackScalar(Object value, boolean quoteStrings) {
        Tag tag;
        ScalarStyle style;

        if (value == null) {
            return new ScalarNode(Tag.NULL, "null", ScalarStyle.PLAIN);
        }

        if (value instanceof String || value instanceof Character) {
            tag = Tag.STR;

            String s = value.toString();
            boolean multiline = s.indexOf('\n') >= 0 || s.indexOf('\r') >= 0;

            if (multiline) {
                style = ScalarStyle.LITERAL;
            } else {
                style = quoteStrings ? ScalarStyle.DOUBLE_QUOTED : ScalarStyle.PLAIN;
            }

            return new ScalarNode(tag, s, style);
        }

        if (value instanceof Integer || value instanceof Long || value instanceof Short || value instanceof Byte) {
            return new ScalarNode(Tag.INT, value.toString(), ScalarStyle.PLAIN);
        }
        if (value instanceof Float || value instanceof Double) {
            return new ScalarNode(Tag.FLOAT, value.toString(), ScalarStyle.PLAIN);
        }
        if (value instanceof Boolean) {
            return new ScalarNode(Tag.BOOL, value.toString(), ScalarStyle.PLAIN);
        }

        // fallback
        return new ScalarNode(Tag.STR, value.toString(), quoteStrings ? ScalarStyle.DOUBLE_QUOTED : ScalarStyle.PLAIN);
    }

    private Object deserializeField(Field field, Node node, String key) {
        return deserializeValue(field.getGenericType(), node, key);
    }

    private Object deserializeValue(Type type, Node node, String key) {
        type = normalizeKnownType(type);
        Class<?> clazz = TypeKey.rawClassOf(type);

        Optional<Codec<?>> optionalCodec = registry.find(type);
        if (optionalCodec.isPresent()) {
            try {
                return optionalCodec.get().deserialize(TypeKey.of(type), YamlNodeConverter.toApiNode(node));
            } catch (RuntimeException exception) {
                LoggingManager.warn("Failed to deserialize '" + key + "' as " + clazz.getName() + ": " + exception.getMessage());
                return null;
            }
        }

        LoggingManager.warn("Unsupported field type: " + clazz.getName() + " (" + key + "). Will treat as string.");
        if (node instanceof ScalarNode) {
            return ((ScalarNode) node).getValue();
        }
        return null;
    }

    private YamlMappingNode serializeObjectNode(UnsafeSerializableObject value) {
        MappingNode node = new MappingNode(Tag.MAP, new ArrayList<>(), FlowStyle.AUTO);
        serialize(value, node);
        return YamlNodeConverter.toApiMappingNode(node);
    }

    private <T extends UnsafeSerializableObject> T deserializeObjectNode(Class<T> clazz, YamlMappingNode node) {
        try {
            T obj = clazz.getDeclaredConstructor().newInstance();
            deserialize(obj, YamlNodeConverter.toSnakeMappingNode(node));
            return obj;
        } catch (Exception e) {
            LoggingManager.error("Failed to deserialize object for '" + clazz.getName() + "'", e);
        }
        return null;
    }

    private Type normalizeKnownType(Type type) {
        return type == null ? Object.class : type;
    }
    private CommentType toSnakeYmlCommentType(com.inotsleep.insutils.api.config.CommentType commentType) {
        return switch (commentType) {
            case BLOCK -> CommentType.BLOCK;
            case IN_LINE ->  CommentType.IN_LINE;
            case BLANK_LINE ->   CommentType.BLANK_LINE;
            case null ->  CommentType.BLANK_LINE;
        };
    }


    public void saveConfig(UnsafeConfig config) {
        File configFile = config.getFile();
        boolean readOnly = config.isReadOnly();

        if (readOnly) {
            LoggingManager.warn("Configuration file " + configFile.getName() + " tried to save, but it's read-only.");
        }

        if (!configFile.getParentFile().exists()) {
            if (!configFile.getParentFile().mkdirs()) {
                LoggingManager.error("Unable to create directory: " + configFile.getParentFile());
            }
        }

        if (!configFile.exists()) {
            LoggingManager.error("Configuration file " + configFile.getName() + " does not exist. Creating...");
            try {
                if (!configFile.createNewFile()) LoggingManager.warn("Config file is already exists, but File#exists() returned false.");
            } catch (IOException e) {
                LoggingManager.error("Unable to create configuration file: " + configFile.getName(), e);
                return;
            }
        }

        MappingNode root = new MappingNode(Tag.MAP, new ArrayList<>(), FlowStyle.AUTO);
        serialize(config, root);

        DumpSettings settings = DumpSettings.builder()
                .setExplicitStart(false)
                .setExplicitRootTag(Optional.empty())
                .setDumpComments(true)
                .build();

        Dump dump = new Dump(settings);


        try (FileOutputStream fileStream = new FileOutputStream(configFile)) {
            dump.dumpNode(root, new YamlOutputStreamWriter(fileStream, StandardCharsets.UTF_8) {
                @Override
                public void processIOException(IOException e) {
                    LoggingManager.error("Unable to save configuration file: ", e);
                }
            });
        }  catch (IOException e) {
            LoggingManager.error("Unable to save configuration file: ", e);
        }
    }


    public void reloadConfig(UnsafeConfig config) {
        File configFile = config.getFile();
        boolean readOnly = config.isReadOnly();
        InputStream stream = config.getStream();


        if (!readOnly && !configFile.getParentFile().exists()) {
            if (!configFile.getParentFile().mkdirs()) {
                LoggingManager.error("Unable to create directory: " + configFile.getParentFile());
            }
        }

        if (!readOnly && !configFile.exists()) {
            LoggingManager.info("Configuration file " + configFile.getName() + " does not exist. Creating...");
            try {
                if (!configFile.createNewFile()) LoggingManager.warn("Config file is already exists, but File#exists() returned false.");
            } catch (IOException e) {
                LoggingManager.error( "Unable to create configuration file: " + configFile.getName(), e);
                return;
            }
            saveConfig(config);
            return;
        }

        LoadSettings settings = LoadSettings.builder().build();

        MappingNode rootNode = null;

        try {
            if (stream == null) stream = Files.newInputStream(configFile.toPath());

            Composer composer = new Composer(settings, new ParserImpl(settings, new StreamReader(settings, new YamlUnicodeReader(stream))));

            while (composer.hasNext()) {
                Node node = composer.next();
                rootNode = (MappingNode) node;
            }

            if (rootNode == null) return;

            deserialize(config, rootNode);

            stream.close();
        } catch (IOException e) {
            LoggingManager.error("Unable to read configuration file: " + configFile.getName(), e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    LoggingManager.error("Unable to read configuration file: " + configFile.getName(), e);
                }
            }
        }
    }

    @Override
    public <T> void registerCodec(Codec<T> codec) {
        registry.register(codec);
    }

    @Override
    public <T> YamlNode serialize(TypeKey<T> type, T value) {
        return YamlNodeConverter.toApiNode(serializeValue(type.type(), value));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserialize(TypeKey<T> type, YamlNode node) {
        return (T) deserializeValue(type.type(), YamlNodeConverter.toSnakeNode(node), "root");
    }
}
