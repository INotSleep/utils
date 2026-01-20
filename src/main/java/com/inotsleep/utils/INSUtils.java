package com.inotsleep.utils;

import com.inotsleep.utils.plugin.BukkitPlugin;
import com.inotsleep.utils.plugin.BungeePlugin;

import java.io.File;
import java.util.List;

public interface INSUtils {
    void register(BukkitPlugin plugin);
    void register(BungeePlugin plugin);

    List<BukkitPlugin> getBukkitPlugins();
    List<BungeePlugin> getBungeePlugins();

    File getDataFolder();
}
