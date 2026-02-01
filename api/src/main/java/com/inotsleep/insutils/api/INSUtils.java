package com.inotsleep.insutils.api;

import com.inotsleep.insutils.api.config.INSUtilsConfig;
import com.inotsleep.insutils.api.plugin.INSBukkitPlugin;
import com.inotsleep.insutils.api.plugin.INSBungeePlugin;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;

public interface INSUtils {
    AtomicReference<INSUtils> instance = new AtomicReference<>();
    static INSUtils getInstance() {
        return instance.get();
    }

    static void setInstance(INSUtils instance) {
        if (instance == null) {
            throw new NullPointerException("instance");
        }
        if (!INSUtils.instance.compareAndSet(null, instance)) {
            throw new IllegalStateException("INSUtils instance already set");
        }
    }

    void register(INSBukkitPlugin plugin);
    void register(INSBungeePlugin plugin);

    List<INSBukkitPlugin> getBukkitPlugins();
    List<INSBungeePlugin> getBungeePlugins();

    File getDataFolder();
    Executor getExecutor();

    INSUtilsConfig getINSUtilsConfig();
}
