package com.inotsleep.utils.logging;

public class ApacheLog4JLogger implements Logger {
    private static final org.apache.logging.log4j.Level DEFAULT_LEVEL = org.apache.logging.log4j.Level.INFO;
    private final org.apache.logging.log4j.Logger logger;

    public ApacheLog4JLogger(Object logger) {
        this.logger = (org.apache.logging.log4j.Logger) logger;
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

    private org.apache.logging.log4j.Level convertLevel(Level level) {
        return switch (level) {
            case TRACE -> org.apache.logging.log4j.Level.TRACE;
            case DEBUG -> org.apache.logging.log4j.Level.DEBUG;
            case INFO -> org.apache.logging.log4j.Level.INFO;
            case WARN -> org.apache.logging.log4j.Level.WARN;
            case ERROR -> org.apache.logging.log4j.Level.ERROR;
        };

    }
}
