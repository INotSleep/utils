package com.inotsleep.insutils.internal.config.codecs;

import com.inotsleep.insutils.api.config.TypeKey;
import com.inotsleep.insutils.api.config.codecs.Codec;
import com.inotsleep.insutils.api.yaml.*;
import com.inotsleep.insutils.internal.config.CodecRegistry;
import com.inotsleep.insutils.spi.config.UnsafeSerializableObject;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;

public final class DefaultCodecs {
    private DefaultCodecs() {
    }

    public static void register(CodecRegistry registry, CodecContext context) {
        registry.register(scalarCodec(TypeKey.of(String.class), Function.identity(), Function.identity(), DefaultCodecs::stringScalarType));
        registry.register(scalarCodec(TypeKey.of(Character.class), String::valueOf, DefaultCodecs::deserializeCharacter, value -> YamlScalarType.DOUBLE_QUOTED));
        registry.register(scalarCodec(TypeKey.of(Boolean.class), String::valueOf, Boolean::parseBoolean, value -> YamlScalarType.PLAIN));
        registry.register(scalarCodec(TypeKey.of(Integer.class), String::valueOf, Integer::parseInt, value -> YamlScalarType.PLAIN));
        registry.register(scalarCodec(TypeKey.of(Long.class), String::valueOf, Long::parseLong, value -> YamlScalarType.PLAIN));
        registry.register(scalarCodec(TypeKey.of(Short.class), String::valueOf, Short::parseShort, value -> YamlScalarType.PLAIN));
        registry.register(scalarCodec(TypeKey.of(Byte.class), String::valueOf, Byte::parseByte, value -> YamlScalarType.PLAIN));
        registry.register(scalarCodec(TypeKey.of(Float.class), String::valueOf, Float::parseFloat, value -> YamlScalarType.PLAIN));
        registry.register(scalarCodec(TypeKey.of(Double.class), String::valueOf, Double::parseDouble, value -> YamlScalarType.PLAIN));
        registry.register(new MapCodec(context));
        registry.register(new CollectionCodec(context));
        registry.register(new SerializableObjectCodec(context));
    }

    public static <T extends Enum<T>> Codec<T> enumCodec(Class<T> type) {
        return new EnumCodec<>(type);
    }

    public interface CodecContext {
        YamlNode serialize(Type type, Object value);
        Object deserialize(Type type, YamlNode node);
        YamlScalarNode serializeMapKey(Type type, Object value);
        YamlMappingNode serializeObject(UnsafeSerializableObject value);
        <T extends UnsafeSerializableObject> T deserializeObject(Class<T> type, YamlMappingNode node);
    }

    private static Character deserializeCharacter(String value) {
        return value.isEmpty() ? '\0' : value.charAt(0);
    }

    private static YamlScalarType stringScalarType(String value) {
        return value.indexOf('\n') >= 0 || value.indexOf('\r') >= 0
                ? YamlScalarType.LITERAL
                : YamlScalarType.DOUBLE_QUOTED;
    }

    private static <T> Codec<T> scalarCodec(
            TypeKey<T> key,
            Function<T, String> serializer,
            Function<String, T> deserializer,
            Function<T, YamlScalarType> scalarTypeResolver
    ) {
        return new ScalarCodec<>(key, serializer, deserializer, scalarTypeResolver);
    }

    private static Type elementTypeOf(Type type) {
        if (type instanceof ParameterizedType pt) {
            return pt.getActualTypeArguments()[0];
        }
        return Object.class;
    }

    private static Type mapKeyTypeOf(Type type) {
        if (type instanceof ParameterizedType pt) {
            return pt.getActualTypeArguments()[0];
        }
        return Object.class;
    }

    private static Type mapValueTypeOf(Type type) {
        if (type instanceof ParameterizedType pt) {
            return pt.getActualTypeArguments()[1];
        }
        return Object.class;
    }

    @SuppressWarnings("unchecked")
    private static TypeKey<Map<?, ?>> mapTypeKey() {
        return (TypeKey<Map<?, ?>>) (TypeKey<?>) TypeKey.of(Map.class);
    }

    @SuppressWarnings("unchecked")
    private static TypeKey<Collection<?>> collectionTypeKey() {
        return (TypeKey<Collection<?>>) (TypeKey<?>) TypeKey.of(Collection.class);
    }

    @SuppressWarnings("unchecked")
    private static TypeKey<UnsafeSerializableObject> serializableObjectTypeKey() {
        return (TypeKey<UnsafeSerializableObject>) (TypeKey<?>) TypeKey.of(UnsafeSerializableObject.class);
    }

    @SuppressWarnings("unchecked")
    private static <T> T newInstanceOrNull(Class<?> rawType) {
        if (rawType.isInterface() || Modifier.isAbstract(rawType.getModifiers())) {
            return null;
        }

        try {
            return (T) rawType.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    private static Map<Object, Object> createMap(Class<?> rawType) {
        Map<Object, Object> concrete = newInstanceOrNull(rawType);
        if (concrete != null) {
            return concrete;
        }
        if (NavigableMap.class.isAssignableFrom(rawType) || SortedMap.class.isAssignableFrom(rawType)) {
            return new TreeMap<>();
        }
        return new LinkedHashMap<>();
    }

    private static Collection<Object> createCollection(Class<?> rawType) {
        Collection<Object> concrete = newInstanceOrNull(rawType);
        if (concrete != null) {
            return concrete;
        }
        if (Set.class.isAssignableFrom(rawType)) {
            return new LinkedHashSet<>();
        }
        if (Deque.class.isAssignableFrom(rawType) || Queue.class.isAssignableFrom(rawType)) {
            return new ArrayDeque<>();
        }
        return new ArrayList<>();
    }

    private record ScalarCodec<T>(
            TypeKey<T> key,
            Function<T, String> serializer,
            Function<String, T> deserializer,
            Function<T, YamlScalarType> scalarTypeResolver
    ) implements Codec<T> {
        @Override
        public YamlNode serialize(T value) {
            return YamlNodes.scalar(serializer.apply(value), scalarTypeResolver.apply(value));
        }

        @Override
        public T deserialize(YamlNode node) {
            if (node instanceof YamlScalarNode scalarNode) {
                return deserializer.apply(scalarNode.getValue());
            }
            throw new IllegalArgumentException("Unsupported node type " + node.getClass());
        }
    }

    private static final class EnumCodec<T extends Enum<T>> implements Codec<T> {
        private final Class<T> type;

        private EnumCodec(Class<T> type) {
            this.type = type;
        }

        @Override
        public YamlNode serialize(T value) {
            return YamlNodes.scalar(value.name(), YamlScalarType.DOUBLE_QUOTED);
        }

        @Override
        public T deserialize(YamlNode node) {
            if (!(node instanceof YamlScalarNode scalarNode)) {
                throw new IllegalArgumentException("Unsupported node type " + node.getClass());
            }
            return Enum.valueOf(type, scalarNode.getValue());
        }

        @Override
        public TypeKey<T> key() {
            return TypeKey.of(type);
        }
    }

    private static final class MapCodec implements Codec<Map<?, ?>> {
        private final CodecContext context;

        private MapCodec(CodecContext context) {
            this.context = context;
        }

        @Override
        public YamlNode serialize(Map<?, ?> value) {
            return serialize(mapTypeKey(), value);
        }

        @Override
        public Map<?, ?> deserialize(YamlNode node) {
            return (Map<?, ?>) deserialize(mapTypeKey(), node);
        }

        @Override
        public YamlNode serialize(TypeKey<?> type, Object value) {
            if (!(value instanceof Map<?, ?> map)) {
                throw new IllegalArgumentException("Expected Map but got " + (value == null ? "null" : value.getClass()));
            }

            Type keyType = mapKeyTypeOf(type.type());
            Type valueType = mapValueTypeOf(type.type());
            List<YamlNodeTyple> entries = new ArrayList<>();

            for (Map.Entry<?, ?> entry : map.entrySet()) {
                entries.add(YamlNodes.entry(
                        context.serializeMapKey(keyType, entry.getKey()),
                        context.serialize(valueType, entry.getValue())
                ));
            }

            return YamlNodes.mapping(entries);
        }

        @Override
        public Object deserialize(TypeKey<?> type, YamlNode node) {
            if (!(node instanceof YamlMappingNode mappingNode)) {
                throw new IllegalArgumentException("Expected mapping node but got " + node.getClass());
            }

            Type keyType = mapKeyTypeOf(type.type());
            Type valueType = mapValueTypeOf(type.type());
            Map<Object, Object> map = createMap(TypeKey.rawClassOf(type.type()));

            List<YamlNodeTyple> tuples = mappingNode.getNodes();
            if (tuples == null) {
                return map;
            }

            for (YamlNodeTyple tuple : tuples) {
                Object key = context.deserialize(keyType, tuple.getKey());
                Object value = context.deserialize(valueType, tuple.getValue());
                map.put(key, value);
            }
            return map;
        }

        @Override
        public TypeKey<Map<?, ?>> key() {
            return mapTypeKey();
        }
    }

    private static final class CollectionCodec implements Codec<Collection<?>> {
        private final CodecContext context;

        private CollectionCodec(CodecContext context) {
            this.context = context;
        }

        @Override
        public YamlNode serialize(Collection<?> value) {
            return serialize(collectionTypeKey(), value);
        }

        @Override
        public Collection<?> deserialize(YamlNode node) {
            return (Collection<?>) deserialize(collectionTypeKey(), node);
        }

        @Override
        public YamlNode serialize(TypeKey<?> type, Object value) {
            if (!(value instanceof Collection<?> collection)) {
                throw new IllegalArgumentException("Expected Collection but got " + (value == null ? "null" : value.getClass()));
            }

            Type elementType = elementTypeOf(type.type());
            List<YamlNode> nodes = new ArrayList<>();
            for (Object element : collection) {
                nodes.add(context.serialize(elementType, element));
            }
            return YamlNodes.sequence(nodes);
        }

        @Override
        public Object deserialize(TypeKey<?> type, YamlNode node) {
            if (!(node instanceof YamlSequenceNode sequenceNode)) {
                throw new IllegalArgumentException("Expected sequence node but got " + node.getClass());
            }

            Type elementType = elementTypeOf(type.type());
            Collection<Object> collection = createCollection(TypeKey.rawClassOf(type.type()));
            List<YamlNode> nodes = sequenceNode.getNodes();
            if (nodes == null) {
                return collection;
            }

            for (YamlNode elementNode : nodes) {
                collection.add(context.deserialize(elementType, elementNode));
            }
            return collection;
        }

        @Override
        public TypeKey<Collection<?>> key() {
            return collectionTypeKey();
        }
    }

    private static final class SerializableObjectCodec implements Codec<UnsafeSerializableObject> {
        private final CodecContext context;

        private SerializableObjectCodec(CodecContext context) {
            this.context = context;
        }

        @Override
        public YamlNode serialize(UnsafeSerializableObject value) {
            return context.serializeObject(value);
        }

        @Override
        public UnsafeSerializableObject deserialize(YamlNode node) {
            return (UnsafeSerializableObject) deserialize(serializableObjectTypeKey(), node);
        }

        @Override
        @SuppressWarnings("unchecked")
        public Object deserialize(TypeKey<?> type, YamlNode node) {
            if (!(node instanceof YamlMappingNode mappingNode)) {
                throw new IllegalArgumentException("Expected mapping node but got " + node.getClass());
            }

            Class<?> rawType = TypeKey.rawClassOf(type.type());
            if (!UnsafeSerializableObject.class.isAssignableFrom(rawType)) {
                throw new IllegalArgumentException("Expected UnsafeSerializableObject but got " + rawType);
            }

            return context.deserializeObject((Class<? extends UnsafeSerializableObject>) rawType, mappingNode);
        }

        @Override
        public TypeKey<UnsafeSerializableObject> key() {
            return serializableObjectTypeKey();
        }
    }
}
