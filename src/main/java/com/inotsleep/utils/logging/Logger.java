package com.inotsleep.utils.logging;

public interface Logger {
    void log(String message);
    void log(String message, Throwable throwable);
    void log(Level level, String message);
    void log(Level level, String message, Throwable throwable);

    default void trace(String message) {
        log(Level.TRACE, message);
    }

    default void trace(String message, Throwable throwable) {
        log(Level.TRACE, message, throwable);
    }

    default void debug(String message) {
        log(Level.DEBUG, message);
    }

    default void debug(String message, Throwable throwable) {
        log(Level.DEBUG, message, throwable);
    }

    default void info(String message) {
        log(Level.INFO, message);
    }

    default void info(String message, Throwable throwable) {
        log(Level.INFO, message, throwable);
    }

    default void warn(String message) {
        log(Level.WARN, message);
    }

    default void warn(String message, Throwable throwable) {
        log(Level.WARN, message, throwable);
    }

    default void error(String message) {
        log(Level.ERROR, message);
    }

    default void error(String message, Throwable throwable) {
        log(Level.ERROR, message, throwable);
    }

    Logger addPrefix(String prefix);
}
