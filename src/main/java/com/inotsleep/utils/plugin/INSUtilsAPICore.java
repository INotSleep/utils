package com.inotsleep.utils.plugin;

import com.inotsleep.utils.INSUtils;

public class INSUtilsAPICore {
    private static INSUtils instance;

    public static INSUtils getInstance() {
        return instance;
    }

    public static void setInstance(INSUtils instance) {
        INSUtilsAPICore.instance = instance;
    }
}
