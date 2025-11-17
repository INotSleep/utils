package com.inotsleep.utils;

import com.inotsleep.utils.hooks.Initializer;
import com.inotsleep.utils.listeners.EventListener;
import com.inotsleep.utils.logging.LoggingManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class AbstractBukkitPlugin extends JavaPlugin {
    public static CommandMap commandMap;
    public static Metrics metrics;

    @Override
    public void onEnable() {
        LoggingManager.setLogger(getLogger());

        commandMap = Bukkit.getCommandMap();

        Bukkit.getPluginManager().registerEvents(new EventListener(this), this);
        Initializer.tryToInitialize();
        doEnable();
    }

    @Override
    public void onDisable() {
        doDisable();
    }

    public abstract void doEnable();
    public abstract void doDisable();

    public void setMetrics(int id) {
        metrics = new Metrics(this, id);
    }
}
