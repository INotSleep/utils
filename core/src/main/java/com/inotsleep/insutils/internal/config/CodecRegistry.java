package com.inotsleep.insutils.internal.config;

import com.inotsleep.insutils.api.config.Codec;
import com.inotsleep.insutils.api.config.TypeKey;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class CodecRegistry {

    private final Map<Type, Codec<?>> exactByType = new ConcurrentHashMap<>();
    private final Map<Class<?>, Codec<?>> byRawClass = new ConcurrentHashMap<>();

    /**
     * Register codec using codec.key().
     * This is the preferred entry point (no wildcard-capture issues).
     */
    public <T> void register(Codec<T> codec) {
        Objects.requireNonNull(codec, "codec");
        registerInternal(codec.key(), codec, RegistrationPolicy.FAIL_IF_PRESENT);
    }

    /**
     * Register codec using explicit key.
     */
    public <T> void register(TypeKey<T> key, Codec<? super T> codec) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(codec, "codec");
        registerInternal(key, codec, RegistrationPolicy.FAIL_IF_PRESENT);
    }

    /**
     * Replace any existing codec for the same Type.
     */
    public <T> void replace(Codec<T> codec) {
        Objects.requireNonNull(codec, "codec");
        registerInternal(codec.key(), codec, RegistrationPolicy.REPLACE);
    }

    public Optional<Codec<?>> find(Type type) {
        Objects.requireNonNull(type, "type");
        return find(TypeKey.of(type)).map(c -> (Codec<?>) c);
    }

    public Codec<?> require(Type type) {
        return find(type).orElseThrow(() -> new IllegalStateException("No codec for: " + type.getTypeName()));
    }

    public <T> Optional<Codec<T>> find(TypeKey<T> key) {
        Objects.requireNonNull(key, "key");

        Type type = normalizeType(key.type());

        // 1) exact match
        Codec<?> exact = exactByType.get(type);
        if (exact != null) {
            return Optional.of(cast(exact));
        }

        // 2) parameterized -> raw fallback
        if (type instanceof ParameterizedType pt) {
            Type rt = pt.getRawType();
            if (rt instanceof Class<?> raw) {
                Optional<Codec<T>> byHierarchy = findByClassHierarchy((Class<T>) normalizeClass(raw));
                if (byHierarchy.isPresent()) {
                    return byHierarchy;
                }
            }
        }

        // 3) class hierarchy fallback
        Class<T> raw = (Class<T>) rawClassOf(type);
        return findByClassHierarchy(raw);
    }

    public <T> Codec<T> require(TypeKey<T> key) {
        return find(key).orElseThrow(() -> new IllegalStateException("No codec for: " + key.type().getTypeName()));
    }

    private void registerInternal(TypeKey<?> key, Codec<?> codec, RegistrationPolicy policy) {
        Type type = normalizeType(key.type());

        if (policy == RegistrationPolicy.FAIL_IF_PRESENT) {
            Codec<?> prev = exactByType.putIfAbsent(type, codec);
            if (prev != null) {
                throw new IllegalStateException("Codec already registered for: " + type.getTypeName());
            }
        } else {
            exactByType.put(type, codec);
        }

        // Raw index only for plain classes, not for parameterized types.
        // This avoids collisions like List<String> vs List<Integer> in raw map.
        if (type instanceof Class<?> c) {
            Class<?> normalized = normalizeClass(c);
            if (policy == RegistrationPolicy.FAIL_IF_PRESENT) {
                byRawClass.putIfAbsent(normalized, codec);
            } else {
                byRawClass.put(normalized, codec);
            }
        }
    }

    private <T> Optional<Codec<T>> findByClassHierarchy(Class<T> type) {
        Class<?> normalized = normalizeClass(type);

        Codec<?> exact = byRawClass.get(normalized);
        if (exact != null) {
            return Optional.of(cast(exact));
        }

        // Find nearest assignable registered raw codec (most specific).
        Class<?> bestKey = null;
        int bestDist = Integer.MAX_VALUE;

        for (Class<?> candidate : byRawClass.keySet()) {
            if (!candidate.isAssignableFrom(normalized)) {
                continue;
            }
            int dist = distance(normalized, candidate);
            if (dist >= 0 && dist < bestDist) {
                bestDist = dist;
                bestKey = candidate;
            }
        }

        if (bestKey == null) {
            return Optional.empty();
        }

        return Optional.of(cast(byRawClass.get(bestKey)));
    }

    private static int distance(Class<?> from, Class<?> to) {
        if (from.equals(to)) {
            return 0;
        }
        if (!to.isAssignableFrom(from)) {
            return -1;
        }

        ArrayDeque<Class<?>> q = new ArrayDeque<>();
        Map<Class<?>, Integer> d = new HashMap<>();

        q.add(from);
        d.put(from, 0);

        while (!q.isEmpty()) {
            Class<?> cur = q.removeFirst();
            int cd = d.get(cur);

            if (cur.equals(to)) {
                return cd;
            }

            Class<?> sup = cur.getSuperclass();
            if (sup != null && d.putIfAbsent(sup, cd + 1) == null) {
                q.addLast(sup);
            }

            for (Class<?> itf : cur.getInterfaces()) {
                if (d.putIfAbsent(itf, cd + 1) == null) {
                    q.addLast(itf);
                }
            }
        }

        return -1;
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

    private static Class<?> rawClassOf(Type type) {
        if (type instanceof Class<?> c) {
            return normalizeClass(c);
        }
        if (type instanceof ParameterizedType pt) {
            Type rt = pt.getRawType();
            if (rt instanceof Class<?> c) {
                return normalizeClass(c);
            }
        }
        throw new IllegalArgumentException("Unsupported Type: " + type);
    }

    @SuppressWarnings("unchecked")
    private static <T> Codec<T> cast(Codec<?> codec) {
        return (Codec<T>) codec;
    }

    private enum RegistrationPolicy {
        FAIL_IF_PRESENT,
        REPLACE
    }
}