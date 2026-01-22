package com.inotsleep.insutils.internal.hooks.holograms;

import com.inotsleep.insutils.api.hooks.holograms.HologramAPI;
import com.inotsleep.insutils.api.hooks.holograms.HologramAPIHolder;
import com.inotsleep.insutils.api.logging.LoggingManager;
import com.inotsleep.insutils.spi.plugin.BukkitPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.atomic.AtomicReference;

public class HologramAPIHolderImpl implements HologramAPIHolder {
    public static AtomicReference<HologramAPI> instance = new AtomicReference<>(null);

    public static void init(BukkitPlugin plugin) {
        HologramAPIHolder.setInstance(new  HologramAPIHolderImpl());
        LoggingManager.info("Initializing HologramAPI");

        if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) instance.set(new HolographicDisplaysHologramAPI(plugin));
        else if (Bukkit.getPluginManager().isPluginEnabled("DecentHolograms")) instance.set(new DecentHologramsHologramAPI(plugin));
        else instance.set(new NullHologramAPI());
    }

    public static void checkPlugin(BukkitPlugin plugin, Plugin toCheck) {
        if (instance.get() == null) return;
        if (toCheck.getName().equals("HolographicDisplays") || toCheck.getName().equals("DecentHolograms")) init(plugin);
    }

    @Override
    public HologramAPI getHologramAPI() {
        return instance.get();
    }
}
