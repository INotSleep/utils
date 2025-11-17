package com.inotsleep.utils.logging;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public final class LoggingManager {

    private static final Map<ClassLoader, ILogger> LOGGERS = new ConcurrentHashMap<>();
    private static ILogger fallbackLogger;
    private static final StackWalker WALKER =
            StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    public static void setLogger(final ILogger logger) {
        if (logger == null) {
            return;
        }

        Class<?> caller = findCallerClass();
        if (caller != null) {
            ClassLoader cl = caller.getClassLoader();
            LOGGERS.put(cl, logger);

            if (fallbackLogger == null) {
                fallbackLogger = logger;
            }
        } else {
            fallbackLogger = logger;
        }
    }

    public static void setLogger(final Object logger) {
        if (logger == null) {
            return;
        }

        String name = logger.getClass().getName();

        if (name.contains("slf4j") && name.contains("Log")) {
            ILogger wrapped = instantiateWrapper("me.inotsleep.utils.logging.SLF4JLogger", logger);
            setLogger(wrapped);
            return;
        }

        if (name.contains("log4j") && name.contains("Log")) {
            ILogger wrapped = instantiateWrapper("me.inotsleep.utils.logging.ApacheLog4JLogger", logger);
            setLogger(wrapped);
            return;
        }

        if (Logger.class.isAssignableFrom(logger.getClass())) {
            setLogger(new JavaLogger((Logger) logger));
        }
    }

    public static ILogger getLogger() {
        return resolveLogger();
    }

    @SuppressWarnings("unchecked")
    private static <T> T instantiateWrapper(
            String wrapperClassName,
            Object delegate
    ) {
        try {
            Class<?> wrapperClass = Class.forName(
                    wrapperClassName,
                    false,
                    LoggingManager.class.getClassLoader()
            );

            Constructor<?> ctor = wrapperClass.getConstructor(Object.class);
            Object instance = ctor.newInstance(delegate);

            return (T) instance;
        } catch (ClassNotFoundException
                 | NoSuchMethodException
                 | InvocationTargetException
                 | InstantiationException
                 | IllegalAccessException
                 | LinkageError e) {
            e.printStackTrace();
            return null;
        }
    }
    private static Class<?> findCallerClass() {
        return WALKER.walk(stream ->
                stream
                        .map(StackWalker.StackFrame::getDeclaringClass)
                        .filter(c -> c != LoggingManager.class)
                        .findFirst()
                        .orElse(null)
        );
    }

    private static ILogger resolveLogger() {
        Class<?> caller = findCallerClass();

        if (caller != null) {
            ClassLoader cl = caller.getClassLoader();
            ILogger logger = LOGGERS.get(cl);
            if (logger != null) {
                return logger;
            }
        }

        return fallbackLogger;
    }

    public static ILogger wrap(Object logger) {
        if (logger == null) {
            return null;
        }

        String name = logger.getClass().getName();

        if (name.contains("slf4j") && name.contains("Log")) {
            return instantiateWrapper("com.inotsleep.utils.logging.SLF4JLogger", logger);
        }

        if (name.contains("log4j") && name.contains("Log")) {
            return instantiateWrapper("com.inotsleep.utils.logging.ApacheLog4JLogger", logger);
        }

        if (logger instanceof Logger j) {
            return new JavaLogger(j);
        }

        return null;
    }

    public static void log(String message) {
        ILogger logger = resolveLogger();
        if (logger == null) return;

        logger.log(message);
    }

    public static void log(String message, Throwable throwable) {
        ILogger logger = resolveLogger();
        if (logger == null) return;

        logger.log(message, throwable);
    }

    public static void log(Level level, String message) {
        ILogger logger = resolveLogger();
        if (logger == null) return;

        logger.log(level, message);
    }

    public static void log(Level level, String message, Throwable throwable) {
        ILogger logger = resolveLogger();
        if (logger == null) return;

        logger.log(level, message, throwable);
    }

    public static void trace(String message) {
        log(Level.TRACE, message);
    }

    public static void trace(String message, Throwable throwable) {
        log(Level.TRACE, message, throwable);
    }

    public static void debug(String message) {
        log(Level.DEBUG, message);
    }

    public static void debug(String message, Throwable throwable) {
        log(Level.DEBUG, message, throwable);
    }

    public static void info(String message) {
        log(Level.INFO, message);
    }

    public static void info(String message, Throwable throwable) {
        log(Level.INFO, message, throwable);
    }

    public static void warn(String message) {
        log(Level.WARN, message);
    }

    public static void warn(String message, Throwable throwable) {
        log(Level.WARN, message, throwable);
    }

    public static void error(String message) {
        log(Level.ERROR, message);
    }

    public static void error(String message, Throwable throwable) {
        log(Level.ERROR, message, throwable);
    }
}
