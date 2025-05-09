package me.inotsleep.utils.listeners;

import me.inotsleep.utils.events.HookInitEvent;
import me.inotsleep.utils.hooks.holograms.HologramAPI;
import me.inotsleep.utils.logging.LoggingManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;

public class EventListener implements Listener {
    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        HologramAPI.checkPlugin(event.getPlugin());
    }

    @EventHandler
    public void onHookInit(HookInitEvent event) {
        LoggingManager.info("Initialized hook " + event.getHook().getName() + (event.getHook().getPluginName() == null ? "" : " with plugin " + event.getHook().getPluginName()));
    }
}
