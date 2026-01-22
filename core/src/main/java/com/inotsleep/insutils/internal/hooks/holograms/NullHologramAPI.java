package com.inotsleep.insutils.internal.hooks.holograms;

import com.inotsleep.insutils.api.hooks.holograms.Hologram;
import com.inotsleep.insutils.api.hooks.holograms.HologramAPI;
import com.inotsleep.insutils.api.logging.LoggingManager;
import org.bukkit.Location;

import java.util.List;

public class NullHologramAPI implements HologramAPI {
    public NullHologramAPI() {
        LoggingManager.warn("No hologram plugin found.");
    }

    @Override
    public Hologram createHologram(List<String> text, Location location) {
        LoggingManager.warn("No hologram plugin is loaded but plugin tryed to use HologramAPI");
        return new NullHologram();
    }

    @Override
    public String getPluginName() {
        return "NULL";
    }
}
