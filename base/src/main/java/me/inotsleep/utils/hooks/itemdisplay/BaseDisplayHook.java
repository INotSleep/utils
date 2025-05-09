package me.inotsleep.utils.hooks.itemdisplay;

import me.inotsleep.utils.AbstractPlugin;
import me.inotsleep.utils.hooks.base.BaseHook;
import me.inotsleep.utils.logging.LoggingManager;
import org.bukkit.Location;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicReference;

public interface BaseDisplayHook extends BaseHook {
    AtomicReference<BaseDisplayHook> instance = new AtomicReference<>();

    static void init(boolean supported) {
        try {
            if (supported) instance.set((BaseDisplayHook) AbstractPlugin.getAbstractInstance().getClass().getClassLoader().loadClass("me.inotsleep.utils.hooks.itemdisplay.DisplayHook").getConstructor().newInstance());
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            LoggingManager.error("Unable to load class of DisplayHook.", e);
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
