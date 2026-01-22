package com.inotsleep.insutils.internal.hooks.holograms;

import com.inotsleep.insutils.api.hooks.holograms.Hologram;
import com.inotsleep.insutils.api.hooks.holograms.HologramAPI;
import eu.decentsoftware.holograms.api.DHAPI;
import com.inotsleep.insutils.api.plugin.INSBukkitPlugin;
import com.inotsleep.insutils.internal.hooks.Initializer;
import org.bukkit.Location;

import java.util.List;
import java.util.UUID;

public class DecentHologramsHologramAPI implements HologramAPI {
    private final INSBukkitPlugin plugin;
    public DecentHologramsHologramAPI(INSBukkitPlugin plugin) {
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
