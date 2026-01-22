package com.inotsleep.insutils.internal.logging;


import com.inotsleep.insutils.api.logging.Level;
import com.inotsleep.insutils.api.logging.Logger;
import com.inotsleep.insutils.api.logging.PrefixedLogger;

public class JavaLogger implements Logger {
    private static final java.util.logging.Level DEFAULT_LEVEL = java.util.logging.Level.INFO;
    private final java.util.logging.Logger logger;

    public JavaLogger(java.util.logging.Logger logger) {
        this.logger = logger;
    }

    @Override
    public void log(String message) {
        logger.log(DEFAULT_LEVEL, message);
    }

    @Override
    public void log(String message, Throwable throwable) {
        logger.log(DEFAULT_LEVEL, message, throwable);
    }

    @Override
    public void log(Level level, String message) {
        logger.log(convertLevel(level), message);
    }

    @Override
    public void log(Level level, String message, Throwable throwable) {
        logger.log(convertLevel(level), message, throwable);
    }

    @Override
    public Logger addPrefix(String prefix) {
        return new PrefixedLogger(this, prefix);
    }

    private java.util.logging.Level convertLevel(Level level) {
        return switch (level) {
            case TRACE -> java.util.logging.Level.FINE;
            case DEBUG -> java.util.logging.Level.CONFIG;
            case INFO -> java.util.logging.Level.INFO;
            case WARN -> java.util.logging.Level.WARNING;
            case ERROR -> java.util.logging.Level.SEVERE;
        };

    }
}
