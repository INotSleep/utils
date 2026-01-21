package com.inotsleep.utils.listeners;

import com.inotsleep.utils.plugin.BukkitPlugin;
import com.inotsleep.utils.events.HookInitEvent;
import com.inotsleep.utils.hooks.holograms.HologramAPI;
import com.inotsleep.utils.logging.LoggingManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;

public class EventListener implements Listener {
    private final BukkitPlugin plugin;
    public EventListener(BukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        HologramAPI.checkPlugin(plugin, event.getPlugin());
    }

    @EventHandler
    public void onHookInit(HookInitEvent event) {
        LoggingManager.info("Initialized hook " + event.getHook().getName() + (event.getHook().getPluginName() == null ? "" : " with plugin " + event.getHook().getPluginName()));
    }
}
