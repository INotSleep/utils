package me.inotsleep.utils;

import me.inotsleep.utils.hooks.Initializer;
import me.inotsleep.utils.hooks.holograms.HologramAPI;
import me.inotsleep.utils.listeners.EventListener;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public abstract class AbstractPlugin<T extends AbstractPlugin<T>> extends JavaPlugin {
    private static AbstractPlugin<?> instance;
    public static CommandMap commandMap;
    private static HologramAPI hologramAPI;
    public static Metrics metrics;

    @Override
    public void onEnable() {
        LoggerFactory.setLogger(getLogger());
        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            AbstractPlugin.commandMap = (CommandMap)bukkitCommandMap.get(Bukkit.getServer());
        }
        catch (final IllegalAccessException | NoSuchFieldException e) {
            this.getLogger().severe("Cannot access CommandMap. Contact me in github!");
            e.printStackTrace();
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
    public static void printError(String string, boolean disable) {
        LoggerFactory.getLogger().severe(string);
        if (disable) disablePlugin();
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

    public static AbstractPlugin<?> getAbstractInstance() {
        return instance;
    }

    public static <T extends AbstractPlugin<T>> T getInstanceByClazz(Class<T> clazz) {
        return (T) instance;
    }
}
