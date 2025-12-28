package com.inotsleep.utils.hooks.holograms;

import eu.decentsoftware.holograms.api.DHAPI;
import com.inotsleep.utils.BukkitPlugin;
import com.inotsleep.utils.hooks.Initializer;
import org.bukkit.Location;

import java.util.List;
import java.util.UUID;

public class DecentHologramsHologramAPI implements HologramAPI {
    private final BukkitPlugin plugin;
    public DecentHologramsHologramAPI(BukkitPlugin plugin) {
        Initializer.callInitEvent(this);
        this.plugin = plugin;
    }

    @Override
    public Hologram createHologram(List<String> text, Location location) {
        return new DecentHologramsHologram(DHAPI.createHologram(plugin.getName() + "_" + UUID.randomUUID(), location, text));
    }

    @Override
    public String getPluginName() {
        return "DecentHolograms";
    }
}
