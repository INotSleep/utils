package com.inotsleep.insutils.api;

import com.inotsleep.insutils.api.config.INSUtilsConfig;
import com.inotsleep.insutils.api.plugin.INSBukkitPlugin;
import com.inotsleep.insutils.api.plugin.INSBungeePlugin;
import com.inotsleep.insutils.api.service.ServiceManager;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executor;

public interface INSUtils {
    static INSUtils getInstance() {
        return ServiceManager.get(INSUtils.class);
    }

    static void setInstance(INSUtils instance) {
        ServiceManager.register(INSUtils.class, instance);
    }

    void register(INSBukkitPlugin plugin);
    void register(INSBungeePlugin plugin);

    List<INSBukkitPlugin> getBukkitPlugins();
    List<INSBungeePlugin> getBungeePlugins();

    File getDataFolder();
    Executor getExecutor();

    INSUtilsConfig getINSUtilsConfig();
}
