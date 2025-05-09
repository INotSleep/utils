package me.inotsleep.utils;

import me.inotsleep.utils.logging.ILogger;
import me.inotsleep.utils.logging.LoggingManager;

import java.util.logging.Logger;

@Deprecated
public class LoggerFactory {
    public static ILogger getLogger() {
        return LoggingManager.getLogger();
    }

    public static void setLogger(Logger logger) {
        LoggingManager.setLogger(logger);
    }
}
