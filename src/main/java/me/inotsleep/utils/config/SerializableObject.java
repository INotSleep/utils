package me.inotsleep.utils.config;

import me.inotsleep.utils.logging.LoggingManager;
import org.snakeyaml.engine.v2.comments.CommentLine;
import org.snakeyaml.engine.v2.comments.CommentType;
import org.snakeyaml.engine.v2.common.FlowStyle;
import org.snakeyaml.engine.v2.common.ScalarStyle;
import org.snakeyaml.engine.v2.nodes.*;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

public abstract class SerializableObject {

    public SerializableObject() {}

    private String[] parseHeader() {
        Header header = getClass().getAnnotation(Header.class);
        if (header == null) return null;

        return header.value();
    }

    public void serialize(MappingNode node) {
        mutateSerialization();

        List<Field> allFields = new ArrayList<>();
        Class<?> cls = getClass();
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
                fieldValue = field.get(this);
            } catch (IllegalAccessException e) {
                LoggingManager.error("Unable to access field value", e);
                continue;
            }
            if (fieldValue == null) {
                continue;
            }

            Node valueNode = serializeValue(fieldValue);

            if (valueNode != null) {
                Node commentNode = (valueNode instanceof ScalarNode) ? valueNode : keyNode;

                processFieldComments(field, keyNode, commentNode);
                NodeTuple tuple = new NodeTuple(keyNode, valueNode);
                node.getValue().add(tuple);
            }
            String[] header = parseHeader();
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
        }
    }

    public void deserialize(MappingNode node) {
        Map < String, Node > nodeMap = new HashMap < > ();
        for (NodeTuple tuple: node.getValue()) {
            Node keyNode = tuple.getKeyNode();
            if (keyNode instanceof ScalarNode) {
                String key = ((ScalarNode) keyNode).getValue();
                nodeMap.put(key, tuple.getValueNode());
            }
        }

        List<Field> allFields = new ArrayList<>();
        Class<?> cls = getClass();
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
            Node fieldNode = nodeMap.get(key);
            if (fieldNode == null) {
                continue;
            }

            try {
                Object deserialized = deserializeField(field, fieldNode, key);
                if (deserialized != null) {
                    field.set(this, deserialized);
                }
            } catch (IllegalAccessException e) {
                LoggingManager.error("Unable to set field value", e);
            }
        }

        mutateDeserialization();
    }

    public void mutateDeserialization() {

    }

    public void mutateSerialization() {

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
                            .collect(Collectors.toList())
            );
        }

        List < CommentLine > lines = new ArrayList < > ();
        List < CommentLine > inlineLines = new ArrayList<>();

        for (Comment comment: commentList) {
            CommentLine line = new CommentLine(
                    Optional.empty(),
                    Optional.empty(),
                    " " + comment.value(),
                    comment.type()
            );

            if (comment.type() == CommentType.BLOCK) lines.add(line);
            else if (comment.type() == CommentType.IN_LINE) inlineLines.add(line);

        }

        if (!lines.isEmpty()) {
            lines.add(0, new CommentLine(
                    Optional.empty(),
                    Optional.empty(),
                    "",
                    CommentType.BLANK_LINE
            ));
        }

        keyNode.setBlockComments(lines);
        valueNode.setInLineComments(inlineLines);
    }

    private Node serializeValue(Object value) {
        if (value instanceof SerializableObject) {
            MappingNode childNode = new MappingNode(Tag.MAP, new ArrayList < > (), FlowStyle.AUTO);
            ((SerializableObject) value).serialize(childNode);
            return childNode;
        } else if (value instanceof Map) {
            return serializeMap((Map < ? , ? > ) value);
        } else if (value instanceof Collection) {
            return serializeCollection((Collection < ? > ) value);
        } else {
            return serializePrimitive(value);
        }
    }

    private MappingNode serializeMap(Map < ? , ? > map) {
        List < NodeTuple > tuples = new ArrayList < > ();
        for (Map.Entry < ? , ? > entry : map.entrySet()) {
            Node keyNode = serializePrimitive(entry.getKey());

            Node valNode = serializeValue(entry.getValue());

            NodeTuple tuple = new NodeTuple(keyNode, valNode);
            tuples.add(tuple);
        }
        return new MappingNode(Tag.MAP, tuples, FlowStyle.AUTO);
    }

    private SequenceNode serializeCollection(Collection < ? > collection) {
        List < Node > nodes = new ArrayList < > ();
        for (Object item: collection) {
            nodes.add(serializeValue(item));
        }
        return new SequenceNode(Tag.SEQ, nodes, FlowStyle.AUTO);
    }

    private ScalarNode serializePrimitive(Object value) {
        Tag tag;
        ScalarStyle style = ScalarStyle.PLAIN;
        if (value == null) {
            tag = Tag.NULL;
            return new ScalarNode(tag, "null", style);
        }

        if (value instanceof Enum < ? > ) {
            tag = Tag.STR;
            return new ScalarNode(tag, ((Enum < ? > ) value).name(), style);
        }

        if (value instanceof String || value instanceof Character) {
            tag = Tag.STR;
        } else if (value instanceof Integer || value instanceof Long ||
                value instanceof Short || value instanceof Byte) {
            tag = Tag.INT;
        } else if (value instanceof Float || value instanceof Double) {
            tag = Tag.FLOAT;
        } else if (value instanceof Boolean) {
            tag = Tag.BOOL;
        } else {
            tag = Tag.STR;
        }

        return new ScalarNode(tag, value.toString(), style);
    }

    private Object deserializeField(Field field, Node node, String key) {
        Class < ? > fieldType = field.getType();
        return deserializeFieldByType(fieldType, field.getGenericType(), node, key);
    }

    private Object deserializeFieldByType(Class < ? > type, java.lang.reflect.Type genericType, Node node, String key) {
        if (SerializableObject.class.isAssignableFrom(type)) {
            if (!(node instanceof MappingNode)) {
                LoggingManager.warn("Expected MappingNode for object '" + key + "' but found " + node.getNodeType());
                return null;
            }
            return deserializeObject((MappingNode) node, type, key);
        }

        if (Map.class.isAssignableFrom(type)) {
            if (!(node instanceof MappingNode)) {
                LoggingManager.warn("Expected MappingNode for map '" + key + "' but found " + node.getNodeType());
                return null;
            }
            return deserializeMap((MappingNode) node, genericType);
        }

        if (Collection.class.isAssignableFrom(type)) {
            if (!(node instanceof SequenceNode)) {
                LoggingManager.warn("Expected SequenceNode for collection '" + key + "' but found " + node.getNodeType());
                return null;
            }
            return deserializeCollection((SequenceNode) node, genericType);
        }

        if (type.isEnum()) {
            if (!(node instanceof ScalarNode)) {
                LoggingManager.warn("Expected ScalarNode for enum '" + key + "' but found " + node.getNodeType());
                return null;
            }
            return deserializeEnum(type, (ScalarNode) node);
        }

        if (type.isPrimitive() ||
                Number.class.isAssignableFrom(type) ||
                type.equals(String.class) ||
                type.equals(Character.class) ||
                type.equals(Boolean.class)) {

            if (!(node instanceof ScalarNode)) {
                LoggingManager.warn("Expected ScalarNode for primitive '" + key + "' but found " + node.getNodeType());
                return null;
            }
            return deserializePrimitive(type, (ScalarNode) node);
        }

        LoggingManager.warn("Unsupported field type: " + type.getName() + ". Will treat as string.");
        if (node instanceof ScalarNode) {
            return ((ScalarNode) node).getValue();
        }
        return null;
    }

    private SerializableObject deserializeObject(MappingNode node, Class < ? > clazz, String key) {
        try {
            SerializableObject obj = (SerializableObject) clazz.getDeclaredConstructor().newInstance();
            obj.deserialize(node);
            return obj;
        } catch (Exception e) {
            LoggingManager.error("Failed to deserialize object for '" + key + "'", e);
        }
        return null;
    }

    private Map < Object, Object > deserializeMap(MappingNode node, java.lang.reflect.Type genericType) {
        Class < ? > keyClass = Object.class;
        Class < ? > valClass = Object.class;
        java.lang.reflect.Type valGenType = null;

        if (genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericType;
            java.lang.reflect.Type kType = pt.getActualTypeArguments()[0];
            java.lang.reflect.Type vType = pt.getActualTypeArguments()[1];

            if (kType instanceof Class) {
                keyClass = (Class < ? > ) kType;
            }
            valGenType = vType;
            if (vType instanceof Class) {
                valClass = (Class < ? > ) vType;
            }
        }

        Map < Object, Object > map = new LinkedHashMap < > ();
        for (NodeTuple tuple: node.getValue()) {
            Node kNode = tuple.getKeyNode();
            Node vNode = tuple.getValueNode();

            Object mapKey = deserializeFieldByType(keyClass, keyClass, kNode, "map-key");
            Object mapValue = deserializeFieldByType(valClass, valGenType, vNode, "map-value");
            map.put(mapKey, mapValue);
        }
        return map;
    }

    private Collection < Object > deserializeCollection(SequenceNode node, java.lang.reflect.Type genericType) {
        Class < ? > elementClass = Object.class;
        java.lang.reflect.Type elementGenType = null;

        if (genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericType;
            java.lang.reflect.Type argType = pt.getActualTypeArguments()[0];
            elementGenType = argType;
            if (argType instanceof Class) {
                elementClass = (Class < ? > ) argType;
            }
        }

        Collection < Object > collection;
        collection = new ArrayList < > ();

        for (Node itemNode: node.getValue()) {
            Object item = deserializeFieldByType(elementClass, elementGenType, itemNode, "item");
            collection.add(item);
        }
        return collection;
    }

    @SuppressWarnings({
            "rawtypes",
            "unchecked"
    })
    private Object deserializeEnum(Class < ? > enumType, ScalarNode node) {
        String value = node.getValue();
        try {
            return Enum.valueOf((Class < Enum > ) enumType, value);
        } catch (IllegalArgumentException ex) {
            LoggingManager.warn("Invalid enum value '" + value + "' for enum " + enumType.getName());
            return null;
        }
    }

    private Object deserializePrimitive(Class < ? > type, ScalarNode node) {
        String value = node.getValue();
        if (type.equals(String.class)) {
            return value;
        }
        if (type.equals(char.class) || type.equals(Character.class)) {
            return value.isEmpty() ? '\0' : value.charAt(0);
        }
        if (type.equals(boolean.class) || type.equals(Boolean.class)) {
            return Boolean.parseBoolean(value);
        }

        try {
            if (type.equals(int.class) || type.equals(Integer.class)) {
                return Integer.parseInt(value);
            }
            if (type.equals(long.class) || type.equals(Long.class)) {
                return Long.parseLong(value);
            }
            if (type.equals(short.class) || type.equals(Short.class)) {
                return Short.parseShort(value);
            }
            if (type.equals(byte.class) || type.equals(Byte.class)) {
                return Byte.parseByte(value);
            }
            if (type.equals(float.class) || type.equals(Float.class)) {
                return Float.parseFloat(value);
            }
            if (type.equals(double.class) || type.equals(Double.class)) {
                return Double.parseDouble(value);
            }
        } catch (NumberFormatException e) {
            LoggingManager.warn("Invalid numeric value '" + value + "' for type " + type.getName());
            return null;
        }

        return null;
    }
}