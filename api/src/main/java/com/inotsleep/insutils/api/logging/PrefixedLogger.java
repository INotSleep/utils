package com.inotsleep.insutils.api.logging;

public class PrefixedLogger implements Logger {
    Logger originalLogger;
    String prefix;

    public PrefixedLogger(Logger originalLogger, String prefix) {
        this.originalLogger = originalLogger;
        this.prefix = prefix;
    }

    @Override
    public void log(String message) {
        originalLogger.log(prefix + message);
    }

    @Override
    public void log(String message, Throwable throwable) {
        originalLogger.log(prefix + message, throwable);
    }

    @Override
    public void log(Level level, String message) {
        originalLogger.log(level, prefix + message);
    }

    @Override
    public void log(Level level, String message, Throwable throwable) {
        originalLogger.log(level, prefix + message, throwable);
    }

    @Override
    public Logger addPrefix(String prefix) {
        return new PrefixedLogger(this, this.prefix + prefix);
    }
}
