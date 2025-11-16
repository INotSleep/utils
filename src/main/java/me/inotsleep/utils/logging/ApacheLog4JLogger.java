package me.inotsleep.utils.logging;

import org.apache.logging.log4j.Logger;

public class ApacheLog4JLogger implements ILogger {
    private static final org.apache.logging.log4j.Level DEFAULT_LEVEL = org.apache.logging.log4j.Level.INFO;
    private final Logger logger;

    public ApacheLog4JLogger(Object logger) {
        this.logger = (Logger) logger;
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

    private org.apache.logging.log4j.Level convertLevel(Level level) {
        switch (level) {
            case TRACE:
                return org.apache.logging.log4j.Level.TRACE;
            case DEBUG:
                return org.apache.logging.log4j.Level.DEBUG;
            case INFO:
                return org.apache.logging.log4j.Level.INFO;
            case WARN:
                return org.apache.logging.log4j.Level.WARN;
            case ERROR:
                return org.apache.logging.log4j.Level.ERROR;
        }

        return DEFAULT_LEVEL;
    }
}
