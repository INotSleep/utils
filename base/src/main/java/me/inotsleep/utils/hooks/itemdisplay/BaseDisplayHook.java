package me.inotsleep.utils.hooks.itemdisplay;

import me.inotsleep.utils.AbstractPlugin;
import me.inotsleep.utils.LoggerFactory;
import me.inotsleep.utils.hooks.base.BaseHook;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

public interface BaseDisplayHook extends BaseHook {
    AtomicReference<BaseDisplayHook> instance = new AtomicReference<>();

    static void init(boolean supported) {
        try {
            if (supported) instance.set((BaseDisplayHook) AbstractPlugin.getAbstractInstance().getClass().getClassLoader().loadClass("me.inotsleep.utils.hooks.itemdisplay.DisplayHook").getConstructor().newInstance());
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            LoggerFactory.getLogger().log(Level.SEVERE, "Unable to load class of DisplayHook. Is plugin corrupt?", e);
        }
        if (instance.get() == null) instance.set(new UnsupportedDisplayHook());
    }




    BaseDisplayWrapper create(Location location, boolean isBlockDisplay);

    default String getName() {
        return "Holograms Hook";
    }

    default String getPluginName() {
        return null;
    }
}
