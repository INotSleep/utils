package com.inotsleep.utils.plugin;

public class INSUtilsAPICore {
    private static INSUtilsPlugin instance;

    public static INSUtilsPlugin getInstance() {
        return instance;
    }

    public static void setInstance(INSUtilsPlugin instance) {
        INSUtilsAPICore.instance = instance;
    }
}
