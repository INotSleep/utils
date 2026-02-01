package com.inotsleep.insutils.api.config;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

public abstract class TypeKey<T> {

    private final Type type;
    private final Class<?> raw;

    protected TypeKey() {
        this.type = normalizeType(captureTypeArgument(getClass()));
        this.raw = rawClassOf(this.type);
    }

    private TypeKey(Type type) {
        this.type = normalizeType(Objects.requireNonNull(type, "type"));
        this.raw = rawClassOf(this.type);
    }

    public static <T> TypeKey<T> of(Class<T> type) {
        return new SimpleTypeKey<>(type);
    }

    public static TypeKey<?> of(Type type) {
        return new SimpleTypeKey<>(type);
    }

    public final Type type() {
        return type;
    }

    public final Class<?> raw() {
        return raw;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TypeKey<?> other)) {
            return false;
        }
        return type.equals(other.type);
    }

    @Override
    public final int hashCode() {
        return type.hashCode();
    }

    @Override
    public String toString() {
        return type.getTypeName();
    }

    private static Type captureTypeArgument(Class<?> subclass) {
        Type st = subclass.getGenericSuperclass();
        if (!(st instanceof ParameterizedType pt)) {
            throw new IllegalStateException(
                    "TypeKey must be created with type parameter, e.g. new TypeKey<MyClass<String>>() {}"
            );
        }
        return pt.getActualTypeArguments()[0];
    }

    public static Class<?> rawClassOf(Type t) {
        if (t instanceof Class<?> c) {
            return c;
        }
        if (t instanceof ParameterizedType p) {
            Type rt = p.getRawType();
            if (rt instanceof Class<?> c) {
                return c;
            }
        }
        if (t instanceof GenericArrayType ga) {
            Class<?> comp = rawClassOf(ga.getGenericComponentType());
            if (comp != null) {
                // делаем Class для массива компонента: comp[].
                return java.lang.reflect.Array.newInstance(comp, 0).getClass();
            }
        }

        return Object.class;
    }

    private static Type normalizeType(Type type) {
        if (type instanceof Class<?> c) {
            return normalizeClass(c);
        }
        return type;
    }

    private static Class<?> normalizeClass(Class<?> c) {
        if (!c.isPrimitive()) {
            return c;
        }
        if (c == int.class) return Integer.class;
        if (c == long.class) return Long.class;
        if (c == boolean.class) return Boolean.class;
        if (c == double.class) return Double.class;
        if (c == float.class) return Float.class;
        if (c == byte.class) return Byte.class;
        if (c == short.class) return Short.class;
        if (c == char.class) return Character.class;
        if (c == void.class) return Void.class;
        return c;
    }

    private static final class SimpleTypeKey<T> extends TypeKey<T> {
        private SimpleTypeKey(Type type) {
            super(type);
        }
    }
}