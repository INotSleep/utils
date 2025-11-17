package com.inotsleep.utils.hooks.holograms;

import com.inotsleep.utils.logging.LoggingManager;
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
