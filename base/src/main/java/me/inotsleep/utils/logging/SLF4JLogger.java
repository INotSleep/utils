package me.inotsleep.utils.logging;

import org.slf4j.Logger;

public class SLF4JLogger implements ILogger{
    private static final Level DEFAULT_LEVEL = Level.INFO;
    private final Logger logger;

    public SLF4JLogger(Logger logger) {
        this.logger = logger;
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
            case DEBUG:
                logger.debug(message);
            case INFO:
                logger.info(message);
            case WARN:
                logger.warn(message);
            case ERROR:
                logger.error(message);
        }
    }

    @Override
    public void log(Level level, String message, Throwable throwable) {
        switch (level) {
            case TRACE:
                logger.trace(message, throwable);
            case DEBUG:
                logger.debug(message, throwable);
            case INFO:
                logger.info(message, throwable);
            case WARN:
                logger.warn(message, throwable);
            case ERROR:
                logger.error(message, throwable);
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
}
