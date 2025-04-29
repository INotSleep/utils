package me.inotsleep.utils.hooks.holograms;

import me.inotsleep.utils.LoggerFactory;
import org.bukkit.Location;

import java.util.List;

public class NullHologramAPI implements HologramAPI {
    public NullHologramAPI() {
        LoggerFactory.getLogger().info("No hologram plugin found.");
    }

    @Override
    public Hologram createHologram(List<String> text, Location location) {
        LoggerFactory.getLogger().warning("No hologram plugin is loaded but plugin tryed to use HologramAPI");
        return new NullHologram();
    }

    @Override
    public String getPluginName() {
        return "NULL";
    }
}
