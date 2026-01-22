package com.inotsleep.insutils.internal.listeners;

import com.inotsleep.insutils.spi.plugin.BukkitPlugin;
import com.inotsleep.insutils.api.events.HookInitEvent;
import com.inotsleep.insutils.api.logging.LoggingManager;
import com.inotsleep.insutils.internal.hooks.holograms.HologramAPIHolderImpl;
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
        HologramAPIHolderImpl.checkPlugin(plugin, event.getPlugin());
    }

    @EventHandler
    public void onHookInit(HookInitEvent event) {
        LoggingManager.info("Initialized hook " + event.getHook().getName() + (event.getHook().getPluginName() == null ? "" : " with plugin " + event.getHook().getPluginName()));
    }
}
