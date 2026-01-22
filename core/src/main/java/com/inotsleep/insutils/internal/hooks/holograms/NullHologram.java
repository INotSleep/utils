package com.inotsleep.insutils.internal.hooks.holograms;

import com.inotsleep.insutils.api.hooks.holograms.Hologram;
import com.inotsleep.insutils.api.logging.LoggingManager;
import org.bukkit.Location;

import java.util.List;

public class NullHologram implements Hologram {
    @Override
    public void delete() {
        LoggingManager.warn("No hologram plugin is loaded but plugin tried to use HologramAPI");
    }

    @Override
    public void edit(List<String> text, Location location) {
        LoggingManager.warn("No hologram plugin is loaded but plugin tried to use HologramAPI");
    }

    @Override
    public void editLines(List<String> text) {
        LoggingManager.warn("No hologram plugin is loaded but plugin tried to use HologramAPI");
    }

    @Override
    public void move(Location location) {
        LoggingManager.warn("No hologram plugin is loaded but plugin tried to use HologramAPI");
    }
}
