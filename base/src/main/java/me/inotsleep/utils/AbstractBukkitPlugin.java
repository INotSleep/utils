package me.inotsleep.utils;

import me.inotsleep.utils.hooks.Initializer;
import me.inotsleep.utils.listeners.EventListener;
import me.inotsleep.utils.logging.LoggingManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public abstract class AbstractBukkitPlugin<T extends AbstractBukkitPlugin<T>> extends JavaPlugin {
    private static AbstractBukkitPlugin<?> instance;
    public static CommandMap commandMap;
    public static Metrics metrics;

    @Override
    public void onEnable() {
        LoggingManager.setLogger(getLogger());
        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            AbstractBukkitPlugin.commandMap = (CommandMap)bukkitCommandMap.get(Bukkit.getServer());
        }
        catch (final IllegalAccessException | NoSuchFieldException e) {
            LoggingManager.error("Failed to get command map.", e);
            Bukkit.getPluginManager().disablePlugin(this);
        }
        instance = this;
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        Initializer.tryToInitialize();
        doEnable();
    }

    public static void disablePlugin() {
        Bukkit.getPluginManager().disablePlugin(instance);
    }

    @Override
    public void onDisable() {
        doDisable();
    }

    public abstract void doDisable();
    public abstract void doEnable();

    public void setMetrics(int id) {
        metrics = new Metrics(this, id);
    }

    public static AbstractBukkitPlugin<?> getAbstractInstance() {
        return instance;
    }

    public static <T extends AbstractBukkitPlugin<T>> T getInstanceByClazz(Class<T> clazz) {
        return (T) instance;
    }
}
