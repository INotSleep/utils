package me.inotsleep.utils;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

import java.lang.reflect.*;

public class Window implements Listener {
    public Inventory inventory;

    public Window(AbstractBukkitPlugin<?> plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void unregisterEvents() {
        for (Method declaredMethod : this.getClass().getDeclaredMethods()) {
            if (!declaredMethod.isAnnotationPresent(EventHandler.class)) continue;

            // Get parameters of this method
            for (Parameter parameter : declaredMethod.getParameters()) {
                if (!Event.class.isAssignableFrom(parameter.getType())) continue;

                // Get all fields of this Event class
                for (Method classMethod : parameter.getType().getDeclaredMethods()) {
                    if (!Modifier.isStatic(classMethod.getModifiers()) || classMethod.getReturnType() != HandlerList.class) continue;

                    try {
                        ((HandlerList) classMethod.invoke(null)).unregister(this);
                    } catch (InvocationTargetException | IllegalAccessException e) {
                        AbstractBukkitPlugin.getAbstractInstance().getLogger().warning("Cannot unregister event: " + e.getMessage());
                    }
                }
            }
        }
    }
}
