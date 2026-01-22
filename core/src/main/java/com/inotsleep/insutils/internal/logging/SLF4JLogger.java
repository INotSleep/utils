package com.inotsleep.insutils.internal.logging;

import com.inotsleep.insutils.api.logging.Level;
import com.inotsleep.insutils.api.logging.Logger;
import com.inotsleep.insutils.api.logging.PrefixedLogger;

public class SLF4JLogger implements Logger {
    private static final Level DEFAULT_LEVEL = Level.INFO;
    private final org.slf4j.Logger logger;

    public SLF4JLogger(Object logger) {
        this.logger = (org.slf4j.Logger) logger;
    }

    @Override
    public void log(String message) {
        log(DEFAULT_LEVEL, message);
    }

    @Override
    public void log(String message, Throwable throwable) {
        log(DEFAULT_LEVEL, message, throwable);
    }

    @Override
    public void log(Level level, String message) {
        switch (level) {
            case TRACE:
                logger.trace(message);
                break;
            case DEBUG:
                logger.debug(message);
                break;
            case INFO:
                logger.info(message);
                break;
            case WARN:
                logger.warn(message);
                break;
            case ERROR:
                logger.error(message);
                break;
        }
    }

    @Override
    public void log(Level level, String message, Throwable throwable) {
        switch (level) {
            case TRACE:
                logger.trace(message, throwable);
                break;
            case DEBUG:
                logger.debug(message, throwable);
                break;
            case INFO:
                logger.info(message, throwable);
                break;
            case WARN:
                logger.warn(message, throwable);
                break;
            case ERROR:
                logger.error(message, throwable);
                break;
        }
    }

    @Override
    public void trace(String message) {
        logger.trace(message);
    }

    @Override
    public void trace(String message, Throwable throwable) {
        logger.trace(message, throwable);
    }

    @Override
    public void debug(String message) {
        logger.debug(message);
    }

    @Override
    public void debug(String message, Throwable throwable) {
        logger.debug(message, throwable);
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void info(String message, Throwable throwable) {
        logger.info(message, throwable);
    }

    @Override
    public void warn(String message) {
        logger.warn(message);
    }

    @Override
    public void error(String message) {
        logger.error(message);
    }

    @Override
    public void warn(String message, Throwable throwable) {
        logger.warn(message, throwable);
    }

    @Override
    public void error(String message, Throwable throwable) {
        logger.error(message, throwable);
    }

    @Override
    public Logger addPrefix(String prefix) {
        return new PrefixedLogger(this, prefix);
    }
}
