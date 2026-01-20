package com.inotsleep.utils.plugin;

import com.inotsleep.utils.INSUtils;
import com.inotsleep.utils.hooks.Initializer;
import com.inotsleep.utils.i18n.I18n;
import com.inotsleep.utils.listeners.EventListener;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class INSUtilsAPIBukkitPlugin extends BukkitPluginImpl implements INSUtils {
    private List<BukkitPlugin> bukkitPlugins;

    private static INSUtilsAPIBukkitPlugin instance;
    public static INSUtilsAPIBukkitPlugin getInstance() {
        return instance;
    }

    @Override
    public void doEnable() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(new EventListener(this), this);
        Initializer.tryToInitialize();

        INSUtilsAPICore.setInstance(this);
        bukkitPlugins = new ArrayList<>();

        new BukkitRunnable() {
            @Override
            public void run() {
                I18n.init(getInstance());
                I18n.getInstance().registerConsumer(getInstance());
            }
        }
        .runTaskAsynchronously(this);
    }

    @Override
    public void doDisable() {
        I18n.getInstance().shutdown();
    }

    @Override
    public CommandMap getCommandMap() {
        return commandMap;
    }

    @Override
    public @NotNull JavaPlugin getPlugin() {
        return this;
    }

    @Override
    public void register(BukkitPlugin plugin) {
        if (bukkitPlugins == null) return; // Assuming that registered plugin is this

        bukkitPlugins.add(plugin);
        I18n.getInstance().registerConsumer(plugin);
    }

    @Override
    public void register(BungeePlugin plugin) {
        throw new UnsupportedOperationException("Bungee plugins cannot be loaded in Bukkit environment");
    }

    @Override
    public List<BukkitPlugin> getBukkitPlugins() {
        return bukkitPlugins;
    }

    @Override
    public List<BungeePlugin> getBungeePlugins() {
        return List.of();
    }
}
