package com.inotsleep.insutils.api.logging;

import java.util.concurrent.atomic.AtomicReference;

public interface LoggingManager {
    AtomicReference<LoggingManager> instance = new AtomicReference<>();

    static void setInstance(LoggingManager instance) {
        LoggingManager.instance.set(instance);
    }
    static LoggingManager getInstance() {
        return instance.get();
    }

    void setLogger(Logger logger);
    void setLogger(final Object logger);

    Logger getLogger();

    static void log(String message) {

        Logger logger = getInstance().getLogger();
        if (logger == null) return;

        logger.log(message);
    }

    static void log(String message, Throwable throwable) {
        Logger logger = getInstance().getLogger();
        if (logger == null) return;

        logger.log(message, throwable);
    }

    static void log(Level level, String message) {
        Logger logger = getInstance().getLogger();
        if (logger == null) return;

        logger.log(level, message);
    }

    static void log(Level level, String message, Throwable throwable) {
        Logger logger = getInstance().getLogger();
        if (logger == null) return;

        logger.log(level, message, throwable);
    }

    static void trace(String message) {
        log(Level.TRACE, message);
    }

    static void trace(String message, Throwable throwable) {
        log(Level.TRACE, message, throwable);
    }

    static void debug(String message) {
        log(Level.DEBUG, message);
    }

    static void debug(String message, Throwable throwable) {
        log(Level.DEBUG, message, throwable);
    }

    static void info(String message) {
        log(Level.INFO, message);
    }

    static void info(String message, Throwable throwable) {
        log(Level.INFO, message, throwable);
    }

    static void warn(String message) {
        log(Level.WARN, message);
    }

    static void warn(String message, Throwable throwable) {
        log(Level.WARN, message, throwable);
    }

    static void error(String message) {
        log(Level.ERROR, message);
    }

    static void error(String message, Throwable throwable) {
        log(Level.ERROR, message, throwable);
    }

    static Logger getPrefixedLogger(String prefix) {
        Logger logger = getInstance().getLogger();
        if (logger == null) return null;

        return new PrefixedLogger(logger, prefix);
    }
}
