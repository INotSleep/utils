package com.inotsleep.utils.plugin;

import com.inotsleep.utils.i18n.I18nConsumer;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;

public interface BukkitPlugin extends I18nConsumer {
    CommandMap getCommandMap();

    String getName();

    @NotNull JavaPlugin getPlugin();

    InputStream getResource(String location);
}
