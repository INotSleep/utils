package me.inotsleep.utils.logging;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

public class LoggingManager {
    private static ILogger logger;

    public static void setLogger(final ILogger logger) {
        LoggingManager.logger = logger;
    }

    public static void setLogger(final Object logger) {
        if (logger.getClass().getName().contains("slf4j") && logger.getClass().getName().contains("Log")) {
            setLogger(instantiateWrapper("me.inotsleep.utils.logging.SLF4JLogger", logger));
        }

        if (logger.getClass().getName().contains("log4j") && logger.getClass().getName().contains("Log")) {
            setLogger(instantiateWrapper("me.inotsleep.utils.logging.ApacheLog4JLogger", logger));
        }

        if (logger.getClass().getName().contains("java.util.logging.Logger")) {
            setLogger(new JavaLogger((Logger) logger));
        }

    }

    public static ILogger getLogger() {
        return logger;
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
            return null;
        }
    }

    public static void log(String message) {
        if (logger == null) return;

        logger.log(message);
    }

    public static void log(String message, Throwable throwable) {
        if (logger == null) return;

        logger.log(message, throwable);
    }

    public static void log(Level level, String message) {
        if (logger == null) return;

        logger.log(level, message);
    }

    public static void log(Level level, String message, Throwable throwable) {
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
